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
//@(#) 1.1 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/RetryableChannelException.java, WAS.channelfw, WASX.SERV1 7/19/04 07:47:06 [1/4/05 10:03:58]

package com.ibm.wsspi.channelfw.exception;

/**
 * This is an exception that can be thrown by a channel when it is unable to
 * start,
 * but thinks that waiting for some period of time and retrying may result in
 * success.
 * A good example of this is when device side channels are started soon after
 * they
 * are stopped. If a bind error takes place because the socket is still tied up
 * in
 * its close down processing, then an exception like this can be thrown to flag
 * the
 * caller that the socket may be freeing up shortly.
 */
public class RetryableChannelException extends ChannelException {

    /** Serialization ID string */
    private static final long serialVersionUID = 4611158931491249843L;

    /**
     * Constructor with an exception message.
     * 
     * @param message
     */
    public RetryableChannelException(String message) {
        super(message);
    }

    /**
     * Constructor with no message or cause.
     */
    public RetryableChannelException() {
        super();
    }

    /**
     * Constructor with an exception message and a cause.
     * 
     * @param message
     * @param cause
     */
    public RetryableChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with no message but with a cause.
     * 
     * @param cause
     */
    public RetryableChannelException(Throwable cause) {
        super(cause);
    }

}