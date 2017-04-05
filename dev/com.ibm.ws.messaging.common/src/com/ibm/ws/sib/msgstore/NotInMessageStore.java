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
 * Exception indicating that the item is not in the message store, and therefore
 * the requested operation is invalid.
 */
public final class NotInMessageStore extends SevereMessageStoreException
{
    private static final long serialVersionUID = -1595749431001619585L;
}
