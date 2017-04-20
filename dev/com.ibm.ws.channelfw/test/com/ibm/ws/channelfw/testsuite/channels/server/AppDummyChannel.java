package com.ibm.ws.channelfw.testsuite.channels.server;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.ws.channelfw.testsuite.channels.protocol.ProtocolDummyContext;
import com.ibm.wsspi.channelfw.Channel;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.DiscriminationProcess;
import com.ibm.wsspi.channelfw.Discriminator;
import com.ibm.wsspi.channelfw.InboundChannel;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.exception.ChannelException;

/**
 * Inbound application level test channel.
 */
@SuppressWarnings("unused")
public class AppDummyChannel implements InboundChannel, Discriminator {
    private AppDummyFactory myFactory = null;
    private ChannelData chfwConfig = null;

    /**
     * Constructor.
     * 
     * @param config
     * @param factory
     */
    public AppDummyChannel(ChannelData config, AppDummyFactory factory) {
        this.myFactory = factory;
        update(config);
    }

    public Discriminator getDiscriminator() {
        return this;
    }

    public void destroy() throws ChannelException {
        this.myFactory.removeChannel(getName());
    }

    public ConnectionLink getConnectionLink(VirtualConnection vc) {
        AppDummyLink link = (AppDummyLink) vc.getStateMap().get("APPDUMMYLINK");
        if (null == link) {
            link = new AppDummyLink();
        }
        return link;
    }

    public Class<?> getDeviceInterface() {
        return ProtocolDummyContext.class;
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

    public void cleanUpState(VirtualConnection vc) {
        vc.getStateMap().remove("APPDUMMYLINK");
    }

    public int discriminate(VirtualConnection vc, Object discrimData) {
        return Discriminator.YES;
    }

    public Channel getChannel() {
        return this;
    }

    public Class<?> getDiscriminatoryDataType() {
        return ProtocolDummyContext.class;
    }

    public int getWeight() {
        return this.chfwConfig.getDiscriminatorWeight();
    }

    /**
     * Get the current config object for the channel.
     * 
     * @return ChannelData
     */
    public ChannelData getConfig() {
        return this.chfwConfig;
    }

    /*
     * @see com.ibm.wsspi.channelfw.base.InboundApplicationChannel#getName()
     */
    public String getName() {
        return this.chfwConfig.getName();
    }

    /*
     * @see com.ibm.wsspi.channelfw.InboundChannel#getDiscriminationProcess()
     */
    public DiscriminationProcess getDiscriminationProcess() {
        throw new IllegalStateException("Not implemented and should not be used");
    }

    /*
     * @see com.ibm.wsspi.channelfw.InboundChannel#setDiscriminationProcess(DiscriminationProcess)
     */
    @SuppressWarnings("unused")
    public void setDiscriminationProcess(DiscriminationProcess dp) {
        throw new IllegalStateException("Not implemented and should not be used");
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#getApplicationInterface()
     */
    public Class<?> getApplicationInterface() {
        throw new IllegalStateException("Not implemented and should not be used");
    }

    /*
     * @see com.ibm.wsspi.channelfw.InboundChannel#getDiscriminatoryType()
     */
    public Class<?> getDiscriminatoryType() {
        throw new IllegalStateException("Not implemented and should not be used");
    }

}
