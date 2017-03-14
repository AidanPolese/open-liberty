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
//@(#) 1.3 CF/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/VirtualConnectionFactory.java, WAS.channelfw, CCX.CF 9/2/04 13:40:12 [5/11/05 12:46:43]

package com.ibm.wsspi.channelfw;

import com.ibm.websphere.channelfw.*;
import com.ibm.wsspi.channelfw.exception.ChainException;
import com.ibm.wsspi.channelfw.exception.ChannelException;

/**
 * The VirtualConnectionFactory is used to create virtual connections.
 * <p>
 * These factories should be obtained from the ChannelFramework.
 */
public interface VirtualConnectionFactory {
    /**
     * Create a VirtualConnection.
     * 
     * @return VirtualConnection
     * @throws ChannelException
     * @throws ChainException
     */
    VirtualConnection createConnection() throws ChannelException, ChainException;

    /**
     * Get the channel or chain name associated with this factory.
     * 
     * @return String
     */
    String getName();

    /**
     * Get the type (inbound || outbound) this factory is associated with.
     * 
     * @return FlowType
     */
    FlowType getType();

    /**
     * Assert that the virtual connection factory will no longer be used
     * and can be cleaned up.
     * 
     * @throws ChannelException
     * @throws ChainException
     */
    void destroy() throws ChannelException, ChainException;
}
