package com.ibm.ws.channelfw.testsuite.channels.outbound;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.OutboundChannel;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.exception.ChannelException;
import com.ibm.wsspi.tcpchannel.TCPConnectRequestContext;

/**
 * Outbound application channel test code.
 */
@SuppressWarnings("unused")
public class GetterChannel implements OutboundChannel {
    private GetterFactory myFactory;
    /** Channel configuration object */
    private ChannelData chfwConfig = null;

    /**
     * Constructor.
     * 
     * @param config
     * @param factory
     */
    public GetterChannel(ChannelData config, GetterFactory factory) {
        myFactory = factory;
        update(config);
    }

    public Class<?>[] getApplicationAddress() {
        return new Class<?>[] { TCPConnectRequestContext.class };
    }

    public Class<?> getDeviceAddress() {
        return TCPConnectRequestContext.class;
    }

    public void destroy() throws ChannelException {
        myFactory.removeChannel(getName());
    }

    public Class<?> getApplicationInterface() {
        return null;
    }

    public ConnectionLink getConnectionLink(VirtualConnection vc) {
        return new GetterLink();
    }

    public Class<?> getDeviceInterface() {
        return OutboundDummyContext.class;
    }

    public void init() throws ChannelException {
        // nothing
    }

    public void start() throws ChannelException {
        // nothing
    }

    public void stop(long millisec) throws ChannelException {
        // nothing
    }

    public void update(ChannelData cc) {
        this.chfwConfig = cc;
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#getName()
     */
    public String getName() {
        return this.chfwConfig.getName();
    }
}
