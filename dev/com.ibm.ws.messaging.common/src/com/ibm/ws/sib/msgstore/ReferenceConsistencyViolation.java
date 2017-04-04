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
 * LIDB3706-5.241  19/01/05 gareth   Add Serialization support
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

/**
 * This class is thrown to indicate that the (admittedly convoluted) reference consistency
 * rules have been violated.  The reference consistency rules are:
 * <ul> 
 * <li>an item reference can only be added to a reference stream in the same transaction as the
 * referred item is added to its item stream.</li>
 * <li>an item reference can only be added to a reference stream owned by the item stream
 * that owns the referred item.</li>
 * </ul>
 */
public final class ReferenceConsistencyViolation extends SevereMessageStoreException
{
    private static final long serialVersionUID = -9217769294938439356L;

    public ReferenceConsistencyViolation(String message, Object[] inserts)
    {
        super(message, inserts);
    }
}
