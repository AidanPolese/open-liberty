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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/ChannelFactoryAlreadyInitializedException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:03 [1/4/05 10:03:37]

package com.ibm.wsspi.channelfw.exception;

/**
 * {@link com.ibm.wsspi.channelfw.ChannelFactory} implementations
 * should throw this if they get initialized more than once.
 * 
 */
public class ChannelFactoryAlreadyInitializedException extends ChannelFactoryException {

    /** Serialization ID string */
    private static final long serialVersionUID = 6325483035210549501L;

    /**
     * Constructor with an exception message.
     * 
     * @param message
     */
    public ChannelFactoryAlreadyInitializedException(String message) {
        super(message);
    }

    /**
     * Constructor with no message or cause.
     */
    public ChannelFactoryAlreadyInitializedException() {
        super();
    }

    /**
     * Constructor with an exception message and a cause.
     * 
     * @param message
     * @param cause
     */
    public ChannelFactoryAlreadyInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Contructor with a cause but no exception message.
     * 
     * @param cause
     */
    public ChannelFactoryAlreadyInitializedException(Throwable cause) {
        super(cause);
    }

}
