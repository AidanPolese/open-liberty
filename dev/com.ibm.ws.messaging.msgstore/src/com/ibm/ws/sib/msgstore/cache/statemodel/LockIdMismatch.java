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
 * --------------- ----------  -----------  -----------------------------------
 *                 27/10/2003  van Leersum  Original
 * LIDB3706-5.239  19/01/2005  gareth      Add Serialization support
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.cache.statemodel;

/**
 * @author DrPhill
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class LockIdMismatch extends StateException 
{
    private static final long serialVersionUID = 6507593834512130570L;

    public LockIdMismatch(long expected, long given) 
    {
        super("{"+expected+"/"+given+"}");
    }
}
