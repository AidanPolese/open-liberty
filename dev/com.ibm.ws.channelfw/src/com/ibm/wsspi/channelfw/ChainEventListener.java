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
//@(#) 1.4 CF/ws/code/channelfw/src/com/ibm/wsspi/channel/framework/ChainEventListener.java, WAS.channelfw, CCX.CF 9/2/04 13:39:22 [5/11/05 12:46:19]

package com.ibm.wsspi.channelfw;

import com.ibm.websphere.channelfw.ChainData;

/**
 * A ChainEventListener is notified whenever lifecycle events such as stop and
 * start
 * happen on a chain. A class implementing this interface needs to be registered
 * into
 * the ChannelFramework in order for the events to be called. This may be
 * registered
 * all chains using the defined name below.
 * 
 */
public interface ChainEventListener {

    /** Identifier used to register a listener for all chains in the framework. */
    String ALL_CHAINS = "all_chains";

    /**
     * Event marking the chain initialization stage.
     * 
     * @param chainData
     */
    void chainInitialized(ChainData chainData);

    /**
     * Event marking the chain started stage.
     * 
     * @param chainData
     */
    void chainStarted(ChainData chainData);

    /**
     * Event marking the chain stopped stage.
     * 
     * @param chainData
     */
    void chainStopped(ChainData chainData);

    /**
     * Event marking the chain quiesced stage.
     * 
     * @param chainData
     */
    void chainQuiesced(ChainData chainData);

    /**
     * Event marking the chain destroyed stage.
     * 
     * @param chainData
     */
    void chainDestroyed(ChainData chainData);

    /**
     * Event marking the chain configuration updated stage.
     * 
     * @param chainData
     */
    void chainUpdated(ChainData chainData);

}
