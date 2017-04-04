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
 *                 26/06/03 drphill  Original
 * 180053          03/11/03 gareth   Remove deprecated methods/interfaces
 * 183455          20/11/03 corrigk  Non-Cache Expirer implementation
 * 180763.5        21/11/03 pradine  Add support for new PersistenceManager Interface
 * 184125          12/12/03 schofiel Spill enhancements
 * 182068.3        19/12/03 schofiel Fast'n'loose - initial drop of CachedPersistentDispatcher
 * 185331.1        08/01/04 pradine  Continued work to deprecate the Recoverable Interface
 * 187989          22/01/04 pradine  Enable spilling of STORE_MAYBE items
 * 188010          22/01/04 pradine  Clean up of persistence layer interfaces
 * 180763.7        10/02/04 pradine  Add support for mutiple item tables
 * 184390.1.2      18/02/04 schofiel Revised Reliability Qualities of Service - MS - Task changes
 * 188052          10/03/04 pradine  Changes to the garbage collector
 * 188052.1        16/03/04 schofiel Remove deprecated persist() method
 * 213328          30/06/04 pradine  Perform synchronous delete during 2PC processing
 * 214205          06/07/04 schofiel Clean up size calculations for tasks
 * 225118          13/08/04 drphill  Move code from tasks to stub (simplification)
 * 223996          25/08/04 corrigk  Remove tick count
 * 223996.1        01/09/04 pradine  Remove tick count and max depth
 * 247513          24/01/05 schofiel Improve spilling performance
 * 258179          06/04/05 schofiel Indoubt transaction reference counts
 * 272110          10/05/05 schofiel 602:SVT: Malformed messages bring down the AppServer
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * SIB0112b.ms.1   07/08/06 gareth   Large message support.
 * 454303          26/07/07 gareth   Various FINDBUGS changes
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * 538096          23/07/08 susana   Use getInMemorySize for spilling & persistence
 * ============================================================================
 */

import java.util.ArrayList;
import java.util.List;

import com.ibm.ws.sib.msgstore.AbstractItem;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.NotInMessageStore;
import com.ibm.ws.sib.msgstore.PersistentDataEncodingException;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink;
import com.ibm.ws.sib.msgstore.persistence.BatchingContext;
import com.ibm.ws.sib.msgstore.persistence.Persistable;
import com.ibm.ws.sib.msgstore.persistence.impl.CachedPersistableImpl;
import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;
import com.ibm.ws.sib.msgstore.transactions.impl.TransactionState;
import com.ibm.ws.sib.utils.DataSlice;

import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;

public final class AddTask extends Task
{
    private final class CachedPersistable extends CachedPersistableImpl
    {
        private long _cachedLockId;
        private List<DataSlice> _cachedMemberData;
        private int _cachedInMemorySize;
        private boolean _isMemberDataCached;

        // Feature SIB0112b.ms.1
        public CachedPersistable(Persistable masterPersistable, boolean copyMemberData) throws PersistentDataEncodingException, SevereMessageStoreException
        {
            super(masterPersistable);

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>$CachedPersistable");

            // The member data is copied conditionally depending on the item's storage
            // hints evaluated in the call to the constructor for this object
            if (copyMemberData)
            {
                List<DataSlice> memberData = masterPersistable.getData();
                if ((null == memberData) || _isPersistentDataImmutable)
                {
                    _cachedMemberData = memberData;
                }
                else
                {
                    // Use the copy constructor of ArrayList to
                    // take a copy of the list.
                    _cachedMemberData = new ArrayList<DataSlice>(memberData);
                }

            }

            // The inMemorySize is cached, as it could change due to flattening/encoding of the
            // member data. We want the value returned here to be constant, otherwise, we're at risk
            // of another class getting its sums wrong. This length is an approximation of the size
            // of the member data in memory.
            _cachedInMemorySize = masterPersistable.getInMemoryByteSize();

            _isMemberDataCached = copyMemberData;

            // The lockid is always copied since the storage hints only apply to the
            // member data. Even if we don't copy the member data at the time that the
            // AddTask is created, we must copy the lock id since the MS user can
            // immediately lock the item and we don't want to risk a timing oddity
            _cachedLockId = masterPersistable.getLockID();

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>$CachedPersistable");
        }

        public List<DataSlice> getData() throws PersistentDataEncodingException, SevereMessageStoreException
        {
            if (!_isMemberDataCached)
            {
                return _masterPersistable.getData();
            }
            return _cachedMemberData;
        }

        public int getInMemoryByteSize()
        {
            return _cachedInMemorySize;
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

        // Feature SIB0112b.ms.1
        public void cacheMemberData() throws PersistentDataEncodingException, SevereMessageStoreException
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "cacheMemberData");

            if (!_isMemberDataCached)
            {
                List<DataSlice> memberData = _masterPersistable.getData();

                if ((null == memberData) || _isPersistentDataImmutable)
                {
                    _cachedMemberData = memberData;
                }
                else
                {
                    // Use the copy constructor of ArrayList to
                    // take a copy of the list.
                    _cachedMemberData = new ArrayList<DataSlice>(memberData);
                }
                _isMemberDataCached = true;
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "cacheMemberData");
        }
    }

    private static TraceNLS nls = TraceNLS.getTraceNLS(MessageStoreConstants.MSG_BUNDLE);
    private static TraceComponent tc = SibTr.register(AddTask.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);

    private CachedPersistable _cachedPersistable = null;
    private Persistable _masterPersistable;
    private final boolean _isPersistentDataImmutable;
    private final boolean _isPersistentDataNeverUpdated;

    public AddTask(AbstractItemLink link) throws SevereMessageStoreException
    {
        super(link);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", link);

        // 258179 - moved called to Task superclass - getItem();
        _masterPersistable = super.getPersistable();
        _isPersistentDataImmutable = getItem().isPersistentDataImmutable();
        _isPersistentDataNeverUpdated = getItem().isPersistentDataNeverUpdated();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>", this);
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#abort(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void abort(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "abort", transaction);

        getLink().abortAdd(transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "abort");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#commitStage2(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void commitExternal(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "commitExternal", transaction);

        // I did not expect that - the xaCommit for all Add task
        // must be called *after* all internalCommitAdd() calls
        // on the same transaction.  So we move the xaCommit call
        // to the 'external' commit phase.
        getLink().commitAdd(transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "commitExternal");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#commitStage1(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void commitInternal(final PersistentTransaction transaction)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "commitInternal", transaction);

        getLink().internalCommitAdd();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "commitInternal");
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#copyDataIfVulnerable()
     */
    public final void copyDataIfVulnerable() throws PersistentDataEncodingException, SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "copyDataIfVulnerable");

        // This is a conditional caching or copy of the data, only taken if the data might
        // be updated by the MS user or if the item cannot guarantee that the
        // item's data can be used by the MS at any time without needing a separate copy
        if (_cachedPersistable == null)
        {
            _cachedPersistable = new CachedPersistable(_masterPersistable,
                                                       !(_isPersistentDataImmutable &&
                                                         _isPersistentDataNeverUpdated));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "copyDataIfVulnerable");
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#ensureDataAvailable()
     */
    public void ensureDataAvailable() throws PersistentDataEncodingException, SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "ensureDataAvailable");

        // Check whether the item is still in the store
        AbstractItem item = getItem();
        if ((item == null) || !item.isInStore())
        {
            throw new NotInMessageStore();
        }

        // This is an unconditional caching or copy of the data, compared with a conditional
        // copy of the data which is taken by copyDataIfVulnerable
        if (_cachedPersistable == null)
        {
            _cachedPersistable = new CachedPersistable(_masterPersistable, true);
        }
        else
        {
            _cachedPersistable.cacheMemberData();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "ensureDataAvailable");
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
     * @see com.ibm.ws.sib.msgstore.task.Task#getTaskType()
     */
    public Task.Type getTaskType()
    {
        return Type.ADD;
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#isCreateOfPersistentRepresentation()
     */
    public final boolean isCreateOfPersistentRepresentation()
    {
        return true;
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
            || (tranState == TransactionState.STATE_PREPARING)
            || (tranState == TransactionState.STATE_PREPARED))
        {
            size = DEFAULT_TASK_PERSISTABLE_SIZE_APPROXIMATION + getPersistable().getInMemoryByteSize();
        }
        else if ((tranState == TransactionState.STATE_ROLLEDBACK)
                 || (tranState == TransactionState.STATE_ROLLINGBACK))
        {
            size = DEFAULT_TASK_PERSISTABLE_SIZE_APPROXIMATION;
        }
        else if (tranState == TransactionState.STATE_COMMITTING_2PC)
        {
            size = 0;
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
     * @see com.ibm.ws.sib.msgstore.persistence.Operation#persist(com.ibm.ws.sib.msgstore.persistence.BatchingContext, int)
     */
    public final void persist(BatchingContext batchingContext, TransactionState tranState)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "persist", new Object[] { batchingContext, tranState});

        if ((tranState == TransactionState.STATE_COMMITTED)
            || (tranState == TransactionState.STATE_COMMITTING_1PC)
            || (tranState == TransactionState.STATE_PREPARING)
            || (tranState == TransactionState.STATE_PREPARED))
        {
            batchingContext.insert(getPersistable());
        }
        else if ((tranState == TransactionState.STATE_ROLLEDBACK)
                 || (tranState == TransactionState.STATE_ROLLINGBACK))
        {
            batchingContext.delete(getPersistable());
        }
        else if (tranState == TransactionState.STATE_COMMITTING_2PC)
        {
            //Do nothing.
        }
        else
        {
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
            item.eventPostRollbackAdd(transaction);
        }
        getLink().postAbortAdd(transaction);

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
            item.eventPostCommitAdd(transaction);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "postCommit");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.xalist.Task#preCommit(com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void preCommit(final PersistentTransaction transaction) throws SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "preCommit", transaction);

        getLink().preCommitAdd(transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "preCommit");
    }
}
