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
//@(#) 1.3 CF/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/internals/InboundVirtualConnection.java, WAS.channelfw, CCX.CF 5/10/04 22:24:37 [5/11/05 12:15:38]

package com.ibm.ws.channelfw.internal;

import com.ibm.ws.channelfw.internal.discrim.DiscriminationGroup;
import com.ibm.wsspi.channelfw.VirtualConnection;

/**
 * This represents an inbound or outbound connection using the channel
 * framework. It basically is used to track the state for individual channels
 * in the chain associated with this connection. Each channel in the chain
 * keeps a ConnectionLink object in this connection.
 */
public interface InboundVirtualConnection extends VirtualConnection {

    /**
     * Returns Discrimination Status. Gets the integers associated with the saved
     * status from the last
     * discrimination. This is used only used internally by the channel framework.
     * 
     * @return integer array that represents status
     */
    int[] getDiscriminatorStatus();

    /**
     * Set Discrimination Status. This sets the status in the discrimination. This
     * is
     * used when the discrimination has been done but is returning a
     * "needs more data". This
     * can be used to save the state information to free ourselves from yet
     * another connection
     * specific state object.
     * 
     * @param status
     *            to keep for the next time we are called.
     */
    void setDiscriminatorStatus(int[] status);

    /**
     * Set the discrimination process used for this discriminator state. This
     * validates the
     * discriminators haven't changes since last status was obtained.
     * 
     * @param dp
     */
    void setDiscriminationGroup(DiscriminationGroup dp);

    /**
     * gets the discrimination process for comparisions to validate the
     * discriminator state.
     * 
     * @return discrimination group
     */
    DiscriminationGroup getDiscriminationGroup();

}
