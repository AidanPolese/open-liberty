package com.ibm.tx.jta.impl;
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/*  ORIGINS: 27                                                                      */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2013 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  Date      Programmer    Defect   Description                                     */
/*  --------  ----------    ------   -----------                                     */
/*  05/09/02   gareth       ------   Move to JTA implementation                      */
/*  11/10/02   awilkins     1452     XAException handling                            */
/*  14/10/02   hursdlg      1454     Remove otid_t requirement                       */
/*  18/10/02   hursdlg      1433     Remove excess synchronizations                  */
/*  25/11/02   awilkins     1513     Repackage ejs.jts -> ws.Transaction             */
/*  20/01/03   gareth     LIDB1673.1 Add JTA2 messages                               */
/*  21/02/03   gareth    LIDB1673.19 Make any unextended code final                  */
/*  15/07/03   mallam       171151    Rollback using TMSUCCESS (TMFAIL on t/o)       */
/*  28/08/03   johawkes     174516   Distribute all ends before prepares             */
/*  18/09/03   johawkes     169114   Improve diags for prep/cmplt XA errors          */
/*  05/02/04   mallam       LIDB2775  Rename XID to XidImpl                          */
/*  18/02/04   hursdlg      LIDB2775  Update ctor                                    */
/*  19/04/04   johawkes     193919.1 New methods for adminconsole                    */
/*  15/06/04   johawkes     209345   Remove unnecessary code                         */
/*  20/09/04   hursdlg      233078   Log more information                            */
/*  14/08/07   johawkes     451213   Moved into JTM                                  */
/*  05/10/-5   awilkins     463184   Override hashCode                               */
/*  26/10/07   johawkes     463185   Static analysis                                 */
/*  04/06/08   hursdlg      515603   Allow forget as c1p may return heuristic        */
/*  17/07/08   johawkes     536926   Remove JET dependencies on org.omg classes      */
/*  02/06/09   mallam       596067   package move                                    */
/*  10/08/13   slaterpa     752004   TRANSUMMARY trace
/* ********************************************************************************* */

import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.*;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.JTA.JTAResourceBase;
import com.ibm.ws.Transaction.JTA.XAReturnCodeHelper;

/**
 * An implementation of org.omg.CosTransactions.Resource interface
 * to support special one-phase only resources, such as JDBC Connection.
 */
public class OnePhaseResourceImpl extends JTAResourceBase
{
    private static final TraceComponent tc = Tr.register(OnePhaseResourceImpl.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);
    private static final TraceComponent tcSummary = Tr.register("TRANSUMMARY", TranConstants.SUMMARY_TRACE_GROUP, null);

    /**
     * Construct an OnePhaseResourceImpl object.
     *
     * @param xid the global transaction identifier.
     */
    public OnePhaseResourceImpl(OnePhaseXAResource xares, Xid xid)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "OnePhaseResourceImpl", new java.lang.Object[] {xares, xid});
        _resource = xares;
        _xid      = xid;
        traceCreate();
        if (tc.isEntryEnabled()) Tr.exit(tc, "OnePhaseResourceImpl");
    }


    /**
     * Prepare a transaction.
     *
     * <p>This is the first phase of the two-phase commit protocol.
     *
     * @return
     * @exception XAException
     * @exception SystemException
     */
    public final int prepare() throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "prepare", _resource);

		// The underlying adapter OnePhaseXAResource will throw an
		// exception with FFDC specific to the wrappered RM.
        try
        {
            _resource.prepare(_xid);
        }
        finally
        {
            if (tc.isEntryEnabled()) Tr.exit(tc, "prepare");
        }

        // If the resource hasn't thrown its own
        // exception throw one here.
        throw new XAException(XAException.XA_RBPROTO);
    }


    /**
     * Commit a transaction.
     *
     * @exception NotSupportedException
     */
    public final void commit() throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "commit", _resource);

        Tr.warning(tc, "WTRN0018_1PC_RESOURCE_DOES_NOT_SUPPORT_COMMIT");
        if (tc.isEntryEnabled()) Tr.exit(tc, "commit");
        throw new XAException(XAException.XAER_PROTO);
    }


    /**
     * Commit a transaction, using one-phase optimization.
     *
     * @exception SystemException
     *                   an unindentified error has been reported
     *                   by the resource manager.
     * @exception RollbackException
     */
    public final void commit_one_phase() throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "commit_one_phase", _resource);
        if (tcSummary.isDebugEnabled()) Tr.debug(tcSummary, "commit_one_phase", this);

        //
        // Commit the one-phase resource.
        //
        try
        {
            _resource.commit(_xid, true);

			// Record the completion direction and Automatic vote.
			_completedCommit = true;
			_vote = JTAResourceVote.commit;
        }
        catch(XAException xae)
        {
			// Record the completion XA return code
			_completionXARC = xae.errorCode;

			throw xae;
        }
        finally
        {
            if (tc.isEntryEnabled()) Tr.exit(tc, "commit_one_phase", _completionXARC);
            if (tcSummary.isDebugEnabled()) Tr.debug(tcSummary, "commit_one_phase result: " + 
                                                                XAReturnCodeHelper.convertXACode(_completionXARC));
        }
    }

    /**
     * Rollback a transaction.
     *
     * @exception SystemException
     *                   an unindentified error has been reported
     *                   by the resource manager
     */
    public final void rollback() throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "rollback", _resource);
        if (tcSummary.isDebugEnabled()) Tr.debug(tcSummary, "rollback", this);

        try
        {
            _resource.rollback(_xid);

			// Record the vote.
			// _completedCommit defaults to false.
			_vote = JTAResourceVote.rollback;
        }
        catch(XAException xae)
        {
			// Record the completion XA return code
			_completionXARC = xae.errorCode;

			throw xae;
        }
        finally
        {
            if (tc.isEntryEnabled()) Tr.exit(tc, "rollback");
            if (tcSummary.isDebugEnabled()) Tr.debug(tcSummary, "rollback result: " + 
                                                                XAReturnCodeHelper.convertXACode(_completionXARC));
        }
    }


    /**
     * The resource manager can forget all knowledge of the transaction.
     * Only allowed because commit_one_phase may return heuristic
     */
    public final void forget() throws XAException
    {
        if (tc.isEntryEnabled())
        {
            Tr.entry(tc, "forget", _resource);
            Tr.exit(tc, "forget");
        }
    }

    public final void destroy()
    {
        if (tc.isEntryEnabled())
        {
            Tr.entry(tc, "destroy", _resource);
            Tr.exit(tc, "destroy");
        }
    }

    /**
     * Override java.lang.Object equals method.
     */
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o instanceof OnePhaseResourceImpl)
            return this._resource.equals(((OnePhaseResourceImpl)o)._resource);

        return super.equals(o);
    }
    
    @Override
    public int hashCode()
    {
    	return _resource.hashCode();
    }


	/* (non-Javadoc)
	 * @see com.ibm.ws.Transaction.JTA.JTAResource#describe()
	 */
	public String describe()
    {
        return "Resource: " + ((OnePhaseXAResource)_resource).getResourceName();
	}
}
