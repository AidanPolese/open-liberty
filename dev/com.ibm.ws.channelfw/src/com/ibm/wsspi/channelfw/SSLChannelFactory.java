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
//@(#) 1.1 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/SSLChannelFactory.java, WAS.channelfw, WASX.SERV1 5/26/04 14:22:10 [8/28/04 13:41:28]

package com.ibm.wsspi.channelfw;

/**
 * Any channel factory implementation that includes SSL must implement this
 * interface. Note that it is intentionally empty. No methods are necessary.
 * The logic of the channel framework does a Class.isAssignableFrom on each
 * channel
 * factory in a chain to determine if the chain has SSL enabled.
 */
public interface SSLChannelFactory extends ChannelFactory {
    // nothing additional
}
