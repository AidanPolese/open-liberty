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

public final class InvalidAddOperation extends SevereMessageStoreException
{
    private static final long serialVersionUID = 8943647275303324403L;

    public InvalidAddOperation(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public InvalidAddOperation(String message, Object insert)
    {
        super(message, new Object[]{insert});
    }
}
