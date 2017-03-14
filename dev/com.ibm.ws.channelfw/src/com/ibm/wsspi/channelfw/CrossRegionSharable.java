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
//@(#) 1.1 CF/ws/code/channelfw/src/com/ibm/wsspi/channel/ws390/CrossRegionSharable.java, WAS.channelfw, CCX.CF 8/20/04 10:39:42 [5/11/05 12:46:58]
package com.ibm.wsspi.channelfw;

import java.util.Map;

import com.ibm.websphere.channelfw.ChannelData;

/**
 * Implement this if your channel may not be sharable in
 * all regions on a z/OS server. This will allow you to
 * tell the ChannelFramework on a per channel instance
 * basis whether or not the channel is sharable.
 * 
 * The two regions in discussion are the CR and CRA.
 * 
 * By default, all channels are sharable.
 * 
 * To use this interface simply implement this interface with your
 * WSChannelFactory.
 */
public interface CrossRegionSharable {
    /**
     * This will indicate whether or not the specific channel data
     * is sharable across regions. Most users who need to
     * implement this will have some resource that they cannot share
     * outside their JVM.
     * 
     * This returns true if the channel is sharable, false otherwise.
     * 
     * Example: A Connector Channel that binds to a specific host
     * and port would be non-sharable. However, one that bound to
     * any available ephemeral port could be sharable. A channel which
     * just took care of protocol parsing/marshalling may have no
     * specific resource that should not be shared, so they need not
     * implement this.
     * 
     * @param channelConfiguration
     * @return true if the channel is sharable, false otherwise.
     */
    boolean isSharable(Map<String, ChannelData> channelConfiguration);

}
