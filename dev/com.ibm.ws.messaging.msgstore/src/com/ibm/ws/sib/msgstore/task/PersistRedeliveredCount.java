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
 * F1332-51592     28/09/11 madhukar Persist redelivery count to FILESTORE
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink;
import com.ibm.ws.sib.msgstore.persistence.BatchingContext;
import com.ibm.ws.sib.msgstore.persistence.Persistable;
import com.ibm.ws.sib.msgstore.persistence.impl.CachedPersistableImpl;
import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;
import com.ibm.ws.sib.msgstore.transactions.impl.TransactionState;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Class coordinates the persisting of redelivered count
 *
 */
public final class PersistRedeliveredCount extends Task
{

    private final class CachedPersistable extends CachedPersistableImpl
    {
        private int _cachedRedeliveredCount;

        public CachedPersistable(Persistable masterPersistable)
        {
            super(masterPersistable);

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>$CachedPersistable");

            _cachedRedeliveredCount = _masterPersistable.getRedeliveredCount();

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>$CachedPersistable");
        }

        public int getRedeliveredCount()
        {
            return _cachedRedeliveredCount;
        }

        public void setRedeliveredCount(int l)
        {
            //not settable
            throw new UnsupportedOperationException();
        }
    }
    private static TraceNLS nls = TraceNLS.getTraceNLS(MessageStoreConstants.MSG_BUNDLE);
    private static TraceComponent tc =
    SibTr.register(PersistRedeliveredCount.class, MessageStoreConstants.MSG_GROUP, MessageStoreConstants.MSG_BUNDLE);
    private Persistable _cachedPersistable = null;
    private Persistable _masterPersistable;

    public PersistRedeliveredCount(AbstractItemLink link) throws SevereMessageStoreException
    {
        super(link);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", link);

        _masterPersistable = super.getPersistable();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>", this);
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.NullTask#abort(com.ibm.ws.sib.msgstore.Transaction)
     * The task of persisting redelivered count is performed through auto-commit transaction.
     * Maintaining transaction states is not required as no work is needed in commit or abort operations.
     */
    public final void abort(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.NullTask#commitStage2(com.ibm.ws.sib.msgstore.Transaction)
     * The task of persisting redelivered count is performed through auto-commit transaction.
     * Maintaining transaction states is not required as no work is needed in commit or abort operations.
     */
    public final void commitExternal(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.Task#commitStage1(com.ibm.ws.sib.msgstore.transactions.Transaction)
     */
    public final void commitInternal(PersistentTransaction transaction) {}

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#copyDataIfVulnerable()
     */
    public final void copyDataIfVulnerable()
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "copyDataIfVulnerable");

        if (_cachedPersistable == null)
        {
            _cachedPersistable = new CachedPersistable(_masterPersistable);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "copyDataIfVulnerable");
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#getPersistable()
     */
    public final Persistable getPersistable()
    {
        if (_cachedPersistable != null)
        {
            return _cachedPersistable;
        }
        else
        {
            return _masterPersistable;
        }
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#getPersistableInMemorySizeApproximation(int)
     */
    public final int getPersistableInMemorySizeApproximation(TransactionState tranState)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getPersistableInMemorySizeApproximation", tranState);

        int size;

        if ((tranState == TransactionState.STATE_COMMITTED) || (tranState == TransactionState.STATE_COMMITTING_1PC))
        {
            size = DEFAULT_TASK_PERSISTABLE_SIZE_APPROXIMATION;
        }
        else
        {
            throw new IllegalStateException(nls.getFormattedMessage("INVALID_TASK_OPERATION_SIMS1520", new Object[] {tranState}, null));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getPersistableInMemorySizeApproximation", Integer.valueOf(size));
        return size;
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.Task#getTaskType()
     */
    public final Type getTaskType()
    {
        return Type.PERSIST_REDELIVERED_COUNT;
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#persist(com.ibm.ws.sib.msgstore.persistence.BatchingContext, int)
     */
    public final void persist(BatchingContext batchingContext, TransactionState tranState)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "persist", new Object[] { batchingContext, tranState});

        if ((tranState == TransactionState.STATE_COMMITTED) || (tranState == TransactionState.STATE_COMMITTING_1PC))
        {
            batchingContext.updateRedeliveredCountOnly(getPersistable());
        }
        else
        {
            throw new IllegalStateException(nls.getFormattedMessage("INVALID_TASK_OPERATION_SIMS1520", new Object[] {tranState}, null));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "persist");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.Task#postAbort(com.ibm.ws.sib.msgstore.transactions.Transaction)
     */
    public final void postAbort(PersistentTransaction transaction) {}

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.Task#postCommit(com.ibm.ws.sib.msgstore.transactions.Transaction)
     */
    public final void postCommit(PersistentTransaction transaction) {}

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.Task#preCommitStage1(com.ibm.ws.sib.msgstore.transactions.Transaction)
     */
    public final void preCommit(PersistentTransaction transaction) {}
}
