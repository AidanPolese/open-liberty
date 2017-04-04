package com.ibm.tx.jta.impl;
/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2009 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*                                                                                                       */
/*  Date      Programmer    Defect   Description                                                         */
/*  --------  ----------    ------   -----------                                                         */
/*  05/09/02   gareth       ------   Move to JTA implementation                                          */
/*  21/10/02   hursdlg      1468     Allow public access to syncs for JTS                                */
/*  21/10/02   gareth       1449     Tidy up messages and exceptions                                     */
/*  17/12/02   awilkins  LIDB1673.12 Implement hooks into LI850 code                                     */
/*  22/01/03   gareth     LIDB1673.1 Add JTA2 messages                                                   */
/*  21/02/03   gareth    LIDB1673.19  Make any unextended code final                                     */
/*  28/02/03   hursdlg   LIDB1673.19.1 Use ArrayList instead of Vector                                   */
/*  01/04/03   mallam      162354    Call syncs added during distributeBefore                            */
/*  24/11/03   awilkins    183479    Synchronization tiers                                               */
/*  12/03/04   hursdlg     194240    NPE check and RRS order                                             */
/*  27/04/04   johawkes    196302    Implement sync levels                                               */
/*  26/04/04   dmatthew    LIDB1922  Initial WSAT support                                                */
/*  28/05/04   dmatthew    206203    WSAT synchronization                                                */
/*  03/08/04   dmatthew    201906    WSAT code review                                                    */
/*  21/06/05   johawkes    284612    Call extended JTA transaction syncs earlier                         */
/*  29/06/05   johawkes    266145.3  Move custom properties into WCCM                                    */
/*  07/07/05   johawkes    289186    Put back PK05365                                                    */
/*  12/07/05   hursdlg     281425    AfterCompletion status of unknown                                   */
/*  06/09/05   hursdlg     287100    Add back recursion checks as in v5                                  */
/*  06/01/06   johawkes    306998.12 Use TraceComponent.isAnyTracingEnabled()                            */
/*  16/03/06   brailsfo    PK19059   Pass back original exception                                        */
/*  24/04/06   johawkes    364694    Integer.getInteger() needs wrapping in doPrivileged apparently      */
/*  01/06/06   kaczyns     367977    Syncs can access DB2 therefore need threadSwitch call during them   */
/*  06/11/29   maples      402670    LI4119-19 code review changes                                       */
/*  19/04/07  mallam      432276      Migrate PK41846 to WASX                                            */
// 01/05/07   johawkes    434414    Remove WAS dependencies
// 06/06/07   johawkes    443467     Moved
// 17/06/07   johawkes    444613     Repackaging
// 26/06/07   johawkes    446894     Fix JTM shutdown delay
// 16/08/07   johawkes    451213     Move LPS back into JTM
// 08-07-14   mallam      523634     EJB3 exception behaviour on compensateOnly  
// 09-06-02   mallam      596067     package move
// 09-11-16   johawkes    F743-305.1 EJB 3.1
/* ***************************************************************************************************** */

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import com.ibm.tx.TranConstants;
import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.JTA.Util;

/**
 * The RegisteredSyncs class provides operations that manage a set of
 * Synchronization objects involved in a transaction. In order to avoid
 * sending multiple synchronization requests to the same resource we require
 * some way to perform Synchronization reference comparisons.
 */
public class RegisteredSyncs
{
    private static final TraceComponent tc = Tr.register(RegisteredSyncs.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /** A Synchronization registered for the outer tier will be driven
     *  before normal beforeCompletion processing and after normal
     *  afterCompletion processing.
     */
    public static final int SYNC_TIER_OUTER = 0;

    /** A Synchronization registered for the normal tier will be driven
     *  normally for both before and after completion processing. It is
     *  equivalent to registering the sync without specifying a tier.
     */
    public static final int SYNC_TIER_NORMAL = 1;

    /** A Synchronization registered for the inner tier will be driven
     *  after normal beforeCompletion processing and before normal
     *  afterCompletion processing.
     */
    public static final int SYNC_TIER_INNER = 2;

    /** A Synchronization registered for the RRS tier will be driven after
     *  inner tier synchronizations during beforeCompletion processing and
     *  before inner tier synchronizations during afterCompletion
     *  processing.  
     */
    public static final int SYNC_TIER_RRS = 3;
        

    protected final TransactionImpl _tran;
    
    // Four ArrayLists; one for each tier of synchronizations. The four
    // sychronization tiers are inner, normal, outer, and RRS. The tier
    // that was specified when a Synchronization was added controls when
    // it will be driven during both before and after completion
    // processing in relation to other synchronizations in other tiers.
    //
    // The ordering is as follows:
    //
    // Outer syncs   
    // Normal syncs  
    // Inner syncs    
    // RRS syncs
    //
    // Completion
    //
    // RRS syncs
    // Inner syncs
    // Normal syncs
    // Outer syncs
    //

    final static int SYNC_ARRAY_SIZE = SYNC_TIER_RRS + 1;  // RRS should always be last

    protected final List[] _syncs = new ArrayList[SYNC_ARRAY_SIZE];

    final static int DEFAULT_DEPTH_LIMIT = 5; // @287100A

    protected final static int _depthLimit;
    
    static
    {
        Integer depthLimit;

        try
        {
            depthLimit = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Integer>()
                    {
                        public Integer run()
                        {
                            return Integer.getInteger("com.ibm.ws.Transaction.JTA.beforeCompletionDepthLimit", DEFAULT_DEPTH_LIMIT);
                        }
                    }
            );
        }
        catch(PrivilegedActionException e)
        {
            FFDCFilter.processException(e, "com.ibm.ws.Transaction.JTA.RegisteredSyncs.<clinit>", "132");
            if (tc.isDebugEnabled()) Tr.debug(tc, "Exception setting depth limit", e);
            depthLimit = null;
        }

        _depthLimit = depthLimit != null ? depthLimit.intValue() : DEFAULT_DEPTH_LIMIT;
        if (tc.isEntryEnabled()) Tr.entry(tc, "beforeCompletion depth limit: " + _depthLimit);
    }

    /**
     * Default RegisteredSyncs constructor.
     *
     * Class extends ArrayList in preference to Vector.  Even though a transaction
     * may migrate between threads, this class should never be active on more than
     * one thread at a time.
     */
    protected RegisteredSyncs(TransactionImpl tran)
    {
        if (tc.isDebugEnabled()) Tr.debug(tc, "RegisteredSyncs", tran);
        _tran = tran;
    }

    /**
     * Distributes before completion operations to all registered Synchronization
     * objects.   If a synchronization raises an exception, mark transaction
     * for rollback.
     * 
     */
    public void distributeBefore()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "distributeBefore", this);
        boolean setRollback = false;

        try
        {
            coreDistributeBefore();
        }
        catch (Throwable exc)
        {
            // No FFDC Code Needed.
            Tr.error(tc, "WTRN0074_SYNCHRONIZATION_EXCEPTION", new Object[] {"before_completion", exc});
            // PK19059 starts here
            _tran.setOriginalException(exc);
            // PK19059 ends here
            setRollback = true;           
        }

        // Finally issue the RRS syncs - z/OS always issues these even if RBO has occurred
        // during previous syncs.  Need to check with Matt if we need to do these even if the
        // overall transaction is set to RBO as we bypass distributeBefore in this case.
        final List RRSsyncs = _syncs[SYNC_TIER_RRS];
                
        if (RRSsyncs != null)
        {       
            for (int j = 0; j < RRSsyncs.size(); j++ )  // d162354 array could grow
            {
                final Synchronization sync = (Synchronization)RRSsyncs.get(j);
       
                if (tc.isEventEnabled()) Tr.event(tc, "driving RRS before sync[" + j + "]", Util.identity(sync));

                try
                {
                    sync.beforeCompletion();
                }
                catch (Throwable exc)
                {
                    // No FFDC Code Needed.
                    Tr.error(tc, "WTRN0074_SYNCHRONIZATION_EXCEPTION", new Object[] {"before_completion", exc});
                    setRollback = true;           
                }      
            }                

            // If RRS syncs, one may be DB2 type 2, so issue thread switch
            // NativeJDBCDriverHelper.threadSwitch();              /* @367977A*/
        }

        //----------------------------------------------------------
        // If we've encountered an error, try to set rollback only
        //----------------------------------------------------------
        if (setRollback && _tran != null)
        {
            try
            {
                _tran.setRollbackOnly();
            }
            catch (Exception ex)
            {
                if (tc.isDebugEnabled()) Tr.debug(tc, "setRollbackOnly raised exception", ex);
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "distributeBefore");
    }
    
    protected void coreDistributeBefore()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "coreDistributeBefore");

        // Iterate through the array forwards so that
        // the syncs are driven in the tier order 
        // outer, then normal, then inner.  Treat RRS separately
        // after the asynchronous sync completions as RRS needs to be as 
        // close to the tran completion as possible.
        for (int i = 0; i < _syncs.length - 1; i++)
        {
            final List syncs = _syncs[i];

            if (syncs != null)
            {       
                // d287100 - container syncs can invoke a new bean which adds new syncs.
                // Keep track of current last value to track new additions and trap possible recursions
                // rrs syncs should not need to check as any growth should be complete after this.
                int depth = 0;
                int currentLast = syncs.size();

                for (int j = 0; j < syncs.size(); j++ )  // d162354 array could grow
                {
                    if (j == currentLast)
                    {
                        depth++;
                        if (tc.isDebugEnabled()) Tr.debug(tc, "depth limit incremented to " + depth);
                        if (depth >= _depthLimit)
                        {
                            if (tc.isEventEnabled()) Tr.event(tc, "depth limit exceeded");
                            if (tc.isEntryEnabled()) Tr.exit(tc, "coreDistributeBefore");
                            throw new IndexOutOfBoundsException("Synchronization beforeCompletion limit exceeded");
                        }
                        currentLast = syncs.size();
                    }

                    final Synchronization sync = (Synchronization)syncs.get(j);
    
                    if (tc.isEventEnabled()) Tr.event(tc, "driving before sync[" + j + "]", Util.identity(sync));
    
                    sync.beforeCompletion();
                }                
            }
        } 

        if (tc.isEntryEnabled()) Tr.exit(tc, "coreDistributeBefore");
    }

    /**
     * Distributes after completion operations to all registered Synchronization
     * objects.
     * 
     * @param status Indicates whether the transaction committed.
     */
    public void distributeAfter(int status)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "distributeAfter", new Object[] { this, status});
        
        // Issue the RRS syncs first - these need to be as close to the completion as possible
        final List RRSsyncs = _syncs[SYNC_TIER_RRS];
                
        if (RRSsyncs != null)
        {       
            final int RRSstatus = (status == Status.STATUS_UNKNOWN ? Status.STATUS_COMMITTED : status); // @281425A
            for (int j = RRSsyncs.size(); --j >= 0;)
            {
                final Synchronization sync = (Synchronization)RRSsyncs.get(j);
    
                try
                {
                    if (tc.isEntryEnabled()) Tr.event(tc, "driving RRS after sync[" + j + "]", Util.identity(sync));
                    sync.afterCompletion(RRSstatus); // @281425C
                }
                catch (Throwable exc)
                {
                    // No FFDC Code Needed.
    
                    // Discard any exceptions at this point.
                    Tr.error(tc, "WTRN0074_SYNCHRONIZATION_EXCEPTION", new Object[] {"after_completion", exc});
                }
            }
        }
        
        coreDistributeAfter(status);

        if (tc.isEntryEnabled()) Tr.exit(tc, "distributeAfter");
    }

    protected void coreDistributeAfter(int status)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "coreDistributeAfter", status);

        // Iterate through the array backwards so that the syncs 
        // are driven in the tier order inner, then normal,
        // and lastly outer.
        for (int i = _syncs.length - 1; --i >= 0;)
        {
            final List syncs = _syncs[i];
            
            if (syncs != null)
            {
                for (int j = 0; j < syncs.size(); ++j)
                {
                    final Synchronization sync = (Synchronization)syncs.get(j);
        
                    try
                    {
                        if (tc.isEntryEnabled()) Tr.event(tc, "driving after sync[" + j + "]", Util.identity(sync));
                        sync.afterCompletion(status);
                    }
                    catch (Throwable exc)
                    {
                        if (distributeAfterException(exc))
                        {
                            if (tc.isDebugEnabled()) Tr.debug(tc, "RuntimeException ... will be ignored", exc);
                        }
                        else
                        {
                            // No FFDC Code Needed.
                            // Discard any exceptions at this point.
                            Tr.error(tc, "WTRN0074_SYNCHRONIZATION_EXCEPTION", new Object[] {"after_completion", exc});
                        }
                    }
                }
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "coreDistributeAfter");
    }

    // d523634: provide a mechanism so that tx.rollback can throw an exception if (cscope) resource
    //          throws a runtime exception (during aftercompletion).
    //  Subclasses should override this method and return 'true' to ignore the exception

    @SuppressWarnings("unused")
    protected boolean distributeAfterException(Throwable t)
    {
        return false;
    }


    protected void add(Synchronization sync, int tier)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "add", new Object[] {sync, tier});
        
        if (sync == null)
        {
            final String msg = "Synchronization object was null";
            final NullPointerException npe = new NullPointerException(msg);
            FFDCFilter.processException(
                npe,
                this.getClass().getName() + ".add",
                "223",
                this);
            if (tc.isEntryEnabled()) Tr.exit(tc, "add", npe);
            throw npe;
        }

        if (_syncs[tier] == null)
        {
            _syncs[tier] = new ArrayList();
        }

        _syncs[tier].add(sync);
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "add");
    }
}
