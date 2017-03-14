//-------------------------------------------------------------------------------
//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2004, 2005, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//
//Change ID     Author    Abstract
//---------     --------  -------------------------------------------------------
//d305240		mjohn256  Need to create the AccessLists and verify the packet sender.
//d306341		mjohn256  Add RAS logging support to UDP Channel.
// 363560       wigger    09/26/07 put correct info in message for hostname "*"
//-------------------------------------------------------------------------------

package com.ibm.ws.udpchannel.internal;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.LinkedList;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.DiscriminationProcess;
import com.ibm.wsspi.channelfw.Discriminator;
import com.ibm.wsspi.channelfw.InboundChannel;
import com.ibm.wsspi.channelfw.OutboundChannel;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.exception.ChannelException;
import com.ibm.wsspi.channelfw.exception.RetryableChannelException;
import com.ibm.wsspi.udpchannel.UDPContext;

/**
 * @author mjohnson
 */
public class UDPChannel implements OutboundChannel, InboundChannel {
    static final TraceComponent tc = Tr.register(UDPChannel.class, UDPMessages.TR_GROUP, UDPMessages.TR_MSGS);

    private static final Class<?> appSideClass = UDPContext.class;
    private static final Class<?>[] appSideList = new Class<?>[] { appSideClass };

    private WorkQueueManager workQueueManager = null;
    private UDPChannelConfiguration udpChannelConfig = null;
    private DiscriminationProcess discriminationProcess = null;
    private boolean stoppedFlag = true;
    private String displayableHostName = null;
    private LinkedList<UDPConnLink> inUse = new LinkedList<UDPConnLink>();
    private UDPNetworkLayer inboundNetworkLayer = null;
    private AccessLists alists;

    /**
     * Constructor.
     * 
     * @param config
     * @param workQueueManager
     * @throws ChannelException
     */
    public UDPChannel(UDPChannelConfiguration config, WorkQueueManager workQueueManager) throws ChannelException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(this, tc, "UDPChannel");
        }
        this.udpChannelConfig = config;
        this.workQueueManager = workQueueManager;

        workQueueManager.addRef();

        this.alists = AccessLists.getInstance(config);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(this, tc, "UDPChannel");
        }
    }

    /*
     * @see com.ibm.wsspi.channelfw.OutboundChannel#getDeviceAddress()
     */
    public Class<?> getDeviceAddress() {
        throw new IllegalStateException("Not implemented and never will be");
    }

    /*
     * @see com.ibm.wsspi.channelfw.OutboundChannel#getApplicationAddress()
     */
    public Class<?>[] getApplicationAddress() {
        return appSideList;
    }

    /*
     * 
     * see com.ibm.wsspi.channelfw.InboundChannel#getDiscriminator()
     */
    public Discriminator getDiscriminator() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "getDiscriminator should not be called in UDPChannel");
        }
        return null;
    }

    /*
     * @see com.ibm.wsspi.channelfw.InboundChannel#getDiscriminationProcess()
     */
    public DiscriminationProcess getDiscriminationProcess() {
        return this.discriminationProcess;
    }

    /*
     * @see
     * com.ibm.wsspi.channelfw.InboundChannel#setDiscriminationProcess(com.ibm
     * .wsspi.channelfw.DiscriminationProcess)
     */
    public void setDiscriminationProcess(DiscriminationProcess process) {
        this.discriminationProcess = process;
    }

    /*
     * @see com.ibm.wsspi.channelfw.InboundChannel#getDiscriminatoryType()
     */
    public Class<?> getDiscriminatoryType() {
        return WsByteBuffer.class;
    }

    /*
     * @see
     * com.ibm.wsspi.channelfw.Channel#getConnectionLink(com.ibm.wsspi.channelfw
     * .VirtualConnection)
     */
    public ConnectionLink getConnectionLink(VirtualConnection vc) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(this, tc, "getConnectionLink");
        }

        UDPConnLink connLink = new UDPConnLink(workQueueManager, vc, this, getConfig());

        // add connection to inUse LinkedList
        synchronized (inUse) {
            inUse.add(connLink);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(this, tc, "getConnectionLink: " + connLink);
        }
        return connLink;
    }

    /**
     * Remove the reference to the provided connection link as an active one.
     * 
     * @param connLink
     */
    public void removeConnLink(UDPConnLink connLink) {
        synchronized (inUse) {
            inUse.remove(connLink);
        }
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#start()
     */
    public void start() throws ChannelException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(this, tc, "start; " + getName());
        }
        if (stoppedFlag) // only start once
        {
            stoppedFlag = false;
            if (getConfig().isInboundChannel()) {
                inboundNetworkLayer = new UDPNetworkLayer(this, workQueueManager, getConfig().getHostname(), getConfig().getPort());
                try {
                    inboundNetworkLayer.initDatagramSocket(null);
                } catch (IOException e) {
                    stoppedFlag = true;
                    throw new RetryableChannelException(e);
                }

                String IPvType = "IPv4";
                if (inboundNetworkLayer.getDatagramSocket().getInetAddress() instanceof Inet6Address) {
                    IPvType = "IPv6";
                }

                String hostname = getConfig().getHostname();
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(this, tc, "getHostname():  >" + hostname + "<");

                if ((hostname == null) || (hostname.equals("*"))) {
                    displayableHostName = "*  (" + IPvType + ")";
                } else {
                    InetAddress addr = inboundNetworkLayer.getDatagramSocket().getLocalAddress();
                    displayableHostName = addr.getHostName() + "  (" + IPvType + ": " + addr.getHostAddress() + ")";
                }
                Tr.info(tc, "CWUDP0001I", new Object[] { getConfig().getExternalName(), displayableHostName, String.valueOf(inboundNetworkLayer.getListenPort()) });
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(this, tc, "start");
        }
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#stop(long)
     */
    public void stop(long millisec) throws ChannelException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(this, tc, "stop " + millisec + "; " + getName());
        }
        // only stop the channel if millisec is 0, otherwise ignore.
        if (millisec == 0) {
            stoppedFlag = true;

            // Stop accepting new connections on the inbound channels
            if (getConfig().isInboundChannel()) {

                try {
                    Tr.info(tc, "CWUDP0002I", new Object[] { getConfig().getExternalName(), displayableHostName, String.valueOf(getConfig().getPort()) });
                } catch (Exception e) {
                    // just debug,since this is on an audit attempt
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                        Tr.debug(this, tc, "Got exception auditing UDP Channel Stop: " + e);
                    }
                }
            }

            // destroy all the "in use" UDPConnLink. This should close all
            // the sockets held by these connections.
            destroyConnLinks();

        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(this, tc, "stop");
        }
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#init()
     */
    public void init() throws ChannelException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "UDP Channel: " + getConfig().getExternalName() + " listening port: " + String.valueOf(getConfig().getPort()));
        }
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#destroy()
     */
    public void destroy() throws ChannelException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(this, tc, "destroy; " + getName());
        }
        try {
            workQueueManager.shutdown();
        } catch (IOException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(this, tc, "Error trying to shutdown work queue manager " + e.getMessage());
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(this, tc, "destroy");
        }
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#getName()
     */
    public String getName() {
        return this.udpChannelConfig.getChannelData().getName();
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#getApplicationInterface()
     */
    public Class<?> getApplicationInterface() {
        return appSideClass;
    }

    /*
     * @see com.ibm.wsspi.channelfw.Channel#getDeviceInterface()
     */
    public Class<?> getDeviceInterface() {
        return null;
    }

    /*
     * @see
     * com.ibm.wsspi.channelfw.Channel#update(com.ibm.websphere.channelfw.ChannelData
     * )
     */
    public void update(ChannelData channelData) {
        synchronized (this.udpChannelConfig) {
            // can't do two updates at the same time
            try {
                this.udpChannelConfig.setChannelData(channelData);
            } catch (ChannelException e) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(this, tc, "Error updating config:" + e.getMessage());
                }
            }
        }
    }

    /**
     * call the destroy on all the UDPConnLink objects related to this
     * UDPChannel which are currently "in use".
     * 
     */
    private void destroyConnLinks() {
        synchronized (inUse) {
            int numlinks = inUse.size();
            for (int i = 0; i < numlinks; i++) {
                inUse.removeFirst().destroy(null);
            }
        }
    }

    protected UDPChannelConfiguration getConfig() {
        return this.udpChannelConfig;
    }

    /**
     * Verify whether the remote address is allowed to communicated with the
     * channel.
     * 
     * @param remoteAddr
     * @return boolean - false means it is denied
     */
    public boolean verifySender(InetAddress remoteAddr) {
        boolean returnValue = true;

        if (alists != null) {
            returnValue = !alists.accessDenied(remoteAddr);
        }
        return returnValue;
    }

}
