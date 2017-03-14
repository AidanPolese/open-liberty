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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/OutboundChannel.java, WAS.channelfw, WASX.SERV1 5/10/04 22:20:27 [8/28/04 13:41:26]

package com.ibm.wsspi.channelfw;

/**
 * Outbound (client side) specific interface for Channels.
 * <p>
 * This extension of Channel adds information about the Object types accepted to
 * connect and connectAsynch calls.
 */
public interface OutboundChannel extends Channel {

    /**
     * The framework uses this method for coherency checking of address types for
     * connect and connectAsynch.
     * This method will return the type of address object this channel plans to
     * pass down towards
     * the device side.
     * 
     * @return Class<?>
     */
    Class<?> getDeviceAddress();

    /**
     * The framework uses this method for coherency checking of address types for
     * connect and connectAsynch.
     * This method will return the type of address objects this channel plans have
     * passed to it
     * from the application side. A channel may accept more than one address
     * object type but
     * passes only one down to the channels below.
     * 
     * @return Class<?>[]
     */
    Class<?>[] getApplicationAddress();

}
