package com.ibm.ws.sib.msgstore;

/*
 * 
 * 
 * ============================================================================
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2012,2014
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
 * 186549.1        01/06/04 corrigk  Added methods for MBean
 * 224000          16/08/04 corrigk  Checked exceptions on getUniqueXxx
 * 225627          24/08/04 corrigk  Add storageStrategy to getUniqueLockId()
 * 223636.2        26/08/04 corrigk  Consolidate dump
 * 288073          13/07/05 schofiel Dump consolidation
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 492055          27/05/08 susana   Allow dump to dump internal & persisted data
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * 572575          28/01/09 gareth   Allow data store lock to be disabled
 * F008622         14/12/11 urwashi  Added new method to support the new recovery command to intialize recovery ME
 * 90454           30/01/13 sanjay   Fix for enabling listPeparedTransactions 
 * ============================================================================
 */

import java.io.IOException;

import com.ibm.ws.sib.admin.JsRecoveryMessagingEngine;
import com.ibm.ws.sib.msgstore.transactions.Transaction;
import com.ibm.ws.sib.transactions.TransactionFactory;
import com.ibm.ws.sib.utils.ras.FormattedWriter;

public interface MessageStoreInterface
{
    /**
     * @param itemStream
     * @param transaction
     * @throws MessageStoreException
     */
    public abstract void add(ItemStream itemStream, Transaction transaction) throws MessageStoreException;

    /**
     * @param itemStream
     * @param lockID id under which the itemStream will be locked when the add is
     *            complete, or {@link AbstractItem#NO_LOCK_ID} if the added itemStream is to
     *            be unlocked.
     * @throws MessageStoreException
     */
    public abstract void add(ItemStream itemStream, long lockID, Transaction transaction) throws MessageStoreException;

    /**
     * Reply the itemStream in the message store with a matching ID. The item returned
     * stream is neither removed from the message store nor locked for exclusive use of the caller.
     * 
     * @param itemStreamID
     * @return itemStream found or null if none.
     * @throws MessageStoreException
     * 
     * @deprecated This method implies too much of a performance bottle-neck and
     *             has the disadvantage that items in non-initialized streams will not be found.
     *             Use instead:
     *             <ul>
     *             <li>{@link #findByStreamId(long)} to find top level itemStreams.</li>
     *             <li>{@link ItemStream#findById(long)} to find an item within an item stream.</li>
     *             <li>{@link ReferenceStream#findById(long)} to find an item within a reference stream.</li>
     *             </ul>
     */
    @Deprecated
    public AbstractItem findById(long itemStreamID) throws MessageStoreException;

    /**
     * Reply the itemStream in the message store with a matching ID. The item returned
     * stream is neither removed from the message store nor locked for exclusive use of
     * the caller.
     * This method only looks in the root of the message store - ie only directly contained
     * itemStreams will be located.
     * 
     * @param itemStreamID
     * @return itemStream found or null if none.
     * @throws MessageStoreException
     */
    public abstract ItemStream findByStreamId(long itemStreamID) throws MessageStoreException;

    /**
     * Return, but do not remove, first match for filter.
     * 
     * @param filter
     * @return {@link ItemStream}
     * @throws MessageStoreException
     */
    public abstract ItemStream findFirstMatching(Filter filter) throws MessageStoreException;

    /**
     * @return Statistics object describing the cache used to hold
     *         items that are {@link AbstractItem#STORE_EVENTUALLY}, {@link AbstractItem#STORE_ALWAYS}, or {@link AbstractItem#STORE_MAYBE}.
     *         <p>
     *         This object is valid for the lifetime of the message store and so
     *         can be cached for easy access.
     *         </p>
     */
    public abstract CacheStatistics getStoredCacheStatistics();

    /**
     * @return Statistics object describing the cache used to hold
     *         items that are {@link AbstractItem#STORE_NEVER}.
     *         <p>
     *         This object is valid for the lifetime of the message store and so
     *         can be cached for easy access.
     *         </p>
     */
    public abstract CacheStatistics getNonStoredCacheStatistics();

    /**
     * Returns the current size of the expiry index - for debug only.
     * 
     * @return the size of the expiry index.
     */
    public abstract int getExpiryIndexSize();

    /**
     * @param storageStrategy
     * @return a unique value from the lockID value space. All lockIDs are allocated from
     *         the same 'number space', and so can be relied upon to be unique for the messageStore
     *         instance. Uniqueness of lockIDs is persisted.
     */
    public abstract long getUniqueLockID(int storageStrategy) throws PersistenceException;

    /**
     * @return a unique value from the tickCount value space. All tickCounts are allocated from
     *         the same 'number space', and so can be relied upon to be unique for the messageStore
     *         instance. Uniqueness of tickCounts is persisted.
     *         Tick values will generally increase in value (except when/if a roll-over
     *         occurs).
     */
    public abstract long getUniqueTickCount() throws PersistenceException;

    /**
     * Initialize method, useful as an intermediate form until JsEngineComponent
     * initialization is linked in.
     * 
     * @param config
     */
    public abstract void initialize(final Configuration config);

    /**
     * @param filter
     * @return a non-Locking Cursor on the stream.
     * @throws {@link MessageStoreException}
     */
    public abstract NonLockingCursor newNonLockingCursor(Filter filter) throws MessageStoreException;

    /**
     * Remove and return first match for filter.
     * 
     * @param filter
     * @param transaction
     * @return {@link ItemStream}
     * @throws MessageStoreException
     */
    public abstract ItemStream removeFirstMatching(Filter filter, Transaction transaction)
                    throws MessageStoreException;

    /**
     * Request that the receiver prints its xml representation
     * (recursively) onto standard out.
     * 
     * @throws IOException
     */
    public abstract void xmlRequestWriteOnSystemOut() throws IOException;

    /**
     * Request that the receiver prints its xml representation
     * (recursively) onto writer
     * 
     * @param fw the FormattedWriter
     * @throws IOException
     */
    public abstract void xmlRequestWriteOnFile(FormattedWriter fw) throws IOException;

    /**
     * Request that the receiver prints its xml representation
     * (recursively) onto writer.
     * 
     * @param writer
     * @throws IOException
     */
    public abstract void xmlWriteOn(FormattedWriter writer) throws IOException;

    /**
     * @return {@link TransactionFactory}
     */
    public abstract TransactionFactory getTransactionFactory();

    /**
     * Allow the expirer to begin its work. No expiry will take place until
     * this method has been called.
     * 
     * Note: for logistical reasons this method will not be implemented
     * until after the message processor has implemented the calls to it.
     */
    public abstract void expirerStart() throws SevereMessageStoreException;

    /**
     * Allow the expirer to stop its work. No expiry will take place after
     * this method has been called.
     * 
     */
    public abstract void expirerStop();

    /**
     * Allow the deliveryDelayManager to begin its work. No Unloking will take place until
     * this method has been called.
     * 
     * Note: for logistical reasons this method will not be implemented
     * until after the message processor has implemented the calls to it.
     */
    public abstract void deliveryDelayManagerStart() throws SevereMessageStoreException;

    /**
     * Allow the deliveryDelayManager to stop its work. No Unlocking will take place after
     * this method has been called.
     * 
     */
    public abstract void deliveryDelayManagerStop();

    /**
     * Request the receiver to dump its xml representation.
     * 
     * @param fw the FormattedWriter passed in by the ME.
     * @param arg an optional string to specifiy the information to be dumped
     *            null to invoke the internal diagnostics dump
     *            "raw" to invoke the raw dump.
     *            "all" to invoke the internal & raw dump, including item information
     */
    void dump(FormattedWriter fw, String arg);

    /**
     * Obtain a list of XIDs which are in-doubt.
     * Part of MBean interface for resolving in-doubt transactions in Message Store.
     * 
     * @return the array of XIDs as strings
     * 
     * @return The list of known prepared transactions
     */
    public String[] listPreparedTransactions();

    /**
     * Commit the given transaction.
     * Part of MBean interface for resolving in-doubt transactions in Message Store.
     * 
     * @param xid a string representing the xid of the transaction to be committed.
     */
    public void commitPreparedTransaction(String xid) throws TransactionException, PersistenceException;

    /**
     * Rollback the given transaction.
     * Part of MBean interface for resolving in-doubt transactions in Message Store.
     * 
     * @param xid a string representing the xid of the transaction to be rolled back.
     */
    public void rollbackPreparedTransaction(String xid) throws TransactionException, PersistenceException;

    // Defect 572575
    /**
     * This method allows the datastore exclusive access lock to
     * be disabled for a period of time. While disabled in this
     * manner the ME will continue to function as normal and will
     * not stop work from being carried out unlike in the case
     * where the lock is lost due to unknown reasons.
     * 
     * @param period The length of time in milliseconds that the
     *            datastore lock will be disabled.
     */
    public void disableDataStoreLock(Long period);

    /**
     * Initialize method, used when starting the messagestore in recovery mode.
     * It initializes the message store with instance of JsRecoveryMessagingEngine
     * 
     * @param me The JsRecoveryMessagingEngine instance used for intializing the message store.
     * @param mode The start mode of the message store.
     */
    public abstract void initialize(JsRecoveryMessagingEngine me, String mode);
}
