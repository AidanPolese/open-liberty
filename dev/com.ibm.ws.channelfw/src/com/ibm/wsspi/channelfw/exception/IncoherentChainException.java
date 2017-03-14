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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/exception/IncoherentChainException.java, WAS.channelfw, WASX.SERV1 5/10/04 22:21:15 [1/4/05 10:03:41]

package com.ibm.wsspi.channelfw.exception;

/**
 * Chain is incoherent...otherwise the interfaces do not mesh correctly.
 */
public class IncoherentChainException extends ChainException {

    /** Serialization ID string */
    private static final long serialVersionUID = 6901609329105430273L;

    /**
     * Constructor with an exception message string.
     * 
     * @param message
     */
    public IncoherentChainException(String message) {
        super(message);
    }

    /**
     * Constructor with no message or cause.
     */
    public IncoherentChainException() {
        super();
    }

    /**
     * Constructor with an exception message string and a cause.
     * 
     * @param message
     * @param cause
     */
    public IncoherentChainException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with no exception message but with a cause.
     * 
     * @param cause
     */
    public IncoherentChainException(Throwable cause) {
        super(cause);
    }

}
