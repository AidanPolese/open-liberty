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
 * Thrown to indicate that an add operation was performed with an item that does not
 * have a valid constructor.  A valid constructor for the message store purposes
 * must be public, accessible, and take no arguments.  This exception will also be
 * thrown if the item being added is a non-static inner class.
 */
public final class InvalidConstructor extends SevereMessageStoreException
{
    private static final long serialVersionUID = -8577657583093944999L;

    public InvalidConstructor(String message, Throwable exception)
    {
        super(message, exception);
    }
}
