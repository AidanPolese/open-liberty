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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/ChainGroupException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:20:58 [1/4/05 10:03:35]

package com.ibm.wsspi.channelfw.exception;

/**
 * Exception specifically with a chain group.
 * 
 */
public class ChainGroupException extends ChannelFrameworkException {

    /** Serialization ID string */
    private static final long serialVersionUID = -7819400101199566906L;

    /**
     * Constructor with an exception message.
     * 
     * @param message
     */
    public ChainGroupException(String message) {
        super(message);
    }

    /**
     * Constructor with no message or cause.
     */
    public ChainGroupException() {
        super();
    }

    /**
     * Constructor with an exception message and a cause.
     * 
     * @param message
     * @param cause
     */
    public ChainGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a cause but no message.
     * 
     * @param cause
     */
    public ChainGroupException(Throwable cause) {
        super(cause);
    }
}
