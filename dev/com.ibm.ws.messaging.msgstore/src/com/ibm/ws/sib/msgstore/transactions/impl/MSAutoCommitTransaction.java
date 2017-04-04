package com.ibm.ws.sib.msgstore.transactions.impl;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *                 03/02/03 drphill  Original
 * 164655          01/05/03 gareth   Initial Transaction code drop
 * 168092          02/06/03 gareth   AutoCommit unittest changes
 * 168464          06/06/03 gareth   Add preCommit call in beforeCompletion
 * 169765          17/06/03 gareth   Add preCommit call to AutoCommit
 * 169889          18/06/03 gareth   Add getId method to Transaction
 * 168080          18/07/03 gareth   LocalTransaction Support (Local Clients)
 * 170652          28/07/03 gareth   Move Transactions code to use SibTr
 * 174550          18/08/03 gareth   Add SITransaction and DelegatingResource
 * 168081          17/09/03 gareth   GlobalTransaction Support (Local Clients)
 * 178563          09/10/03 gareth   Add callback and MS get/setter
 * 182347          10/11/03 gareth   Add Persistent Transaction ID
 * 181930          12/11/03 gareth   XA Recovery Support
 * 184788          04/12/03 gareth   Add Transaction state model
 * 188494          27/01/04 gareth   Tie transactions to individual ME
 * 189573          05/02/04 gareth   Add NLS support to transaction code
 * 188050.4        06/04/04 pradine  SpecJAppServer2003 optimization
 * 184806.3.2      15/04/04 gareth   Move getPersistentTranId()
 * 186657.1        24/05/04 gareth   Per-work-item error checking.
 * 199334.1        27/05/04 gareth   Add transaction size counter
 * 186657.4        11/06/04 gareth   Add isAlive() method
 * 216527          15/07/04 gareth   Handle SeverePersistenceException
 * 219825          28/07/04 schofiel Reused auto-commit transactions use the same tranid
 * 222613          09/08/04 gareth   Standardize tranid generation
 * 229186          03/09/04 pradine  Cache the batching context inside a transaction
 * 229486          07/05/04 gareth   Improve javadoc
 * 237695          13/10/04 gareth   Better cleanup in afterCompletion
 * 262580          30/03/05 gareth   Make tran id more unique
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources (incl. rename)
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * 316887          03/02/06 gareth   Modify exception handling
 * 410652          12/04/07 gareth   Check Transactions ME at add time
 * 520772          14/05/08 djvines  Use an AtomicReference for _state and _workList to ensure visibility
 * PK70809         28/08/08 ajw      Make the callbacks list synchronized
 * ============================================================================
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.concurrent.atomic.AtomicReference;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.msgstore.MessageStore;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.PersistenceException;
import com.ibm.ws.sib.msgstore.ProtocolException;
import com.ibm.ws.sib.msgstore.RollbackException;
import com.ibm.ws.sib.msgstore.SeverePersistenceException;
import com.ibm.ws.sib.msgstore.TransactionException;
import com.ibm.ws.sib.msgstore.TransactionMaxSizeExceededException;
import com.ibm.ws.sib.msgstore.impl.MessageStoreImpl;
import com.ibm.ws.sib.msgstore.persistence.BatchingContext;
import com.ibm.ws.sib.msgstore.task.TaskList;
import com.ibm.ws.sib.msgstore.transactions.ExternalAutoCommitTransaction;
import com.ibm.ws.sib.transactions.PersistentTranId;
import com.ibm.ws.sib.transactions.TransactionCallback;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * This is the zero-phase implementation of the Transaction interface. When passed as
 * part of a call to the ItemStream interfaces this object will allow the execution
 * of work in single item batches.
 */
public final class MSAutoCommitTransaction implements ExternalAutoCommitTransaction, PersistentTransaction
{
    private static TraceComponent tc = SibTr.register(MSAutoCommitTransaction.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);

    private MessageStoreImpl   _ms;
    private PersistenceManager _persistence;
    private XidManager         _xidManager;

    private AtomicReference<TransactionState> _state    = new AtomicReference<TransactionState>(TransactionState.STATE_ACTIVE);

    private PersistentTranId   _ptid;
    private AtomicReference<WorkList>         _workList = new AtomicReference<WorkList>();

    private List<TransactionCallback>         _callbacks = Collections.synchronizedList(new ArrayList<TransactionCallback>(5));

    private int                _maxSize;
    private int                _size = 0;

    private BatchingContext    _bc;


    /**
     * The Default constructor for MSAutoCommitTransaction.
     *
     * @param ms      The (@link MessageStorImpl MessageStore} that this transaction is a part of.
     * @param persistence The {@link PersistenceManager} implementation to be used this transaction.
     * @param maxSize The number of operations allowed in this transaction.
     */
    public MSAutoCommitTransaction(MessageStoreImpl ms, PersistenceManager persistence, int maxSize)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "AutoCommitTransaction", "MessageStore="+ms+", Persistence="+persistence+", MaxSize="+maxSize);

        _ms          = ms;
        _persistence = persistence;
        _maxSize     = maxSize;

        if (_ms != null)
        {
            _xidManager = _ms.getXidManager();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "AutoCommitTransaction");
    }


    /**
     * In the AutoCommit case this method is the trigger for the completion
     * all work associated with this transaction. As completion is triggered
     * by every call to this method we therefore ensure zero-phase completion
     * for any WorkItems added to this transaction implementation.
     *
     * @param item   The piece of work to complete.
     *
     * @exception ProtocolException
     *                   Thrown if this method is called at an inappropriate time (during completion)
     *                   or with illegal parameters (null work items).
     * @exception TransactionException
     *                   Thrown if an error occurs whilst completing the supplied work item.
     */
    public synchronized void addWork(WorkItem item) throws ProtocolException, TransactionException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "addWork", "WorkItem="+item);

        if (_state.get() != TransactionState.STATE_ACTIVE)
        {
            ProtocolException pe = new ProtocolException("TRAN_PROTOCOL_ERROR_SIMS1001", new Object[]{});
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Cannot add work to transaction. Transaction is complete or completing!", pe);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "addWork");
            throw pe;
        }
        else
        {
            if (item != null)
            {
                _state.set(TransactionState.STATE_COMMITTING_1PC);

                // Create our work list for use in completion.
                _workList.set(new TaskList());

                _workList.get().addWork(item);

                // Call registered callbacks.
                for (int i = 0; i < _callbacks.size(); i++)
                {
                    TransactionCallback callback = (TransactionCallback) _callbacks.get(i);
                    callback.beforeCompletion(this);
                }

                try
                {
                    // We need to call preCommit first to
                    // see if we should continue with the
                    // transaction.
                    try
                    {
                        _workList.get().preCommit(this);
                    }
                    catch (Throwable t)
                    {
                        com.ibm.ws.ffdc.FFDCFilter.processException(t, "com.ibm.ws.sib.msgstore.transactions.AutoCommitTransaction.addWork", "1:186:1.10", this);
                        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Exception caught during work list preCommit!", t);

                        // An error has occurred during preCommit
                        // so we need to rollback and throw a
                        // RollbackException
                        try
                        {
                            _state.set(TransactionState.STATE_ROLLINGBACK);

                            // When we throw our RollbackException the TM is
                            // going to assume we are complete so we need to
                            // ensure we are rolled-back.
                            _workList.get().rollback(this);
                        }
                        catch (Throwable t2)
                        {
                            com.ibm.ws.ffdc.FFDCFilter.processException(t2, "com.ibm.ws.sib.msgstore.transactions.AutoCommitTransaction.addWork", "1:203:1.10", this);
                            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Exception caught rolling-back transaction after preCommit failure!", t2);
                        }

                        // Mark ourselves as complete and inform
                        // the TM that we have rolled-back.
                        _state.set(TransactionState.STATE_ROLLEDBACK);

                        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Transaction rolled-back due to Exception in preCommit!", t);
                        throw new RollbackException("COMPLETION_EXCEPTION_SIMS1002", new Object[] {t}, t);
                    }

                    // Call the persistence layer to write to the
                    // database.
                    _persistence.commit(this, true);

                    // Now we have called the persistence layer we need
                    // to call the in memory model to ensure it matches
                    // the direction that the database took.
                    _workList.get().commit(this);

                    _state.set(TransactionState.STATE_COMMITTED);
                }
                catch (RollbackException rbe)
                {
                    //No FFDC Code Needed.
                    throw rbe;
                }
                catch (SeverePersistenceException spe)
                {
                    com.ibm.ws.ffdc.FFDCFilter.processException(spe, "com.ibm.ws.sib.msgstore.transactions.AutoCommitTransaction.addWork", "1:233:1.10", this);

                    // As we have thrown a severe exception we need to inform
                    // the ME that this instance is no longer able to continue.
                    if (_ms != null)
                    {
                        _ms.reportLocalError();
                    }

                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Unrecoverable persistence exception caught during commit phase of transaction!", spe);
                    throw new TransactionException("COMPLETION_EXCEPTION_SIMS1002", new Object[] {spe}, spe);
                }
                catch (PersistenceException pe)
                {
                    com.ibm.ws.ffdc.FFDCFilter.processException(pe, "com.ibm.ws.sib.msgstore.transactions.AutoCommitTransaction.addWork", "1:247:1.10", this);

                    // The persistence layer has had a problem
                    // so we need to rollback our in memory model
                    // as we can't ensure our data's integrity.
                    try
                    {
                        // When we throw our RollbackException the TM is
                        // going to assume we are complete so we need to
                        // ensure we are rolled-back.
                        _workList.get().rollback(this);

                        _state.set(TransactionState.STATE_ROLLEDBACK);
                    }
                    catch (Throwable t)
                    {
                        com.ibm.ws.ffdc.FFDCFilter.processException(t, "com.ibm.ws.sib.msgstore.transactions.AutoCommitTransaction.addWork", "1:263:1.10", this);
                        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Exception caught rolling-back WorkList after Commit failure!", t);
                    }

                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Transaction rolled-back due to Exception in Commit!", pe);
                    throw new RollbackException("COMPLETION_EXCEPTION_SIMS1002", new Object[] {pe}, pe);
                }
                catch (Throwable t)
                {
                    com.ibm.ws.ffdc.FFDCFilter.processException(t, "com.ibm.ws.sib.msgstore.transactions.AutoCommitTransaction.addWork", "1:272:1.10", this);
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Exception caught during commit phase of transaction!", t);
                    throw new TransactionException("COMPLETION_EXCEPTION_SIMS1002", new Object[] {t}, t);
                }
                finally
                {
                    // Defect 237695
                    // We need to make sure that we are ready for
                    // re-use even if a problem occurs in afterCompletion.
                    try
                    {
                        boolean committed = (_state.get() == TransactionState.STATE_COMMITTED);

                        if (_workList.get() != null)
                        {
                            _workList.get().postComplete(this, committed);
                        }

                        for (int i = 0; i < _callbacks.size(); i++)
                        {
                            TransactionCallback callback = (TransactionCallback) _callbacks.get(i);
                            callback.afterCompletion(this, committed);
                        }
                    }
                    catch (Throwable t)
                    {
                        com.ibm.ws.ffdc.FFDCFilter.processException(t, "com.ibm.ws.sib.msgstore.transactions.AutoCommitTransaction.addWork", "1:298:1.10", this);
                        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Exception caught during post commit phase of transaction!", t);
                        // Defect 316887
                        // We aren't going to rethrow this exception as if
                        // a previous exception has been thrown outside the
                        // finally block it is likely to have more
                        // information about the root problem.
                    }

                    // We need to reset all of our stateful variables
                    // so that we are ready for re-use.
                    _callbacks.clear();
                    _workList.set(null);
                    _state.set(TransactionState.STATE_ACTIVE);
                    _ptid     = null;

                    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "addWork");
                }
            }
            else
            {
                // We cannot do anything with a null
                // item so we need to reset our
                // state and throw an exception.
                _state.set(TransactionState.STATE_ACTIVE);

                ProtocolException pe = new ProtocolException("TRAN_PROTOCOL_ERROR_SIMS1001", new Object[]{});
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Cannot add null work item to transaction!", pe);
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "addWork");
                throw pe;
            }
        }
    }


    public WorkList getWorkList()
    {
        WorkList snapshot = _workList.get();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(this, tc, "getWorkList");
            SibTr.exit(this, tc, "getWorkList", "return="+snapshot);
        }
        return snapshot;
    }


    public void registerCallback(TransactionCallback callback)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "registerCallback", "Callback="+callback);

        _callbacks.add(callback);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "registerCallback");
    }


    public boolean isAutoCommit()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(this, tc, "isAutoCommit");
            SibTr.exit(this, tc, "isAutoCommit", "return=true");
        }
        return true;
    }


    public synchronized PersistentTranId getPersistentTranId()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getPersistentTranId");

        if (_ptid == null)
        {
            if (_xidManager != null)
            {
                _ptid = new PersistentTranId(_xidManager.generateLocalTranId());
            }
            else
            {
                _ptid = new PersistentTranId(this.hashCode());
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getPersistentTranId", "return="+_ptid);
        return _ptid;
    }


    public void incrementCurrentSize() throws SIResourceException
    {
        if (++_size > _maxSize)
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Transaction size incremented: "+_size);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.event(this, tc, "Maximum transaction size reached, throwing exception!");
            throw new SIResourceException(new TransactionMaxSizeExceededException());
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Transaction size incremented: "+_size);
    }


    public boolean isAlive()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "isAlive");

        boolean retval = (_state.get() == TransactionState.STATE_ACTIVE);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "isAlive", "return="+retval);
        return retval;
    }


    /**
     * @return True if (and only if) the transaction has (or can have)
     * subordinates.
     */
    public boolean hasSubordinates()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "hasSubordinates");

        boolean retval = false;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "hasSubordinates", "return="+retval);
        return retval;
    }

    /*************************************************************************/
    /*                  PersistentTransaction Implementation                 */
    /*************************************************************************/


    public final int getTransactionType()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(this, tc, "getTransactionType");
            SibTr.exit(this, tc, "getTransactionType", "return=TX_AUTO_COMMIT");
        }
        return TX_AUTO_COMMIT;
    }


    public void setTransactionState(TransactionState state)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setTransactionState", "State="+state);

        _state.set(state);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setTransactionState");
    }

    public TransactionState getTransactionState()
    {
        TransactionState snapshot = _state.get();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(this, tc, "getTransactionState");
            SibTr.exit(this, tc, "getTransactionState", "return="+snapshot);
        }
        return snapshot;
    }


    public BatchingContext getBatchingContext()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(this, tc, "getBatchingContext");
            SibTr.exit(this, tc, "getBatchingContext", "return="+_bc);
        }
        return _bc;
    }


    public void setBatchingContext(BatchingContext bc)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "setBatchingContext", "BatchingContext="+bc);

        _bc = bc;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "setBatchingContext");
    }

    // Defect 410652
    /**
     * This method is used to check the MessageStore instance that an implementing
     * transaction object originated from. This is used to check that a transaction
     * is being used to add Items to the same MessageStore as that it came from.
     *
     * @return The MessageStore instance where this transaction originated from.
     */
    public MessageStore getOwningMessageStore()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(this, tc, "getOwningMessageStore");
            SibTr.exit(this, tc, "getOwningMessageStore", "return="+_ms);
        }
        return _ms;
    }
}
