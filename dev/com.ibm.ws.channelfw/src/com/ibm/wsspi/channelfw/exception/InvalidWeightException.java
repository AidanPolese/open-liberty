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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/InvalidWeightException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:25 [1/4/05 10:03:45]

package com.ibm.wsspi.channelfw.exception;

/**
 * This exception is thrown if an invalid weight is given in a channel
 * configuration.
 */
public class InvalidWeightException extends ChannelException {

    /** Serialization ID string */
    private static final long serialVersionUID = -3045401376510042751L;

    /**
     * Constructor with an exception message.
     * 
     * @param message
     */
    public InvalidWeightException(String message) {
        super(message);
    }

    /**
     * Constructor with no message or cause.
     */
    public InvalidWeightException() {
        super();
    }

    /**
     * Contructor with an exception message and a cause.
     * 
     * @param message
     * @param cause
     */
    public InvalidWeightException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with no exception message but with a cause.
     * 
     * @param cause
     */
    public InvalidWeightException(Throwable cause) {
        super(cause);
    }

}
