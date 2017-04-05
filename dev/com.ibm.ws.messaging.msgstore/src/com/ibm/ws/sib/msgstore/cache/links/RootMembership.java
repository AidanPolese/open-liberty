package com.ibm.ws.sib.msgstore.cache.links;
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
 *  Reason         Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *                 26/06/03 drphill  Original
 * 176779          12/09/03 pradine  Adapted to new Tuple interface
 * 176788          17/09/03 pradine  Added getCanExpireSilently()
 * 180053          03/11/03 gareth   Remove deprecated methods/interfaces
 * 188010          22/01/04 pradine  Clean up of persistence layer interfaces
 * 180763.7        10/02/04 pradine  Add support for mutiple item tables
 * 187223.4        05/03/04 pradine  Non-cache expirer support
 * 196685          31/03/04 pradine  Changes to spill behaviour
 * 201684.1        25/06/04 pradine  Misleading method names
 * 214830.2        12/07/04 pradine  MessageStore CacheLoader Implementation
 * 223636.2        26/08/03 corrigk  Consolidate dump
 * 247511          05/01/05 drphill  remove call to setter for parentStatistics
 * 270103          27/04/05 schofiel sib.msgstore.OutOfCacheSpace exceptions before mediation
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 291186          08/11/05 schofiel Refactor lists to improve CPU utilisation
 * 306998.20       09/01/06 gareth   Add new guard condition to trace statements
 * 410652          12/04/07 gareth   Check Transactions ME at add time
 * SIB0112d.ms.2   28/06/07 gareth   MemMgmt: SpillDispatcher improvements - datastore
 * 454303          26/07/07 gareth   Various FINDBUGS changes
 * 463642          04/09/07 gareth   Revert to using spill limits
 * 484799          28/01/08 gareth   Take byte size into account in spill limits
 * 520772          14/05/08 djvines  Synchronize _nextItemStreamSequenceToIssue in _buildStreamTree
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ibm.ws.sib.msgstore.Filter;
import com.ibm.ws.sib.msgstore.ItemStream;
import com.ibm.ws.sib.msgstore.MessageStore;
import com.ibm.ws.sib.msgstore.MessageStoreConstants;
import com.ibm.ws.sib.msgstore.MessageStoreException;
import com.ibm.ws.sib.msgstore.MismatchedMessageStoreException;
import com.ibm.ws.sib.msgstore.NonLockingCursor;
import com.ibm.ws.sib.msgstore.OutOfCacheSpace;
import com.ibm.ws.sib.msgstore.PersistenceException;
import com.ibm.ws.sib.msgstore.ReferenceStream;
import com.ibm.ws.sib.msgstore.SevereMessageStoreException;
import com.ibm.ws.sib.msgstore.cache.statemodel.ListStatistics;
import com.ibm.ws.sib.msgstore.impl.MessageStoreImpl;
import com.ibm.ws.sib.msgstore.list.LinkedList;
import com.ibm.ws.sib.msgstore.list.UnprioritizedNonlockingCursor;
import com.ibm.ws.sib.msgstore.persistence.Persistable;
import com.ibm.ws.sib.msgstore.persistence.PersistentMessageStore;
import com.ibm.ws.sib.msgstore.persistence.TupleTypeEnum;
import com.ibm.ws.sib.msgstore.transactions.impl.PersistentTransaction;
import com.ibm.ws.sib.msgstore.transactions.Transaction;
import com.ibm.ws.sib.utils.ras.FormattedWriter;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

public final class RootMembership extends LinkOwner
{
    public static final long MEMBER_KEY = -2;
    public static final long MEMBERSHIP_KEY = -3;

    private static TraceComponent tc = SibTr.register(RootMembership.class,
                                                      MessageStoreConstants.MSG_GROUP,
                                                      MessageStoreConstants.MSG_BUNDLE);

    private LinkedList _itemStreams;
    private final MessageStoreImpl _messageStore;

    private long _nextItemStreamSequenceToIssue = 0;
    // root may not need these eventually
    private final ListStatistics _statistics;

    public RootMembership(MessageStoreImpl messageStore, Persistable persistable) throws OutOfCacheSpace, SevereMessageStoreException
    {
        super(persistable, true);
        _messageStore = messageStore;
        _statistics = new ListStatistics(this);
        _restoreStateAvailable(this, null);
    }

    private void _buildStreamTree(final HashMap tupleMap) throws SevereMessageStoreException
    {
        // initialize the receiver and its children recursively
        // my children's tuples have key MEMBER_KEY
        _itemStreams = new LinkedList();
        ArrayList children = (ArrayList) tupleMap.remove(Long.valueOf(this.getID()));
        for (int i = 0; null != children && i < children.size(); i++)
        {
            Persistable tuple = (Persistable) children.get(i);
            tuple.setContainingStream(getTuple());
            ItemStreamLink childLink = null;
            if (tuple.getTupleType().equals(TupleTypeEnum.ITEM_STREAM))
            {
                childLink = new ItemStreamLink(this, tuple);
            }
            else
            {
                throw new SevereMessageStoreException("Wrong tuple type in ItemStream:" + tuple.getTupleType());
            }
            if (null != childLink)
            {
                childLink.restoreState(tuple);
                if (childLink.isInStore())
                {
                    _messageStore.register(childLink);
                }
                childLink._initializeChildren(tupleMap);
            }
            // notch up the sequence number.  Note this is based on the tuple rather
            // than the link, as links may be removed by expiry.
            long seq = tuple.getSequence();
            synchronized(this)
            {
                if (seq >= _nextItemStreamSequenceToIssue)
                {
                    _nextItemStreamSequenceToIssue = seq + 1;
                }
            }
        }
    }
    private final HashMap _buildTupleMap(PersistentMessageStore pm) throws PersistenceException
    {
        final HashMap tupleMap = new HashMap();
        // get all the tuples from the database
        List list = pm.readAllStreams();
        Iterator tuples = list.iterator();
        // sort the tuples into collections of siblings keyed by their
        // parents id.
        while (tuples.hasNext())
        {
            Persistable tuple = (Persistable) tuples.next();
            long parent = tuple.getContainingStreamId();
            Long key = Long.valueOf(parent);
            ArrayList siblings = (ArrayList) tupleMap.get(key);
            if (null == siblings)
            {
                siblings = new ArrayList();
                tupleMap.put(key, siblings);
            }
            siblings.add(tuple);
        }
        return tupleMap;
    }

    private void _recoverStreamsWithInDoubts(PersistentMessageStore pm) throws PersistenceException, SevereMessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "_recoverStreamsWithInDoubts");

        Iterator iter = pm.identifyStreamsWithIndoubtItems().iterator();
        while (iter.hasNext())
        {
            Long id = (Long) iter.next();
            LinkOwner link = (LinkOwner) _messageStore.getLink(id.longValue());
            if (null != link)
            {
                link.loadOwnedLinks();
            }
            else
            {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "no item?");
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "_recoverStreamsWithInDoubts");
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.ItemCollection#addItemStream(com.ibm.ws.sib.msgstore.ItemStream, com.ibm.ws.sib.msgstore.Transaction)
     */
    public final void addItemStream(final ItemStream itemStream, long lockID, final Transaction transaction) throws MessageStoreException
    {
        // Defect 410652
        // Check the transaction being used is from the same MessageStore as
        // ours so that we don't get a mismatch and run the possibility of
        // hitting a DuplicateKeyException at persistence time. If the MS's
        // don't match then the unique key generator being used for this
        // add could be using a range that has already been used in the MS
        // that will be used to persist the transaction.
        final MessageStoreImpl messageStore = getMessageStoreImpl();
        final MessageStore     tranStore    = ((PersistentTransaction)transaction).getOwningMessageStore();
        // We only need to do a simple equality check as all that we really
        // care about is that the same MS instance in memory is being used.
        if (messageStore != tranStore)
        {
            MismatchedMessageStoreException mmse = new MismatchedMessageStoreException("Transaction supplied on add does not originate from this MessageStore! MS: "+messageStore+", Tran.MS: "+tranStore);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.event(this, tc, "Transaction supplied on add does not originate from this MessageStore! MS: "+messageStore+", Tran.MS: "+tranStore, mmse);
            throw mmse;
        }

        int strategy = itemStream.getStorageStrategy();
        final long itemID = messageStore.getUniqueValue(strategy);
        TupleTypeEnum type = TupleTypeEnum.ITEM_STREAM;
        Persistable childPersistable = getTuple().createPersistable(itemID, type);
        childPersistable.setStorageStrategy(strategy);
        final AbstractItemLink link = new ItemStreamLink(itemStream, this, childPersistable);
        // Defect 463642
        // Revert to using spill limits previously removed in SIB0112d.ms.2
        link.setParentWasSpillingAtAddTime(getListStatistics().isSpilling());
        messageStore.registerLink(link, itemStream);
        link.cmdAdd(this, lockID, (PersistentTransaction)transaction);
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.impl.StreamLink#append(com.ibm.ws.sib.msgstore.impl.AbstractItemLink)
     * Used by AddTask to really attach the link.  Overridden here to catch itemStreams and
     * referenceStreams
     */
    public final void append(final AbstractItemLink link) throws SevereMessageStoreException
    {
        if (link.isItemStreamLink())
        {
            _itemStreams.append(link);
        }
        else
        {
            throw new SevereMessageStoreException("Cannot add this directly to message store");
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.impl.StreamLink#assertCanDelete(com.ibm.ws.sib.msgstore.transactions.Transaction)
     */
    public final SevereMessageStoreException assertCanDelete(PersistentTransaction transaction)
    {
        return new SevereMessageStoreException("cannot delete root");
    }

    // Defect 484799
    public final void checkSpillLimits()
    {
        // No action needed.
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.links.LinkOwner#eventWatermarkBreached()
     */
    public final void eventWatermarkBreached()
    {
        // Nothing to do
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.links.StreamLink#expirableExpire()
     */
    public final boolean expirableExpire()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.ItemCollection#findFirstMatchingItemStream(com.ibm.ws.sib.msgstore.Filter)
     */
    public final ItemStream findFirstMatchingItemStream(final Filter filter) throws MessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "findFirstMatchingItemStream", filter);

        ItemStream item = (ItemStream) _itemStreams.findFirstMatching(filter);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "findFirstMatchingItemStream", item);
        return item;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.links.StreamLink#getItemStream()
     */
    final ItemStream getItemStream()
    {
        return null;
    }
    /**
     * @return
     */
    public final ListStatistics getListStatistics()
    {
        return _statistics;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getMembershipKey()
     */
    public final long getMembershipKey()
    {
        return MEMBERSHIP_KEY;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.store.Membership#getMessageStore()
     */
    public final MessageStoreImpl getMessageStoreImpl()
    {
        return _messageStore;
    }

    public ListStatistics getParentStatistics()
    {
        return _statistics;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.links.StreamLink#getReferenceStream()
     */
    final ReferenceStream getReferenceStream()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.persistence.Tuple#getTupleType()
     */
    public final TupleTypeEnum getTupleType()
    {
        return TupleTypeEnum.ROOT;
    }

    /**
     * This method essentially drives the initial loading of the cache layer from the
     * persistence layer.
     * @throws SevereMessageStoreException 
     */
    public final void initialize() throws PersistenceException, SevereMessageStoreException
    {
        PersistentMessageStore pm = _messageStore.getPersistentMessageStore();
        final HashMap tupleMap = _buildTupleMap(pm);
        _buildStreamTree(tupleMap);
        _recoverStreamsWithInDoubts(pm);
    }

    public final void linkAvailable(AbstractItemLink link) {}

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.links.LinkOwner#loadOwnedLinks()
     */
    public final boolean loadOwnedLinks()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.ItemCollection#newNonLockingItemStreamCursor(com.ibm.ws.sib.msgstore.Filter)
     */
    public final NonLockingCursor newNonLockingItemStreamCursor(final Filter filter)
    {
        return new UnprioritizedNonlockingCursor(_itemStreams, filter);
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.links.StreamLink#nextSequence()
     */
    public synchronized long nextSequence()
    {
        return _nextItemStreamSequenceToIssue++;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.ItemCollection#removeFirstMatchingItemStream(com.ibm.ws.sib.msgstore.Filter, com.ibm.ws.sib.msgstore.Transaction)
     */
    public final ItemStream removeFirstMatchingItemStream(final Filter filter, final PersistentTransaction transaction) throws MessageStoreException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "removeFirstMatchingItemStream");

        ItemStream item = (ItemStream) _itemStreams.removeFirstMatching(filter, transaction);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "removeFirstMatchingItemStream", item);
        return item;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.cache.links.AbstractItemLink#xmlTagName()
     */
    protected final String xmlTagName()
    {
        return XML_ROOT_MEMBERSHIP;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.sib.msgstore.impl.AbstractItemLink#xmlWriteChildrenOn(com.ibm.ws.sib.msgstore.FormatBuffer)
     */
    protected final void xmlWriteChildrenOn(FormattedWriter writer) throws IOException
    {
        super.xmlWriteChildrenOn(writer);
        xmlWriteItemStreamsOn(writer);
    }

    public final void xmlWriteItemStreamsOn(FormattedWriter writer) throws IOException
    {
        if (null != _itemStreams)
        {
            _itemStreams.xmlWriteOn(writer, XML_ITEM_STREAMS);
        }
    }
}
