/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jfap.inbound.channel;

import java.util.HashMap;
import java.util.Map;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.channelfw.ChannelFactoryData;
import com.ibm.websphere.channelfw.OutboundChannelDefinition;
import com.ibm.ws.sib.jfapchannel.server.impl.JFapChannelInbound;
import com.ibm.wsspi.channelfw.Channel;
import com.ibm.wsspi.channelfw.ChannelFactory;
import com.ibm.wsspi.channelfw.exception.ChannelException;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryException;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 *
 */
public class JFAPServerInboundChannelFactory implements ChannelFactory {
    /** Map of the existing channels for this factory. */
    private Map<String, Channel> existingChannels = null;
    /** Property map that may or may not exist for the factory. */
    private Map<Object, Object> commonProperties = null;

    private ChannelFactoryData _channelFactoryData;

    /**
     * Constructor.
     */
    public JFAPServerInboundChannelFactory() {
        this.existingChannels = new HashMap<String, Channel>();
    }

    public void destroy() {
    // nothing
    }

    public Class<?>[] getDeviceInterface() {
        return new Class<?>[] { TCPConnectionContext.class };
    }

    public void init(ChannelFactoryData data) throws ChannelFactoryException {
        // nothing
        _channelFactoryData = data;
    }

    /*
     * @see com.ibm.wsspi.channelfw.ChannelFactory#findOrCreateChannel(ChannelData)
     */
    public synchronized Channel findOrCreateChannel(ChannelData channelData) throws ChannelException {
        String channelName = channelData.getName();
        Channel ret = this.existingChannels.get(channelName);
        if (ret == null) {
            // Create the new channel with the input configuration
            ret = new JFapChannelInbound(_channelFactoryData, channelData);
            this.existingChannels.put(channelName, ret);
        }
        return ret;
    }

    /**
     * Remove a channel from the existing channels list.
     * 
     * @param channelName
     */
    public synchronized void removeChannel(String channelName) {
        this.existingChannels.remove(channelName);
    }

    /*
     * @see com.ibm.wsspi.channelfw.ChannelFactory#getProperties()
     */
    public Map<Object, Object> getProperties() {
        return this.commonProperties;
    }

    /*
     * @see com.ibm.wsspi.channelfw.ChannelFactory#updateProperties(java.util.Map)
     */
    public void updateProperties(Map<Object, Object> properties) {
        this.commonProperties = properties;
    }

    /*
     * @see com.ibm.wsspi.channelfw.ChannelFactory#getApplicationInterface()
     */
    public final Class<?> getApplicationInterface() {
        return JFAPInboundServiceContext.class;
    }

    /*
     * @see com.ibm.wsspi.channelfw.ChannelFactory#getOutboundChannelDefinition(java.util.Map)
     */
    @Override
    public OutboundChannelDefinition getOutboundChannelDefinition(Map<Object, Object> props) {
        return null;
    }

}
