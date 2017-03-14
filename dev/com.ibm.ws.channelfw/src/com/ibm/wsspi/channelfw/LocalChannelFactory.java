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
//@(#) 1.1 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/LocalChannelFactory.java, WAS.channelfw, WASX.SERV1 5/26/04 14:22:06 [8/28/04 13:41:25]

package com.ibm.wsspi.channelfw;

/**
 * Any channel factory implementation that limits its chain to only be used
 * by client requests from the local machine must implement this
 * interface. Note that it is intentionally empty. No methods are necessary.
 * The logic of the channel framework does a Class.isAssignableFrom on each
 * channel
 * factory in a chain to determine if the chain is local only. For example,
 * the in process channel factory must implement this interface.
 */
public interface LocalChannelFactory extends ChannelFactory {
    // nothing additional
}
