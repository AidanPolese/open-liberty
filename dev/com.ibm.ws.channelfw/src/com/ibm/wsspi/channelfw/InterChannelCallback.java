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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/InterChannelCallback.java, WAS.channelfw, WASX.SERV1 5/10/04 22:20:25 [8/28/04 13:41:24]

package com.ibm.wsspi.channelfw;

/**
 * A generic callback mechanism used for asynchronous operations.
 * <p>
 * This interface is not used specifically within the framework, but is a model
 * for consistency that can be used by any channel.
 */
public interface InterChannelCallback {
    /**
     * Called when the request has completeted successfully.
     * 
     * @param vc
     */
    void complete(VirtualConnection vc);

    /**
     * Called back if an exception occurres while processing the request.
     * The implementer of this interface can then decide how to handle this
     * exception.
     * 
     * @param vc
     * @param t
     *            The Throwable that caused the error.
     */
    void error(VirtualConnection vc, Throwable t);
}
