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
//HISTORY
//~~~~~~~
// 
//Change ID    Date      Author    Abstract
//---------    --------  --------  ---------------------------------------
//305918       09/16/05  clanzen   Provide chain data to channel factories.
//======================================================================== */

package com.ibm.ws.channelfw.internal.chains;

import com.ibm.websphere.channelfw.ChainData;
import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.channelfw.internal.ChainDataImpl;
import com.ibm.ws.channelfw.internal.ChannelFrameworkConstants;
import com.ibm.ws.channelfw.internal.RuntimeState;
import com.ibm.wsspi.channelfw.Channel;

/**
 * Basic chain instance.
 */
public abstract class Chain {
    /** Trace service */
    private static final TraceComponent tc =
                    Tr.register(Chain.class,
                                ChannelFrameworkConstants.BASE_TRACE_NAME,
                                ChannelFrameworkConstants.BASE_BUNDLE);

    /**
     * List of actual channels in this Chain
     * package protected for InboundChainImpl and OutboundChainImpl
     */
    protected Channel[] channels = null;
    /**
     * data for this Chain
     * package protected for InboundChainImpl and OutboundChainImpl
     */
    protected ChainDataImpl chainData = null;
    /**
     * state of this chain
     * package protected for InboundChainImpl and OutboundChainImpl
     */
    protected RuntimeState state = RuntimeState.UNINITIALIZED;
    /**
     * Array of ChannelData
     * package protected for InboundChainImpl and OutboundChainImpl
     */
    protected ChannelData channelDataArray[] = null;
    /**
     * Task associated with stopping this chain after a timer pops. May be null if it doesn't exist.
     */
    private StopChainTask stopChainTask = null;

    /**
     * Constructor.
     * 
     * @param config
     */
    public Chain(ChainData config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "constructor");
        }
        this.chainData = (ChainDataImpl) config;
        this.state = RuntimeState.UNINITIALIZED;
        this.channelDataArray = new ChannelData[chainData.getChannelList().length];
        for (int i = 0; i < channelDataArray.length; i++) {
            this.channelDataArray[i] = chainData.getChannelList()[i];
            // Store a ref to the chain data that can eventually be used by channel factories in findOrCreateChannel.
            this.channelDataArray[i].getPropertyBag().put(ChannelFrameworkConstants.CHAIN_DATA_KEY, config);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "constructor");
        }
    }

    // *************************************
    // Accessor Methods
    // *************************************

    /**
     * Query the name of this chain.
     * 
     * @return String
     */
    public String getName() {
        return this.chainData.getName();
    }

    /**
     * Access the chain configuration object.
     * 
     * @return ChainData
     */
    public ChainData getChainData() {
        return this.chainData;
    }

    /**
     * Access the list of channels in this chain.
     * 
     * @return Channel[]
     */
    public Channel[] getChannels() {
        return this.channels;
    }

    /**
     * Access the configurations for the channels in this chain.
     * 
     * @return ChannelData[]
     */
    public ChannelData[] getChannelsData() {
        return this.channelDataArray;
    }

    /**
     * Query the current runtime state of this chain.
     * 
     * @return RuntimeState
     */
    public RuntimeState getState() {
        return this.state;
    }

    /**
     * Check whether this chain contains the input channel name.
     * 
     * @param channelName
     * @return boolean
     */
    public boolean containsChannel(String channelName) {
        boolean found = false;
        for (int i = 0; i < channels.length; i++) {
            if (channels[i].getName().equals(channelName)) {
                found = true;
                break;
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "containsChannel: " + channelName + "=" + found);
        }
        return found;
    }

    /**
     * Set the chain stop task to the input object.
     * 
     * @param task
     */
    public void setStopTask(StopChainTask task) {
        this.stopChainTask = task;
    }

    /**
     * Access the current chain stop task instance.
     * 
     * @return StopChainTask
     */
    public StopChainTask getStopTask() {
        return this.stopChainTask;
    }

    // *************************************
    // Life Cycle Methods
    // *************************************

    /**
     * Initialize this chain.
     */
    public void init() {
        this.state = RuntimeState.INITIALIZED;
        // Alert chainEventListeners
        this.chainData.chainInitialized();
    }

    /**
     * Start this chain.
     */
    public void start() {
        this.state = RuntimeState.STARTED;
        // Alert chainEventListeners
        this.chainData.chainStarted();
    }

    /**
     * Quiesce this chain.
     */
    public void quiesce() {
        this.state = RuntimeState.QUIESCED;
        // alert the listeners
        this.chainData.chainQuiesced();
    }

    /**
     * Stop this chain.
     */
    public void stop() {
        this.state = RuntimeState.INITIALIZED;
        // Alert chainEventListeners
        this.chainData.chainStopped();
    }

    /**
     * Destroy this chain.
     */
    public void destroy() {
        this.state = RuntimeState.UNINITIALIZED;
        // Alert chainEventListeners
        this.chainData.chainDestroyed();
    }

    /**
     * For testing purposes only.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("Chain: ");
        sb.append(this.chainData.getName());
        sb.append("\r\n\ttype = ").append(this.chainData.getType().getOrdinal());
        sb.append("\r\n\tstate: ").append(this.state.ordinal);
        return sb.toString();
    }

}
