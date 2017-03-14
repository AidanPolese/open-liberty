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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/ChainTimerException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:20:59 [1/4/05 10:03:36]

package com.ibm.wsspi.channelfw.exception;

/**
 * This exception is thrown if an invalid timer is given during a chain stop.
 * 
 */
public class ChainTimerException extends ChannelException {

    /** Serialization ID string */
    private static final long serialVersionUID = -6157035791556297636L;

    /**
     * Constructor with an exception message.
     * 
     * @param message
     */
    public ChainTimerException(String message) {
        super(message);
    }

    /**
     * Constructor with no message or cause.
     */
    public ChainTimerException() {
        super();
    }

    /**
     * Constructor with an exception message and a cause.
     * 
     * @param message
     * @param cause
     */
    public ChainTimerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with no message but with a cause.
     * 
     * @param cause
     */
    public ChainTimerException(Throwable cause) {
        super(cause);
    }
}