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
//@(#) 1.6 SERV1/ws/code/channelfw/src/com/ibm/websphere/channel/framework/ChainData.java, WAS.channelfw, WASX.SERV1 8/24/04 15:36:24 [8/28/04 13:40:09]

package com.ibm.websphere.channelfw;

import java.io.Serializable;

/**
 * ChainData is a representation of the configuration information
 * about a specific Transport Chain. A Transport Chain can be viewed
 * as a protocol stack. A Transport Chain is composed of Transport
 * Channels and is used as a client or server transport.
 * <p>
 * This API can be used to get more information about a specific
 * Transport Channel from the runtime.
 * 
 * @ibm-api
 */
public interface ChainData extends Serializable {

    /**
     * Fetch the name of this chain as it was named in the configuration
     * or on creation.
     * 
     * @return String
     */
    String getName();

    /**
     * Get the type of chain (inbound or outbound).
     * 
     * @see com.ibm.websphere.channelfw.FlowType
     * 
     * @return FlowType
     */
    FlowType getType();

    /**
     * Get a list of the channel names in order from closest to connection
     * initiator to farthest.
     * <p>
     * On a client (outbound) transport, the connection initiator is normally a
     * higher level protocol (i.e. HTTP Channel).
     * <p>
     * On a server (inbound) transport, the connection initiator is often the
     * lowest level channel like the TCPChannel.
     * 
     * @return ChannelData[]
     */
    ChannelData[] getChannelList();

    /**
     * Check whether the configuration of this chain marked it as enabled
     * or not. If it is not enabled, then it should not be started.
     * 
     * @return boolean
     */
    boolean isEnabled();

    /**
     * Set the flag on whether this chain is enabled or not based on the
     * configuration.
     * 
     * @param flag
     */
    void setEnabled(boolean flag);
}
