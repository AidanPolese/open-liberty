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
 *                 27/10/03 drphill  Original
 * 180053          03/11/03 gareth   Remove deprecated methods/interfaces
 * 180763.5        21/11/03 pradine  Add support for new PersistenceManager Interface
 * 184125          12/12/03 schofiel Spill enhancements
 * 182068.3        19/12/03 schofiel Fast'n'loose - initial drop of CachedPersistentDispatcher
 * 185331.1        08/01/04 pradine  Continued work to deprecate the Recoverable Interface
 * 187989          22/01/04 pradine  Enable spilling of STORE_MAYBE items
 * 188010          22/01/04 pradine  Clean up of persistence layer interfaces
 * 190181.1        18/02/04 schofiel Refactor data caching for dispatch - part 2
 * 188052          10/03/04 pradine  Changes to the garbage collector
 * 188052.1        16/03/04 schofiel Remove deprecated persist() method
 * 213328          30/06/04 pradine  Perform synchronous delete during 2PC processing
 * 214205          06/07/04 schofiel Clean up size calculations for tasks
 * 225118          13/08/20 drphill  Move code from tasks to stub (simplification)
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * 454303          26/07/07 gareth   Various FINDBUGS changes
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * 538096          23/07/08 susana   Use getInMemorySize for spilling & persistence
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
 * Class coordinates the persisting of a lock
 *
 */
public final class PersistLock extends Task
{

    private final class CachedPersistable extends CachedPersistableImpl
    {
        private long _cachedLockId;

        public CachedPersistable(Persistable masterPersistable)
        {
            super(masterPersistable);

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>$CachedPersistable");

            _cachedLockId = _masterPersistable.getLockID();

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>$CachedPersistable");
        }

        public long getLockID()
        {
            return _cachedLockId;
        }

        public void setLockID(long l)
        {
            //not settable
            throw new UnsupportedOperationException();
        }
    }
    private static TraceNLS nls = TraceNLS.getTraceNLS(MessageStoreConstants.MSG_BUNDLE);
    private static TraceComponent tc =
    SibTr.register(PersistLock.class, MessageStoreConstants.MSG_GROUP, MessageStoreConstants.MSG_BUNDLE);
    private Persistable _cachedPersistable = null;
    private Persistable _masterPersistable;

    public PersistLock(AbstractItemLink link) throws SevereMessageStoreException
    {
        super(link);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", link);

        _masterPersistable = super.getPersistable();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>", this);
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.NullTask#abort(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void abort(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "abort", transaction);

        getLink().abortPersistLock(transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "abort");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.task.NullTask#commitStage2(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void commitExternal(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "commitExternal", transaction);

        getLink().commitPersistLock(transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "commitExternal");
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
        return Type.PERSIST_LOCK;
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
            batchingContext.updateLockIDOnly(getPersistable());
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
