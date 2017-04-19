/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.channelfw.testsuite.channels.protocol;

import java.util.HashMap;
import java.util.Map;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.channelfw.ChannelFactoryData;
import com.ibm.websphere.channelfw.OutboundChannelDefinition;
import com.ibm.wsspi.channelfw.Channel;
import com.ibm.wsspi.channelfw.ChannelFactory;
import com.ibm.wsspi.channelfw.SSLChannelFactory;
import com.ibm.wsspi.channelfw.exception.ChannelException;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryException;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryPropertyIgnoredException;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 * Secure protocol (mid-chain) factory.
 */
@SuppressWarnings("unused")
public class ProtocolSecureFactory implements SSLChannelFactory {
    /** Map of the existing channels for this factory. */
    private Map<String, Channel> existingChannels = null;
    /** Property map that may or may not exist for the factory. */
    private Map<Object, Object> commonProperties = null;

    /**
     * Constructor.
     */
    public ProtocolSecureFactory() {
        this.existingChannels = new HashMap<String, Channel>();
    }

    @Override
    public void destroy() {
        // nothing
    }

    @Override
    public Channel findOrCreateChannel(ChannelData config) throws ChannelException {
        String channelName = config.getName();
        Channel rc = this.existingChannels.get(channelName);
        if (null == rc) {
            rc = new ProtocolSecureChannel(config);
            this.existingChannels.put(channelName, rc);
        }
        return rc;
    }

    @Override
    public Class<?> getApplicationInterface() {
        return TCPConnectionContext.class;
    }

    @Override
    public Class<?>[] getDeviceInterface() {
        return new Class<?>[] { TCPConnectionContext.class };
    }

    @Override
    public OutboundChannelDefinition getOutboundChannelDefinition(Map<Object, Object> props) {
        return new OutDef(props);
    }

    @Override
    public Map<Object, Object> getProperties() {
        return this.commonProperties;
    }

    @Override
    public void init(ChannelFactoryData data) throws ChannelFactoryException {
        // nothing
    }

    @Override
    public void updateProperties(Map<Object, Object> properties)
                    throws ChannelFactoryPropertyIgnoredException {
        this.commonProperties = properties;
    }

    private class OutDef implements OutboundChannelDefinition {
        private static final long serialVersionUID = -7625993397141832104L;

        public OutDef(Map<Object, Object> props) {
            // nothing
        }

        @Override
        public Map<Object, Object> getOutboundChannelProperties() {
            return null;
        }

        @Override
        public Class<?> getOutboundFactory() {
            return ProtocolSecureFactory.class;
        }

        @Override
        public Map<Object, Object> getOutboundFactoryProperties() {
            return null;
        }
    }
}
