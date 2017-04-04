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
 *                 27/10/03 drphill  Original
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception                
 * ============================================================================
 */

public interface ItemMembership extends Membership 
{
    /*
     * @return owning itemStream or null if none.
     */
    public ItemStream getOwningItemStream() throws SevereMessageStoreException;
    
    /*
     * @return the reference count
     */
    public int getReferenceCount();

    /**
     * This method is part of the interface between the MessageStoreInterface
     * component and the MessageStoreImplementation component.  It should 
     * only be used by MessageStore Code.<br> 
     * @return the item 
     */
    public AbstractItem getItem() throws SevereMessageStoreException;
}
