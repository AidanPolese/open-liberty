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
 * A cursor for for iterating over ObjectManager Lists.
 * Extended for transactions.
 * 
 * @see java.util.ListIterator
 */
public interface ListIterator extends Iterator
{
    /**
     * Returns <tt>true</tt> if this list iterator has more elements when
     * traversing the list in the reverse direction. (In other words, returns
     * <tt>true</tt> if <tt>previous(Transaction)</tt> would return an element rather than
     * throwing an exception.)
     * 
     * @param Transaction which determines the visibility of elements.
     * @return <tt>true</tt> if the list iterator has more elements when
     *         traversing the list in the reverse direction.
     * @throws ObjectManagerException.
     */
    boolean hasPrevious(Transaction transaction) throws ObjectManagerException;

    /**
     * Returns <tt>true</tt> if this list iterator has more dirty elements when
     * traversing the list in the reverse direction. (In other words, returns
     * <tt>true</tt> if <tt>previous()</tt> would return an element rather than
     * throwing an exception.)
     * 
     * @param transaction - the transaction which determines the visibility of elements.
     * @return <tt>true</tt> if the list iterator has more elements when
     *         traversing the list in the reverse direction.
     * @throws ObjectManagerException.
     */
    boolean hasPrevious() throws ObjectManagerException;

    /**
     * Returns the previous element in the list. This method may be called
     * repeatedly to iterate through the list backwards, or intermixed with
     * calls to <tt>next</tt> to go back and forth. Alternating
     * calls to <tt>next</tt> and <tt>previous</tt> may not return the same
     * element if transaction states change.
     * 
     * @param Transaction which determines the visibility of elements.
     * @return the previous element in the list.
     * 
     * @exception java.util.NoSuchElementException if the iteration has no previous
     *                element.
     * @throws ObjectManagerException.
     */
    Object previous(Transaction transaction) throws ObjectManagerException;

    /**
     * Returns the previous element in the dirty list. This method may be called
     * repeatedly to iterate through the list backwards, or intermixed with
     * calls to <tt>next</tt> to go back and forth. (Note that alternating
     * calls to <tt>next</tt> and <tt>previous</tt> will return the same
     * element repeatedly.)
     * 
     * @return the previous element in the list.
     * 
     * @exception java.util.NoSuchElementException if the iteration has no previous
     *                element.
     * @throws ObjectManagerException.
     */
    Object previous() throws ObjectManagerException;

    /**
     * Returns the index of the element that would be returned by a subsequent
     * call to <tt>next</tt>. (Returns list size if the list iterator is at the
     * end of the list.)
     * 
     * @param transaction - the transaction which determines the visibility of elements.
     * @return long the index of the element that would be returned by a subsequent
     *         call to <tt>next</tt>, or list size if list iterator is at end
     *         of list.
     * @exception UnsupportedOperationException if the <tt>add</tt> method is
     *                not supported by this list iterator.
     */
    long nextIndex(Transaction transaction);

    /**
     * Returns the index of the element that would be returned by a subsequent
     * call to <tt>previous</tt>. (Returns -1 if the list iterator is at the
     * beginning of the list.)
     * 
     * @param transaction - the transaction which determines the visibility of elements.
     * @return long the index of the element that would be returned by a subsequent
     *         call to <tt>previous</tt>, or -1 if list iterator is at
     *         beginning of list.
     * @exception UnsupportedOperationException if the <tt>add</tt> method is
     *                not supported by this list iterator.
     */
    long previousIndex(Transaction transaction);

    /**
     * Replace the Token in the elemts under the cursor with a new Token.
     * 
     * @param Token to replace the existine one, may ne null.
     * @param Transaction controling the update.
     * @exception InvalidStateException if the result of a previous next()/next(Transaction)
     *                method is not eligible for setting, for exampe because it has already been
     *                removed.
     * @exception IllegalStateException if neither <tt>next</tt> nor
     *                <tt>previous</tt> have been called, or <tt>remove</tt> or
     *                <tt>add</tt> have been called after the last call to
     *                <tt>next</tt> or <tt>previous</tt>.
     * @throws ObjectManagerException.
     */
    void set(Token token,
             Transaction transaction)
                    throws ObjectManagerException;

    /**
     * Inserts a Token into the list before the current cursor position.
     * 
     * @param Token to insert.
     * @param Transaction which controls the insertion.
     * 
     * @throws ObjectManagerException.
     */
    void add(Token token,
             Transaction transaction)
                    throws ObjectManagerException;
} // interface ListIterator.