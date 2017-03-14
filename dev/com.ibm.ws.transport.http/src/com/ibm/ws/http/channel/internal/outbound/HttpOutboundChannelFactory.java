// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.channel.internal.outbound;

import java.util.Map;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.channelfw.OutboundChannelDefinition;
import com.ibm.ws.http.channel.internal.HttpChannelFactory;
import com.ibm.wsspi.channelfw.Channel;
import com.ibm.wsspi.http.channel.outbound.HttpOutboundServiceContext;

/**
 * Factory to create new HTTP outbound channels.
 */
public class HttpOutboundChannelFactory extends HttpChannelFactory {

    /**
     * Constructor for an HTTP outbound channel factory.
     */
    public HttpOutboundChannelFactory() {
        super(HttpOutboundServiceContext.class);
    }

    /**
     * Create a new HTTP outbound channel instance.
     * 
     * @param channelData
     * @return Channel (HttpOutboundChannel)
     */
    @Override
    public Channel createChannel(ChannelData channelData) {
        return new HttpOutboundChannel(channelData, this, getObjectFactory());
    }

    /*
     * @see
     * com.ibm.wsspi.channelfw.ChannelFactory#getOutboundChannelDefinition(java
     * .util.Map)
     */
    @Override
    @SuppressWarnings("unused")
    public OutboundChannelDefinition getOutboundChannelDefinition(Map<Object, Object> props) {
        return null;
    }

}
