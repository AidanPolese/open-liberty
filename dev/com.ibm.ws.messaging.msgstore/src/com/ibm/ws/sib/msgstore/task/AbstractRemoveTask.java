package com.ibm.ws.sib.msgstore.task;
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
 *                 12/03/04 drphill  Original
 * 188052.1        16/03/04 schofiel Remove deprecated persist() method
 * 213328          30/06/04 pradine  Perform synchronous delete during 2PC processing
 * 214205          06/07/04 schofiel Clean up size calculations for tasks
 * 225118          13/08/04 drphill  Move code from tasks to stub (simplification)
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * 445623          23/07/07 gareth   Use Boolean.valueOf() in trace statements
 * 463942          03/09/07 gareth   Change size reported to dispatchers
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * 538096          23/07/08 susana   Use getInMemorySize for spilling & persistence
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.AbstractItem;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink;
import com.ibm.ws.sib.msgstore.cache.links.LinkOwner;
import com.ibm.ws.sib.msgstore.persistence.BatchingContext;
import com.ibm.ws.sib.msgstore.persistence.Persistable;
import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;
import com.ibm.ws.sib.msgstore.transactions.impl.TransactionState;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

public abstract class AbstractRemoveTask extends Task
{
    private static TraceNLS nls = TraceNLS.getTraceNLS(MessageStoreConstants.MSG_BUNDLE);
    private static TraceComponent tc = SibTr.register(AbstractRemoveTask.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);

    public AbstractRemoveTask(final AbstractItemLink link) throws SevereMessageStoreException
    {
        super(link);
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#abort(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void abort(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "abort", transaction);

        getLink().abortRemove(transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "abort");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#commitStage2(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void commitExternal(final PersistentTransaction transaction)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            SibTr.entry(this, tc, "commitExternal", transaction);
            SibTr.exit(this, tc, "commitExternal");
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#commitStage1(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void commitInternal(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "commitInternal", transaction);

        getLink().commitRemove(transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "commitInternal");
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#getPersistableInMemorySizeApproximation(int)
     */
    public final int getPersistableInMemorySizeApproximation(TransactionState tranState)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getPersistableInMemorySizeApproximation", tranState);

        int size;

        if ((tranState == TransactionState.STATE_COMMITTED)
            || (tranState == TransactionState.STATE_COMMITTING_1PC)
            || (tranState == TransactionState.STATE_COMMITTING_2PC)
            || (tranState == TransactionState.STATE_PREPARED)
            || (tranState == TransactionState.STATE_PREPARING)
            || (tranState == TransactionState.STATE_ROLLEDBACK)
            || (tranState == TransactionState.STATE_ROLLINGBACK))
        {
            size = DEFAULT_TASK_PERSISTABLE_SIZE_APPROXIMATION + getPersistable().getInMemoryByteSize();
        }
        else
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getPersistableInMemorySizeApproximation");
            throw new IllegalStateException(nls.getFormattedMessage("INVALID_TASK_OPERATION_SIMS1520", new Object[] {tranState}, null));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getPersistableInMemorySizeApproximation", Integer.valueOf(size));
        return size;
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.Task#getTaskType()
     */
    public Task.Type getTaskType()
    {
        return Type.REMOVE;
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#isDeleteOfPersistentRepresentation()
     */
    public boolean isDeleteOfPersistentRepresentation()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#isRemoveFromList(com.ibm.ws.sib.msgstore.cache.xalist.TransactionalList)
     */
    public final boolean isRemoveFromList(final LinkOwner list)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "isRemoveFromList");

        LinkOwner myList = getLink().getOwningStreamLink();
        boolean is = (myList == list);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "isRemoveFromList", Boolean.valueOf(is));
        return is;
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#persist(com.ibm.ws.sib.msgstore.persistence.BatchingContext, int)
     */
    public final void persist(BatchingContext batchingContext, TransactionState tranState)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "persist", new Object[] { batchingContext, tranState});

        if ((tranState == TransactionState.STATE_COMMITTED)
            || (tranState == TransactionState.STATE_COMMITTING_1PC)
            || (tranState == TransactionState.STATE_COMMITTING_2PC))
        {
            batchingContext.delete(getPersistable());
        }
        else if ((tranState == TransactionState.STATE_PREPARED)
                 || (tranState == TransactionState.STATE_PREPARING))
        {
            Persistable tuple = getPersistable();
            tuple.setLogicallyDeleted(true);
            batchingContext.updateLogicalDeleteAndXID(tuple);
        }
        else if ((tranState == TransactionState.STATE_ROLLEDBACK)
                 || (tranState == TransactionState.STATE_ROLLINGBACK))
        {
            Persistable tuple = getPersistable();
            tuple.setLogicallyDeleted(false);
            batchingContext.updateLogicalDeleteAndXID(tuple);
        }
        else
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "persist");
            throw new IllegalStateException(nls.getFormattedMessage("INVALID_TASK_OPERATION_SIMS1520", new Object[] {tranState}, null));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "persist");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#postAbort(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void postAbort(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "postAbort", transaction);

        AbstractItem item = getItem();
        if (null == item)
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "null item");
        }
        else
        {
            item.eventPostRollbackRemove(transaction);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "postAbort");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#postCommit(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void postCommit(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "postCommit", transaction);

        AbstractItem item = getItem();
        if (null == item)
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "null item");
        }
        else
        {
            item.eventPostCommitRemove(transaction);
        }
        getLink().postCommitRemove(transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "postCommit");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#preCommit(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void preCommit(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "preCommit", transaction);

        getItem();  // try to cache item in case we havent got it yet....
        getLink().preCommitRemove(transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "preCommit");
    }
}
