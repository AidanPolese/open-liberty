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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/InvalidChannelNameException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:21 [1/4/05 10:03:43]

package com.ibm.wsspi.channelfw.exception;

/**
 * This exception is thrown if the channel name is equal to that of another
 * channels or is invlaid.
 */
public class InvalidChannelNameException extends ChannelException {

    /** Serialization ID string */
    private static final long serialVersionUID = 8153822118205179608L;

    /**
     * Constructor with an exception message.
     * 
     * @param message
     */
    public InvalidChannelNameException(String message) {
        super(message);
    }

    /**
     * Constructor with no message or cause.
     */
    public InvalidChannelNameException() {
        super();
    }

    /**
     * Constructor with an exception message and a cause.
     * 
     * @param message
     * @param cause
     */
    public InvalidChannelNameException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a cause but no message.
     * 
     * @param cause
     */
    public InvalidChannelNameException(Throwable cause) {
        super(cause);
    }
}
