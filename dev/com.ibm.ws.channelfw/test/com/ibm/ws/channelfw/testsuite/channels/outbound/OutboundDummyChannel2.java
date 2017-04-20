package com.ibm.ws.channelfw.testsuite.channels.outbound;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.OutboundChannel;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.exception.ChannelException;
import com.ibm.wsspi.tcpchannel.TCPConnectRequestContext;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 * Outbound channel for test usage.
 */
@SuppressWarnings("unused")
public class OutboundDummyChannel2 implements OutboundChannel {
    private OutboundDummyFactory2 myFactory;
    private ChannelData chfwConfig = null;

    /**
     * Constructor.
     * 
     * @param config
     * @param factory
     */
    public OutboundDummyChannel2(ChannelData config, OutboundDummyFactory2 factory) {
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
        return OutboundDummyContext.class;
    }

    public ConnectionLink getConnectionLink(VirtualConnection vc) {
        return new OutboundDummyLink2();
    }

    public Class<?> getDeviceInterface() {
        return TCPConnectionContext.class;
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
        this.chfwConfig = null;
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#getName()
     */
    public String getName() {
        return this.chfwConfig.getName();
    }
}
