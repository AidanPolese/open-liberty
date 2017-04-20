package com.ibm.ws.channelfw.testsuite.channels.broken;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.ws.channelfw.testsuite.channels.protocol.ProtocolDummyFactory;
import com.ibm.wsspi.channelfw.Channel;
import com.ibm.wsspi.channelfw.exception.InvalidChannelFactoryException;

/**
 * The following channel and channel factory will be used to verify that when a channel of
 * this type is included in a chain in a group, the group can still be
 * inited/started/stopped/destroyed. Specifically, all other chains will be handled
 * and the lifecycle methods will not stop / fail fast.
 */
public class BrokenChannelFactory extends ProtocolDummyFactory {
    /**
     * Constructor.
     * 
     * @throws InvalidChannelFactoryException
     */
    public BrokenChannelFactory() throws InvalidChannelFactoryException {
        super();
    }

    protected Channel createChannel(ChannelData config) {
        return new BrokenChannel(config, this);
    }
}