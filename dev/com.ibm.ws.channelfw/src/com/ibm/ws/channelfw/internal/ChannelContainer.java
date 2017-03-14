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
//@(#) 1.3 CF/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/impl/ChannelContainer.java, WAS.channelfw, CCX.CF 5/10/04 22:24:41 [5/11/05 12:15:22]

package com.ibm.ws.channelfw.internal;

import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.channelfw.internal.chains.Chain;
import com.ibm.wsspi.channelfw.Channel;

/**
 * This class is used by the channel framework to track the state of channels
 * in the runtime.
 */
public class ChannelContainer {

    // Instance variables
    /**
     * Channel in this container
     */
    private Channel channel = null;
    /**
     * state of this channel
     */
    private RuntimeState state = null;
    /**
     * map of chains this channel is in.
     */
    private Map<String, Chain> chainMap = null;
    /**
     * channel data of this child channel
     */
    private ChildChannelDataImpl channelData = null;

    /**
     * Constructor.
     * 
     * @param inputChannel
     *            represented by this container
     * @param inputData
     *            related to the channel
     */
    public ChannelContainer(Channel inputChannel, ChildChannelDataImpl inputData) {
        this.channel = inputChannel;
        this.channelData = inputData;
        this.state = RuntimeState.INITIALIZED;
        this.chainMap = new HashMap<String, Chain>();
    }

    /**
     * get the Channel instance this chain is associated with
     * 
     * @return Channel
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * return state of this Channel
     * 
     * @return RuntimeState
     */
    public RuntimeState getState() {
        return this.state;
    }

    /**
     * return Map of chains this channel is in
     * 
     * @return Map
     */
    public Map<String, Chain> getChainMap() {
        return this.chainMap;
    }

    /**
     * return this ChildChannelData
     * 
     * @return ChildChannelDataImpl
     */
    public ChildChannelDataImpl getChannelData() {
        return this.channelData;
    }

    /**
     * Update the chain map with a reference to the input chain.
     * 
     * @param chain
     */
    public void addChainReference(Chain chain) {
        this.chainMap.put(chain.getName(), chain);
    }

    /**
     * Remove the reference to the input chain in the chain map.
     * 
     * @param chainName
     */
    public void removeChainReference(String chainName) {
        this.chainMap.remove(chainName);
    }

    /**
     * Update the state of the channel.
     * 
     * @param inputState
     */
    public void setState(RuntimeState inputState) {
        this.state = inputState;
    }
}
