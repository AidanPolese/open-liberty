package com.ibm.ws.sib.msgstore.cache.statemodel;
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
 * LIDB3706-5.239  19/01/05 gareth   Add Serialization support
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.SevereMessageStoreException;

/**
 * Our very own severe exception for state errors
 */
public class StateException extends SevereMessageStoreException 
{
    private static final long serialVersionUID = 8031640561007073391L;

    public StateException(String message) 
    {
        super(message);
    }
}
