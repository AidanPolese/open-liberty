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
//@(#) 1.3 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/OutboundConnectionLink.java, WAS.channelfw, WASX.SERV1 5/10/04 22:20:29 [8/28/04 13:41:27]

package com.ibm.wsspi.channelfw;

/**
 * This is the ConnectionLink specifically for Outbound (client side)
 * Channels. Since the outbound connections are initiated by the application
 * channel, this interface adds connect methods (one asynchronous and one not)
 * to make new connections.
 */
public interface OutboundConnectionLink extends ConnectionLink {

    /**
     * Connect to the provided address asynchronously. The ready
     * methods will be called when the connection is established or fails.
     * <p>
     * Failures will come via a destroy call on the ConnectionReadyCallback. In
     * this failure scenario, the virtual connection will not be reusable for a
     * new connect.
     * 
     * @param address
     *            The address to connect to.
     */
    void connectAsynch(Object address);

    /**
     * Connect to the provided address synchronously. If a failure occurs,
     * an exception will be thrown. In this failure scenario, the virtual
     * connection will not be reusable for a new connect.
     * 
     * @param address
     *            The address to connect to.
     * @exception Exception
     *                This exception thrown if connect fails. Often on network
     *                channel implementations this will be an IOException.
     */
    void connect(Object address) throws Exception;
}
