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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/InvalidChannelFactoryException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:19 [1/4/05 10:03:43]

package com.ibm.wsspi.channelfw.exception;

/**
 * Channel Factory is invalid or otherwise not found.
 */
public class InvalidChannelFactoryException extends ChannelFactoryException {

    /** Serialization ID string */
    private static final long serialVersionUID = 1592448367216093522L;

    /**
     * Constructor for InvalidChannelFactoryException.
     * 
     * @param message
     */
    public InvalidChannelFactoryException(String message) {
        super(message);
    }

    /**
     * Constructor with no message or cause.
     */
    public InvalidChannelFactoryException() {
        super();
    }

    /**
     * Constructor with an exception message and a cause.
     * 
     * @param message
     * @param cause
     */
    public InvalidChannelFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with no message but with a cause.
     * 
     * @param cause
     */
    public InvalidChannelFactoryException(Throwable cause) {
        super(cause);
    }

}
