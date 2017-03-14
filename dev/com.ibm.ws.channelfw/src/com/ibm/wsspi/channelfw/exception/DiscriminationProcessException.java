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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/DiscriminationProcessException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:11 [1/4/05 10:03:40]

package com.ibm.wsspi.channelfw.exception;

/**
 * Discriminators added to a running DiscriminationProcess or discrimination
 * had errors during execution.
 * 
 */
public class DiscriminationProcessException extends ChainException {

    /** Serialization ID string */
    private static final long serialVersionUID = -3060883482540305521L;

    /**
     * Constructor with an exception message.
     * 
     * @param message
     */
    public DiscriminationProcessException(String message) {
        super(message);
    }

    /**
     * Constructor with no message or cause.
     */
    public DiscriminationProcessException() {
        super();
    }

    /**
     * Constructor with an exception message and a cause.
     * 
     * @param message
     * @param cause
     */
    public DiscriminationProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with no message but with a cause.
     * 
     * @param cause
     */
    public DiscriminationProcessException(Throwable cause) {
        super(cause);
    }
}
