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
//@(#) 1.5 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/OutboundVirtualConnection.java, WAS.channelfw, WASX.SERV1 9/2/04 13:39:53 [2/28/05 08:13:09]

package com.ibm.wsspi.channelfw;

/**
 * This represents an outbound connection using the channel
 * framework.
 * <p>
 * The methods within this particular interface (not the VirtualConnection which
 * is extended) are only for use by non-channel users of the channel framework.
 * <p>
 * Outbound connections may be utilized wihtout implementing a full channel. In
 * fact, no channel framework interface ever needs implemented by a client for
 * pure synchronous support.
 * <p>
 * These interfaces are essentially pass throughs to the top channel on the
 * chain. For example, in a chain like Http->TCP...a connection may call connect
 * on the interface which will subsequently call connect on the Http channel.
 * This will, after some possible translation, call connect on the TCP Channel.
 * <p>
 * The point is that a channel such as the Http or TCP channel never needs to
 * know anything about VirtualOutboundConnections. They will not be using any
 * part of this interface.
 */
public interface OutboundVirtualConnection extends VirtualConnection {
    /**
     * This returns the object that should be cast to the interface of the
     * initial channel in the chain. The applicaiton works with the connection
     * by invoking methods on this interface.
     * 
     * This should not be used by channels themselves. Instead, the channels
     * themselves
     * should call their ConnectionLink on the device side's getChannelAccessor
     * method. This
     * method merely returns the interface of the most application side channel in
     * the
     * channel chain.
     * 
     * @return Object
     */
    Object getChannelAccessor();

    /**
     * Complete the virtual connection by connecting to the provided
     * address. The conn channel links in the virtual connection will receive
     * ready
     * or destroy indications as the connection as completed via the callback.
     * When destroy is called due to a connect failure, the virtual connection
     * will
     * not be reusable for a new connect.
     * 
     * @param address
     * @param appCallback
     *            called when the connect has completed, or error occurred.
     * @exception IllegalStateException
     *                may be thrown if there are failures doing the connect
     */
    void connectAsynch(Object address, ConnectionReadyCallback appCallback) throws IllegalStateException;

    /**
     * Complete the virtual connection by connecting to the provided
     * address. This is a synchronous connect. If an error occurs, an exception
     * will be thrown. In this case, the virtual connection will not be reusable
     * for a new connect.
     * 
     * @param address
     * @exception Exception
     */
    void connect(Object address) throws Exception;

    /**
     * Indicate that this virtual connection is no longer in use. This clients use
     * of the connection is complete. If this is closed with an exception, the
     * framework will log that exception.
     * 
     * @param e
     */
    void close(Exception e);

}
