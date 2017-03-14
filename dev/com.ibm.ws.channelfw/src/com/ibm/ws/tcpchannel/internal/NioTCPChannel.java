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
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 04/15/05 gilgen      LIDB3618-2      Created file - split out of NIO functions from base
// 04/21/05 wigger      LIDB3618-2      Aio support Changes
// 04/28/05 wigger      271613          Nio class break out changes
// 07/29/05 wigger      294806          Clean up comments
// 08/01/05 wigger      294806          pass back termination object
// 11/03/05 gilgen      320175          make accept and connect common
// 03/15/06 wigger      354970          return WQM/SWQM based on PureNonBlocking setting
// 03/31/06 wigger      358208          prevent security excpetions

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.channelfw.exception.ChannelException;

/**
 * NIO specific TCP channel instance.
 */
public class NioTCPChannel extends TCPChannel {

    private static WorkQueueManager workQueueManager = null;

    private static final TraceComponent tc = Tr.register(NioTCPChannel.class, TCPChannelMessageConstants.TCP_TRACE_NAME, TCPChannelMessageConstants.TCP_BUNDLE);

    /**
     * Constructor.
     */
    public NioTCPChannel() {
        super();
    }

    public ChannelTermination setup(ChannelData runtimeConfig, TCPChannelConfiguration tcpConfig, TCPChannelFactory factory) throws ChannelException {

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "setup");
        }

        super.setup(runtimeConfig, tcpConfig, factory);
        // create WorkQueueMgr if this is the first NonBlocking Channel that
        // is being created.

        if (workQueueManager == null) {
            workQueueManager = new WorkQueueManager();
        }

        if (!config.isInbound()) {
            connectionManager = new ConnectionManager(this, workQueueManager);
        }

        workQueueManager.startSelectors(config.isInbound());

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "setup");
        }
        return getWorkQueueManager();
    }

    /**
     * Returns the WorkQueueManager reference.
     * 
     * @return WorkQueueManager
     */
    protected WorkQueueManager getWorkQueueManager() {
        return workQueueManager;
    }

    // LIDB3618-2 add method
    public SocketIOChannel createOutboundSocketIOChannel() throws IOException {
        SocketChannel channel = SocketChannel.open();
        Socket socket = channel.socket();
        return NioSocketIOChannel.createIOChannel(socket, this);
    }

    public SocketIOChannel createInboundSocketIOChannel(SocketChannel sc) {
        return NioSocketIOChannel.createIOChannel(sc.socket(), this);
    }

    public TCPReadRequestContextImpl createReadInterface(TCPConnLink connLink) {
        return new NioTCPReadRequestContextImpl(connLink);
    }

    public TCPWriteRequestContextImpl createWriteInterface(TCPConnLink connLink) {
        return new NioTCPWriteRequestContextImpl(connLink);
    }

}
