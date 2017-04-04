package com.ibm.ws.objectManager;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 *
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 *   251161        07/04/05   gareth    Add ObjectManager code to CMVC
 * ============================================================================
 */

/**
 * An cursor for Tokens in a Collection that is sensitive to the Transaction context.
 * 
 * @see java.util.Iterator.
 */
public interface Iterator
{
    /**
     * Returns <tt>true</tt> if the iteration has more elements.
     * 
     * @param Transaction which must see the next element.
     * @return boolean true if the iterator has more elements.
     * 
     * @throws java.util.ConcurrentModificationException if the cursor is no longer in the list.
     * @exception ObjectManagerException.
     */
    boolean hasNext(Transaction transaction)
                    throws ObjectManagerException;

    /**
     * A dirty version of hasNext(Transaction) which
     * returns <tt>true</tt> if the iteration has more elements including elements that
     * are being added deleted or modified by a transaction.
     * <p>
     * 
     * @return <tt>true</tt> if the iterator has more elements.
     * 
     * @throws java.util.ConcurrentModificationException if the cursor is no longer in the list.
     * @exception ObjectManagerException.
     */
    boolean hasNext() throws ObjectManagerException;

    /**
     * Returns the next element in the iteration.
     * 
     * @param transaction the transaction which sees the next element.
     * @return Object the next element in the iteration.
     * @exception java.util.NoSuchElementException iteration has no more elements.
     * @exception ObjectManagerException.
     */
    Object next(Transaction transaction) throws ObjectManagerException;

    /**
     * Returns the next element in the interation, including elements
     * being added removed or modified by a Transaction.
     * 
     * @return Object the next element in the iteration.
     * @exception java.util.NoSuchElementException iteration has no more elements.
     * @exception ObjectManagerException.
     */

    Object next() throws ObjectManagerException;

    /**
     * Removes from the underlying collection the last element returned by the
     * iterator. This method can be called only once per call to <tt>next</tt>.
     * 
     * @param Transaction which sees the next element and controls the removal.
     * @return Object which is either a Token List.Entry or Map.Entry.
     * @exception ObjectManagerException.
     * @exception java.lang.IllegalStateException if the <tt>next</tt> method has not
     *                yet been called, or the <tt>remove</tt> method has already
     *                been called after the last call to the <tt>next</tt>
     *                method.
     * @exception InvalidStateException if the result of a previous next()/next(Transaction)
     *                method is not eligible for removal, for exampe because it has already been
     *                removed.
     */
    Object remove(Transaction transaction) throws ObjectManagerException;
} // interface Iterator. 
