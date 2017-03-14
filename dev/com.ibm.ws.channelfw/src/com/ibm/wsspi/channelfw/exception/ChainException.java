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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/ChainException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:19:45 [1/4/05 10:03:34]

package com.ibm.wsspi.channelfw.exception;

/**
 * This exception class will be the base for creating many exception classes
 * related to chains.
 */
public class ChainException extends ChannelFrameworkException {

    /** Serialization ID string */
    private static final long serialVersionUID = -3097683412942829927L;

    /**
     * Constructor.
     * 
     * @param message
     */
    public ChainException(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public ChainException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message
     * @param cause
     */
    public ChainException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public ChainException(Throwable cause) {
        super(cause);
    }

}
