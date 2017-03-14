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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/InvalidChainNameException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:17 [1/4/05 10:03:42]

package com.ibm.wsspi.channelfw.exception;

/**
 * Chain name is equal to that of anothers.
 */
public class InvalidChainNameException extends ChainException {

    /** Serialization ID string */
    private static final long serialVersionUID = -2233425810071024316L;

    /**
     * Constructor with an exception message.
     * 
     * @param message
     */
    public InvalidChainNameException(String message) {
        super(message);
    }
}
