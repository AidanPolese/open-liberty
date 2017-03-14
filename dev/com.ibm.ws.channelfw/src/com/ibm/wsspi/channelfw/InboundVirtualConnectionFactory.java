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
//@(#) 1.3 CF/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/InboundVirtualConnectionFactory.java, WAS.channelfw, CCX.CF 9/2/04 13:39:47 [5/11/05 12:46:41]

package com.ibm.wsspi.channelfw;

/**
 * This interface is used to create inbound virtual connections.
 * <p>
 * These factories should be obtained from the ChannelFramework.
 */
public interface InboundVirtualConnectionFactory extends VirtualConnectionFactory {
    /**
     * Create a VirtualConnection.
     * 
     * @return VirtualConnection
     */
    VirtualConnection createConnection();
}
