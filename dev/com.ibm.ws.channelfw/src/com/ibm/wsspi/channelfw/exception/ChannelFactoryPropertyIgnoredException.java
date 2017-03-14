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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/ChannelFactoryPropertyIgnoredException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:07 [1/4/05 10:03:38]

package com.ibm.wsspi.channelfw.exception;

/**
 * This exception is thrown by channel factory implementations when a
 * property is being set that the channel factory cannot handle.
 */
public class ChannelFactoryPropertyIgnoredException extends ChannelFactoryException {

    /** Serialization ID string */
    private static final long serialVersionUID = -6471004761179894209L;

    /**
     * Constructor.
     * 
     * @param message
     */
    public ChannelFactoryPropertyIgnoredException(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public ChannelFactoryPropertyIgnoredException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message
     * @param cause
     */
    public ChannelFactoryPropertyIgnoredException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public ChannelFactoryPropertyIgnoredException(Throwable cause) {
        super(cause);
    }
}