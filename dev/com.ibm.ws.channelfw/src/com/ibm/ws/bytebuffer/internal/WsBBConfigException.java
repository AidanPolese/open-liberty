//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 09/21/04 gilgen      233448          Add copyright statement and change history.
// 01/20/05 gilgen      LIDB3706-5.165  Add Serialization UID for LI 3706

package com.ibm.ws.bytebuffer.internal;

/**
 * Exception thrown if the byte buffer service's configuration is
 * found to be incorrect.
 */
public class WsBBConfigException extends Exception {
    // required SUID since Exception extends Throwable, which is serializable
    private static final long serialVersionUID = 5562203322439138676L;

    /**
     * Constructor for WsBBConfigException.
     * 
     * @param message
     */
    public WsBBConfigException(String message) {
        super(message);
    }

    /**
     * Constructor for WsBBConfigException.
     * 
     * @param message
     * @param t throwable to wrap around
     */
    public WsBBConfigException(String message, Throwable t) {
        super(message, t);
    }

}
