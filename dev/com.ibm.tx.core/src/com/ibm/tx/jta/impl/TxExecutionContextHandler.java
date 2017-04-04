package com.ibm.tx.jta.impl;
/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2009 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  YY-MM-DD   Programmer  Defect      Description                                                       */
/*  ---------  ----------  ------      -----------                                                       */
/*  03-05-16   ehadley     -           Creation                                                          */
/*  03-07-18   johawkes    LIDB2110.12 JCA 1.5                                                           */
/*  03-08-06   johawkes    173643      Use Strings to key txnTable                                       */
/*  03-08-18   johawkes    174376      Validate execution context on associate                           */
/*  03-08-19   johawkes    174593      Resume local tran on dissociate                                   */
/*  03-08-22   johawkes    174726      Allow null Xid on associate                                       */
/*  03-09-25   johawkes    177245      Allow commit_one_phase during recovery                            */
/*  03-09-25   johawkes    177208      Add trace                                                         */
/*  03-09-30   johawkes    178038      Use local association methods                                     */
/*  03-11-07   johawkes    182128      throw WCE when already associated                                 */
/*  20/11/03   johawkes    182862      Remove static partner log dependencies                            */
/*  27/11/03   johawkes    178502      Start an RA during XA recovery                                    */
/*  05/12/03   johawkes    184903      Refactor PartnerLogTable                                          */
/*  08/12/03   johawkes    184992      Correct error code for concurrent work                            */
/*  12/12/03   johawkes    185481      Suppress some WTRN0091 error messages                             */
/*  06/01/04   hursdlg     LIDB2775    zOS/distributed merge                                             */
/*  07/01/04   johawkes    LIDB2110    RA Uninstall                                                      */
/*  04/02/04   johawkes    189497      Improve comments per code review                                  */
/*  05/02/04   mallam      LIDB2775    Rename XID to XidImpl                                             */
/*  04/03/04   johawkes    191316      Log resources when setting LPS state                              */
/*  17/03/04   johawkes    192653      Cancel timeouts on RA uninstall                                   */
/*  25/03/04   johawkes    195344.1    Stop logging JCAProvider on registerAS                            */
/*  22/04/04   awilkins    198904.1    getXid changes                                                    */
/*  27/04/04   mallam      197039    Prolong finish for heuristic on recovery                            */
/*  27/05/04   johawkes    204546      stoppingProvider rollback behaviour                               */
/*  28/05/04   johawkes    204553      Now uses isJCAImportedAndPrepared()                               */
/*  16/06/04   johawkes    209345      Remove unnecessary code                                           */
/*  06/07/04   johawkes    213406      Allow completion during quiesce                                   */
/*  27/07/04   johawkes    219412      Fix shutdown for JCA imported transactions                        */
/*  04/08/05   kaczyns     LIDB2110    Use JCATranWrapper iface                                          */
/*  19/08/04   johawkes    224215      Detect uninstalled providers better                               */
/*  09/12/04   hursdlg     240298      Suspend LTC prior to creating global txn                          */
/*  11/01/05   hursdlg     249308      recover to return only CR based xids                              */
/*  31/01/05   hursdlg     248457.1    Check for tran after dissociate                                   */
/*  15/03/05   mallam      260064      undo 248457.1                                                     */
/*  05/07/05   johawkes    252569      Process Faults                                                    */
/*  05/11/01   mezarin     LI3187-29.2 Modify the recover call for z/OS                                  */
/*  06/06/12   hursdlg     371109      Check for null on recover                                         */
// 07/04/12 johawkes LIDB4171-35    Componentization
// 07/04/12 johawkes 430278         Further componentization
// 07/05/16 johawkes 438575         Further componentization
// 07/06/06 johawkes 443467         Repackaging
// 07/08/06 johawkes 451213.1       Moved into JTM
// 07/08/30 johawkes 463313         Override TransactionImpl creation in WAS
// 08/02/15 kaczyns  512190         Handle exceptions on tranwrapper create
// 08/04/25 johawkes 514000         Log JCA provider entry
// 09/06/02 mallam   596067         package move
/* ***************************************************************************************************** */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.WorkCompletedException;
import javax.resource.spi.work.WorkException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import com.ibm.tx.jta.*;
import com.ibm.tx.TranConstants;
import com.ibm.tx.util.ByteArray;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.JTA.Util;

/**
 * An implementation of the com.ibm.ws.j2c.work.ExecutionContextHandler interface
 */
public class TxExecutionContextHandler
{
    private static final TraceComponent tc = Tr.register(TxExecutionContextHandler.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    private static final TxExecutionContextHandler _instance = new TxExecutionContextHandler();

    // Hashtable of Xid->JCATranWrapper
    protected static final HashMap<ByteArray, JCATranWrapper> txnTable = new HashMap<ByteArray, JCATranWrapper>();

    /* (non-Javadoc)
     * Associates an ExecutionContext with the current thread.
     * This is called on the *dispatch thread*.
     * @see com.ibm.ws.j2c.work.ExecutionContextHandler#associate(javax.resource.spi.work.ExecutionContext, java.lang.String)
     */
    public void associate(ExecutionContext ec, String providerId) throws WorkCompletedException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "doAssociate", new Object[] { ec, providerId });

        // Check that a transaction context is present
        if (null == ec)
        {
            final WorkCompletedException wce = new WorkCompletedException("Invalid ExecutionContext", WorkException.TX_RECREATE_FAILED);
            Tr.error(tc, "WTRN0091_ASSOCIATE_FAILED", new Object[] { null, null });
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", "Invalid ExecutionContext");
            throw wce;
        }

        final Xid xid = ec.getXid();
        if (null == xid)
        {
            // Nothing to do
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", "Null Xid");
            return;
        }

        // If xid is present, it's got to be valid
        if (!TxXATerminator.isValid(xid))
        {
            final WorkCompletedException wce = new WorkCompletedException("Invalid Xid", WorkException.TX_RECREATE_FAILED);
            Tr.error(tc, "WTRN0091_ASSOCIATE_FAILED", new Object[] { ec, ec.getTransactionTimeout()});
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", "Invalid Xid");
            throw wce;
        }

        int status = Status.STATUS_NO_TRANSACTION;

        try
        {
            status = TransactionManagerFactory.getTransactionManager().getStatus();
        }
        catch(SystemException e)
        {
            final WorkCompletedException wce = new WorkCompletedException(WorkException.TX_RECREATE_FAILED, e);
            Tr.error(tc, "WTRN0091_ASSOCIATE_FAILED", new Object[] { ec, ec.getTransactionTimeout()});
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", wce);
            throw wce;
        }

        if (status != Status.STATUS_NO_TRANSACTION)
        {
            // There's already a global tx on this thread
            final WorkCompletedException wce = new WorkCompletedException("Already associated", WorkException.TX_RECREATE_FAILED);
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", "Already associated");
            throw wce;
        }

        if (providerId == null)
        {
            final WorkCompletedException wce = new WorkCompletedException("Null providerId", WorkException.TX_RECREATE_FAILED);
            Tr.error(tc, "WTRN0091_ASSOCIATE_FAILED", new Object[] { ec, ec.getTransactionTimeout()});
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", "Invalid providerId: " + providerId);
            throw wce;
        }

        // Check the txnTable for a previous occurance of this XID
        // If this returns null it means either that the transaction
        // is already associated or that is has already been prepared.
        // d240298 - findTxWrapper will also suspend any LTC and save in the wrapper for resuming later
        final JCATranWrapper txWrapper;
        try
        {
            txWrapper = findTxWrapper((int) ec.getTransactionTimeout(), xid, providerId);
        }
        catch(WorkCompletedException wce)
        {
            // Must be quiescing
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", "Can't create new tx while quiescing");
            throw wce;
        }

        if (txWrapper == null)
        {
            // Must already have had an association or been prepared
            final WorkCompletedException wce = new WorkCompletedException("Already have an association or already prepared", WorkException.TX_CONCURRENT_WORK_DISALLOWED);
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", "Already have an association or already prepared");
            throw wce;
        }

        // Resume the new transaction
        try
        {
        	((TranManagerSet)TransactionManagerFactory.getTransactionManager()).resume(txWrapper.getTransaction());
        }
        catch (InvalidTransactionException e)
        {
            final WorkCompletedException wce = new WorkCompletedException("resume threw InvalidTransactionException", e);
            wce.setErrorCode(WorkException.TX_RECREATE_FAILED);
            Tr.error(tc, "WTRN0091_ASSOCIATE_FAILED", new Object[] { ec, ec.getTransactionTimeout()});
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", "resume threw InvalidTransactionException");
            throw wce;
        }
        catch (IllegalStateException e)
        {
            final WorkCompletedException wce = new WorkCompletedException("resume threw IllegalStateException", e);
            wce.setErrorCode(WorkException.TX_RECREATE_FAILED);
            Tr.error(tc, "WTRN0091_ASSOCIATE_FAILED", new Object[] { ec, ec.getTransactionTimeout()});
            if (tc.isEntryEnabled()) Tr.exit(tc, "associate", "resume threw IllegalStateException");
            throw wce;
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "associate");
    }

    /**
     * Dissociates any ExecutionContext on the current thread
     */
    public void dissociate()
    {
        doDissociate();
    }

    public static TransactionImpl doDissociate()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "doDissociate");

        final TransactionImpl oldTxn = ((TranManagerSet)TransactionManagerFactory.getTransactionManager()).getTransactionImpl();
        if (oldTxn != null)
        {
        	((TranManagerSet)TransactionManagerFactory.getTransactionManager()).suspend();

            final Xid xid = oldTxn.getXid();

            final ByteArray key = new ByteArray(xid.getGlobalTransactionId());

            final JCATranWrapper txWrapper;

            synchronized (txnTable)
            {
                txWrapper = txnTable.get(key);

                if (null != txWrapper)
                {
                    txWrapper.removeAssociation();
                }
                else
                {
                    // No imported transaction to disassociate
                    if (tc.isEntryEnabled()) Tr.exit(tc, "doDissociate", oldTxn);
                    return oldTxn;
                }
            }

            // Now we resume the local transaction
            // if we suspended it in associate
            txWrapper.resume();
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "doDissociate", oldTxn);
        return oldTxn;
    }

    /**
     * Given an Xid, returns the corresponding JCATranWrapper from the table of
     * imported transactions, or null if no entry exists.
     * @param xid
     * @param addAssociation
     * @return
     * @throws XAException
     */
    public static JCATranWrapper getTxWrapper(Xid xid, boolean addAssociation) throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getTxWrapper", new Object[] { xid, addAssociation});

        final ByteArray key = new ByteArray(xid.getGlobalTransactionId());

        final JCATranWrapper txWrapper;

        synchronized (txnTable)
        {
            txWrapper = txnTable.get(key);

            if (txWrapper != null)
            {
                if (addAssociation)
                {
                    if (!txWrapper.hasAssociation())
                    {
                        txWrapper.addAssociation();
                    }
                    else
                    {
                        // Already associated
                        if (tc.isEntryEnabled()) Tr.exit(tc, "getTxWrapper", "throwing XAER_PROTO");
                        throw new XAException(XAException.XAER_PROTO);
                    }
                }
            }
            else
            {
                if (tc.isEntryEnabled()) Tr.exit(tc, "getTxWrapper", "throwing XAER_NOTA");
                throw new XAException(XAException.XAER_NOTA);
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "getTxWrapper", txWrapper);
        return txWrapper;
    }

    /**
     * Retrieve a JCATranWrapper from the table.
     * Insert it first if it wasn't already there.
     * Returns null if association already existed or if transaction has been prepared.
     * @param timeout
     * @param xid
     * @return
     */
    protected JCATranWrapper findTxWrapper(int timeout, Xid xid, String providerId) throws WorkCompletedException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "findTxWrapper", new Object[] { timeout, xid, providerId });

        final JCATranWrapper txWrapper;
        final ByteArray key = new ByteArray(xid.getGlobalTransactionId());

        synchronized (txnTable)
        {
            if (!txnTable.containsKey(key))
            {
                // XID has not been encountered - create a new TransactionImpl and add it to the table
                
                // ......unless we're quiescing
                if(!((TranManagerSet)TransactionManagerFactory.getTransactionManager()).isQuiesced())
                {
                    final JCARecoveryData jcard = (JCARecoveryData) ((TranManagerSet)TransactionManagerFactory.getTransactionManager()).registerJCAProvider(providerId);

                    try
                    {
                        jcard.logRecoveryEntry();
                    }
                    catch(Exception e)
                    {
                        if (tc.isEntryEnabled()) Tr.exit(tc, "findTxWrapper", e);
                        throw new WorkCompletedException(e.getLocalizedMessage(), WorkException.TX_RECREATE_FAILED);
                    }

                    // Create a new wrapper, suspend any transaction, create the new TransactionImpl and mark associated
                    txWrapper = createWrapper(timeout, xid, jcard);
                    
                    txnTable.put(key, txWrapper);
                }
                else
                {
                    if (tc.isEntryEnabled()) Tr.exit(tc, "findTxWrapper", "quiescing");
                    throw new WorkCompletedException("In quiesce period", WorkException.TX_RECREATE_FAILED);
                }
            }
            else
            {
                // XID has already been imported, retrieve JCATranWrapper from table
                if (tc.isEventEnabled()) Tr.event(tc, "Already encountered", key);

                txWrapper = txnTable.get(key);

                // If we already had an association, return null so
                // caller knows to throw an exception.
                if (!txWrapper.hasAssociation())
                {
                    // If we were already prepared, return null so
                    // caller knows to throw an exception.
                    if (!txWrapper.isPrepared())
                    {
                        txWrapper.addAssociation();
                    }
                    else
                    {
                        if (tc.isEntryEnabled()) Tr.exit(tc, "findTxWrapper", "already prepared");
                        return null;
                    }
                }
                else
                {
                    if (tc.isEntryEnabled()) Tr.exit(tc, "findTxWrapper", "already associated");
                    return null;
                }

                // d240298 - Suspend any local transaction before we return,
                // save it in the wrapper for resuming later
                txWrapper.suspend();   // @D240298A
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "findTxWrapper", txWrapper);
        return txWrapper;
    }

    // Overridden in WAS
    protected JCATranWrapper createWrapper(int timeout, Xid xid, JCARecoveryData jcard) throws WorkCompletedException /* @512190C*/
    {
        return new JCATranWrapperImpl(timeout, xid, jcard); // @D240298C
    }

    /**
     * To be called by recovery manager
     * @param txn
     */
    public static void addTxn(TransactionImpl txn)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "addTxn", txn);

        final ByteArray key = new ByteArray(txn.getXid().getGlobalTransactionId());

        synchronized (txnTable)
        {
            if (!txnTable.containsKey(key))
            {
                txnTable.put(key, new JCATranWrapperImpl(txn, true, false)); // @LIDB2110C
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "addTxn");
    }

    /**
     * Called in getTransaction
     * @param gtid
     * 
     */
    public static final void removeTxn(Xid xid)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "removeTxn", xid);

        final ByteArray key = new ByteArray(xid.getGlobalTransactionId());

        final JCATranWrapper wrapper;

        synchronized (txnTable)
        {
            wrapper = txnTable.remove(key);
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "removeTxn", wrapper);
    }

    /**
     * @param txn
     */
    protected void reAssociate(TransactionImpl txn)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "reAssociate", txn);

        // If it was imported we need to properly reAssociate
        // otherwise we just need to resume
        if (txn.isRAImport())
        {
            // Dummy up an Execution context
            final ExecutionContext ec = new ExecutionContext();
            ec.setXid(txn.getXid());

            try
            {
                associate(ec, txn.getJCARecoveryData().getWrapper().getProviderId());
            }
            catch (WorkCompletedException e)
            {
                // won't get here in this case
                if (tc.isEventEnabled()) Tr.exit(tc, "reAssociate", e);
            }
        }
        else
        {
            try
            {
            	((TranManagerSet)TransactionManagerFactory.getTransactionManager()).resume(txn);
            }
            catch (Exception e)
            {
                if (tc.isEventEnabled()) Tr.event(tc, "reAssociate", e);
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "reAssociate");
    }

    /**
     * @param providerId
     * @param flag ignored
     * @return
     * @throws XAException
     */
    public static Xid[] recover(int flag) throws XAException
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "recover", Util.printFlag(flag));

        if (((TranManagerSet)TransactionManagerFactory.getTransactionManager()).isReplayComplete())
        {
            final Xid[] xids;

            synchronized (txnTable)
            {
                final ArrayList<Xid> xidList = new ArrayList<Xid>();                   // @LI3187-29.2C


                // Single process - we can check our own in-process list of JCA txns
                for (Iterator i = txnTable.values().iterator(); i.hasNext();)
                {
                    final JCATranWrapper txWrapper = (JCATranWrapper) i.next();

                    final TransactionImpl txn = txWrapper.getTransaction();

                    switch (txn.getTransactionState().getState())
                    {
                    case TransactionState.STATE_HEURISTIC_ON_COMMIT :
                    case TransactionState.STATE_HEURISTIC_ON_ROLLBACK :
                    case TransactionState.STATE_PREPARED :

                        if (tc.isDebugEnabled()) Tr.debug(tc, "recovering txn with state: " + txn.getTransactionState());
                        final Xid xid = txn.getJCAXid();
                        xidList.add(xid);
                        break;

                    default :
                        break;
                    }
                }

                xids = xidList.toArray(new Xid[0]);
            }

            if (tc.isEntryEnabled()) Tr.exit(tc, "recover", xids);
            return xids;
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "recover", "throwing XAER_RMFAIL");
        throw new XAException(XAException.XAER_RMFAIL);
    }

    /**
     * @param txWrapper
     */
    public static void removeAssociation(JCATranWrapper txWrapper)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "removeAssociation", txWrapper);

        synchronized (txnTable)
        {
            txWrapper.removeAssociation();
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "removeAssociation");
    }

    public static TxExecutionContextHandler instance()
    {
        return _instance;
    }
}
