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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/InvalidRuntimeStateException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:23 [1/4/05 10:03:44]

package com.ibm.wsspi.channelfw.exception;

/**
 * Exception indicating a particular channel framework API call was made during
 * in invalid runtime state for the framework.
 * 
 */
public class InvalidRuntimeStateException extends ChainException {

    /** Serialization ID string */
    private static final long serialVersionUID = 400959244233494560L;

    /**
     * Constructor.
     * 
     * @param message
     */
    public InvalidRuntimeStateException(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public InvalidRuntimeStateException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message
     * @param cause
     */
    public InvalidRuntimeStateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public InvalidRuntimeStateException(Throwable cause) {
        super(cause);
    }

}
