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
//@(#) 1.6 SERV1/ws/code/channelfw/src/com/ibm/websphere/channel/framework/ChannelData.java, WAS.channelfw, WASX.SERV1 5/26/04 08:38:11 [8/28/04 13:40:12]

package com.ibm.websphere.channelfw;

import java.io.Serializable;
import java.util.Map;

/**
 * The ChannelData is a representation of the runtime configuration of a specific Transport Channel.
 * The Transport Channel is the equivalent to a layer in a protocol stack. This Transport Channel
 * has a name, a set of properties, and various other information.
 * 
 * @ibm-api
 */
public interface ChannelData extends Serializable {

    /**
     * Get a name of this channel configuration. This may contain
     * runtime information, whereas getExternalName() never does, ie.
     * this may give MyChannel_CFINTERNAL_CHILD_0.
     * 
     * @return String
     */
    String getName();

    /**
     * Return the name for this channel that can be published externally.
     * This may or may not be the same value as getName().
     * 
     * @return external name for this channel data
     */
    String getExternalName();

    /**
     * Get the Class of the factory for this channel.
     * 
     * @return Class
     */
    Class<?> getFactoryType();

    /**
     * Get the property set for the name/value configuration pairs.
     * 
     * @return Map
     */
    Map<Object, Object> getPropertyBag();

    /**
     * Get the weight of this disciminator in this channel's config.
     * <p>
     * This is only valid on Inbound channels.
     * 
     * @return int
     */
    int getDiscriminatorWeight();

    /**
     * Determines if this represents an inbound channel.
     * 
     * @return boolean Returns true if this is inbound and false otherwise.
     */
    boolean isInbound();

    /**
     * Determines if this represents an outbound channel.
     * 
     * @return boolean Returns true if this is outbound and false otherwise.
     */
    boolean isOutbound();

}
