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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/ChannelFactoryException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:05 [1/4/05 10:03:38]

package com.ibm.wsspi.channelfw.exception;

/**
 * This exception class will be the base for creating many exception classes
 * related to channel factories.
 * 
 */
public class ChannelFactoryException extends ChannelException {

    /** Serialization ID string */
    private static final long serialVersionUID = 2493186982674438118L;

    /**
     * Constructor.
     * 
     * @param message
     */
    public ChannelFactoryException(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public ChannelFactoryException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message
     * @param cause
     */
    public ChannelFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public ChannelFactoryException(Throwable cause) {
        super(cause);
    }
}
