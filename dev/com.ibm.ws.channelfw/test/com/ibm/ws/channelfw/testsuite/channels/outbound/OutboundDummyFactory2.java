package com.ibm.ws.channelfw.testsuite.channels.outbound;

import java.util.HashMap;
import java.util.Map;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.channelfw.ChannelFactoryData;
import com.ibm.websphere.channelfw.OutboundChannelDefinition;
import com.ibm.wsspi.channelfw.Channel;
import com.ibm.wsspi.channelfw.ChannelFactory;
import com.ibm.wsspi.channelfw.exception.ChannelException;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryException;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryPropertyIgnoredException;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 * Outbound channel factory for test usage.
 */
@SuppressWarnings("unused")
public class OutboundDummyFactory2 implements ChannelFactory {
    /** Map of the existing channels for this factory. */
    private Map<String, Channel> existingChannels = null;
    /** Property map that may or may not exist for the factory. */
    private Map<Object, Object> commonProperties = null;

    /**
     * Constructor.
     */
    public OutboundDummyFactory2() {
        this.existingChannels = new HashMap<String, Channel>();
    }

    public void destroy() {
        // nothing
    }

    public Class<?> getApplicationInterface() {
        return OutboundDummyContext.class;
    }

    public Class<?>[] getDeviceInterface() {
        return new Class<?>[] { TCPConnectionContext.class };
    }

    public void init(ChannelFactoryData data) throws ChannelFactoryException {
        // nothing
    }

    /*
     * @see com.ibm.wsspi.channelfw.ChannelFactory#findOrCreateChannel(ChannelData)
     */
    public synchronized Channel findOrCreateChannel(ChannelData channelData) throws ChannelException {
        String channelName = channelData.getName();
        Channel rc = this.existingChannels.get(channelName);
        if (null == rc) {
            rc = new OutboundDummyChannel2(channelData, this);
            this.existingChannels.put(channelName, rc);
        }
        return rc;
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
    public void updateProperties(Map<Object, Object> properties) throws ChannelFactoryPropertyIgnoredException {
        this.commonProperties = properties;
    }

    /*
     * @see com.ibm.wsspi.channelfw.ChannelFactory#getOutboundChannelDefinition(java.util.Map)
     */
    @Override
    public OutboundChannelDefinition getOutboundChannelDefinition(Map<Object, Object> props) {
        return null;
    }

}
