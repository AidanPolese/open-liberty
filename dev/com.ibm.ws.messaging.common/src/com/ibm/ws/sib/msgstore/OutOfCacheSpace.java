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
 * Reason          Date        Origin       Description
 * --------------- ----------  -----------  --------------------------------------------
 *                 27/10/2003  van Leersum  Original
 * LIDB3706-5.241  19/01/05    gareth       Add Serialization support
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore;

/**
 * @author DrPhill
 *
 */
public final class OutOfCacheSpace extends MessageStoreException
{
    private static final long serialVersionUID = -2171929279392183745L;

    public OutOfCacheSpace()
    {
        super();
    }
}
