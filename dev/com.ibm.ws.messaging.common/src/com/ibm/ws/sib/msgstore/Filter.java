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
 * Reason          Date   Origin       Description
 * --------------- ------ --------     --------------------------------------------
 *                 030203 van Leersum  Original
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore;


/**
 * Defines the interface to callback used to determine 'match' during a filtered
 * get or browse operation.
 * 
 * @author drphill
 */
public interface Filter {
	
	/**
	 * Method filterMatches.
	 * <p>this method is called to determine if any particular {@link AbstractItem}
	 * is a suitable match for the purposes of the receiver. Implementors
	 * of the filter should use this method to indicate suitable matches
	 * by returning true.</p>
	 * <p>The order in which the {@link AbstractItem}s are tested is dependant upon 
	 * the implementation of the {@link ItemStream} being traversed.  The 
     * implemention should be confined to performing the test, and should not
	 * attempt to side effect the {@link ItemStream} or the {@link AbstractItem}s in 
     * any way.</p>
     * <p> For any given instance of cursor, this method must always return the same 
     * result for the same item presented.
     * </p>
	 * @param abstractItem {@link AbstractItem} to be tested
	 * @return boolean true if this member is a suitable match, false
	 * otherwise.
	 */
	public boolean filterMatches(AbstractItem abstractItem) throws MessageStoreException ;
	
}
