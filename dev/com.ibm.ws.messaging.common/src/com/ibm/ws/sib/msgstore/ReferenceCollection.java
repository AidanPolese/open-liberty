package com.ibm.ws.sib.msgstore;
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
 * 224000          16/08/04 corrigk  Checked exceptions in getUniqueXxx
 * SIB0002.ms.1    28/07/05 schofiel Changes for remote MQ subordinate resources
 * 290610          09/08/05 schofiel Statistics enhancements
 * 278082          03/01/06 schofiel Parameter on factory method to disable cursor jumpback
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.transactions.Transaction;

/**
 * This class is intended as a bridge between the MessageStoreInterface
 * component and the MessageStoreImplementation component.  It should 
 * only be used by MessageStore Code. 
 */
public interface ReferenceCollection extends Membership 
{
    /**
     * @param reference
     * @param lockID id under which the reference will be locked when the add is 
     * complete, or {@link AbstractItem#NO_LOCK_ID} if the added reference is to
     * be unlocked.
     * @param transaction
     * 
     * @throws {@link OutOfCacheSpace} if there is not enough space in the 
     * unstoredCache and the storage strategy is {@link AbstractItem#STORE_NEVER}.
     *  
     * @throws {@link StreamIsFull} if the size of the stream would exceed the 
     * maximum permissable size if an add were performed.
     *
     * @throws {@ProtocolException} Thrown if an add is attempted when the 
     * transaction cannot allow any further work to be added i.e. after 
     * completion of the transaction.
     * 
     * @throws {@TransactionException} Thrown if an unexpected error occurs.
     * 
     * @throws {@PersistenceException} Thrown if a database error occurs.
     */
    public void addReference(final ItemReference reference, long lockID, final Transaction transaction)
        throws OutOfCacheSpace, ProtocolException, StreamIsFull, TransactionException, PersistenceException, SevereMessageStoreException;

    /**
     * Reply the item in the receiver with a matching ID.  The item returned
     * stream is neither removed from the message store nor locked for exclusive use
     * of the caller.
     * 
     * @param itemId
     * 
     * @return item found or null if none.
     * @throws MessageStoreException
     */
    public AbstractItem findById(long itemId) throws SevereMessageStoreException;


    /*
     * @param filter
     * @param transaction
     * @return {@link AbstractItem}
     */
    public AbstractItem findFirstMatching(final Filter filter) throws MessageStoreException;

    /*
     * Find the item that has been known to the stream for longest.  The item returned
     * may be in any of the states defined in the state model. The caller should not
     * assume that the item can be used for any particular purpose.
     * @return Item, may be null.
     * @throws {@link MessageStoreException} if the item was spilled and could not
     * be unspilled.  Or if item not found in backing store.
     */
    public AbstractItem findOldestItem() throws MessageStoreException;

    /**
     * @return owning itemStream or null if none.
     */
    public ItemStream getOwningItemStream() throws SevereMessageStoreException;

    public Statistics getStatistics() throws SevereMessageStoreException;

    /**
     * @return true if the list is spilling.
     */
    public boolean isSpilling();

    /*
     * @param filter
     * @return
     */
    public LockingCursor newLockingCursor(Filter filter, boolean jumpbackEnabled) throws PersistenceException, SevereMessageStoreException;

    /*
     * @param filter
     * @return
     */
    public NonLockingCursor newNonLockingCursor(Filter filter) throws SevereMessageStoreException;

    /*
     * This method is part of the interface between the MessageStoreInterface
     * component and the MessageStoreImplementation component.  It should 
     * only be used by MessageStore Code.<br> 
     * @param filter
     * @param transaction
     * @return {@link AbstractItem}
     */
    public AbstractItem removeFirstMatching(final Filter filter, final Transaction transaction)
        throws MessageStoreException;

    public void setWatermarks(long countLow, long countHigh, long bytesLow, long bytesHigh) ;

}
