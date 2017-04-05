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
 * Reason          Date      Origin       Description
 * --------------- --------  -----------  -------------------------------------
 *                 27/10/03  van Leersum  Original
 * LIDB3706-5.241  19/01/05  gareth       Add Serialization support
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore;

/**
 * Exception to indicate that the owning stream has exceeded its storage
 * limit.
 * 
 * @author DrPhill
 *
 */
public final class StreamIsFull extends MessageStoreException
{
    private static final long serialVersionUID = -2236937663160002456L;

    public StreamIsFull()
    {
        super();
    }
}
