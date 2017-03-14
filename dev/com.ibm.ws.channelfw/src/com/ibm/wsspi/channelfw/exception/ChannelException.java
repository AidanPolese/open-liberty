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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/ChannelException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:01 [1/4/05 10:03:36]

package com.ibm.wsspi.channelfw.exception;

/**
 * This exception class will be the base for creating many exception classes
 * related to channels.
 */
public class ChannelException extends ChannelFrameworkException {

    /** Serialization ID string */
    private static final long serialVersionUID = 4309702246400782423L;

    private boolean suppressFFDC = false;

    /**
     * Constructor.
     * 
     * @param message
     */
    public ChannelException(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public ChannelException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message
     * @param cause
     */
    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public ChannelException(Throwable cause) {
        super(cause);
    }

    public void suppressFFDC(boolean suppress) {
        suppressFFDC = suppress;
    }

    public boolean suppressFFDC() {
        return suppressFFDC;
    }

}
