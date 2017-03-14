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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/ChannelFrameworkException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:09 [1/4/05 10:03:39]

package com.ibm.wsspi.channelfw.exception;

/**
 * This is a base exception class which will be extended by all
 * the other channel framework exceptions. The purpose of it is
 * to consolidate all the different types of the exceptions into
 * a single type for use in method signatures.
 */
public class ChannelFrameworkException extends Exception {

    /** Serialization ID string */
    private static final long serialVersionUID = 7351509803790105244L;

    /**
     * Constructor.
     * 
     * @param message
     */
    public ChannelFrameworkException(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public ChannelFrameworkException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message
     * @param cause
     */
    public ChannelFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public ChannelFrameworkException(Throwable cause) {
        super(cause);
    }
}
