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
// 09/21/04 gilgen      233448          Add copyright statement and change history.
// 04/05/05 gilgen      LIDB3618-2      Added Aio support
// 04/21/05 wigger      LIDB3618-2      Aio support Changes
// 04/28/05 wigger      271613          Nio class break out changes
// 07/26/05 wigger      293682          fix logic error for blocking channel path
// 07/26/05 wigger      293682          don't bind with a null local address
// 08/30/05 wigger      300955          do connect with privileged thread
// 11/03/05 gilgen      320175          make accept and connect common
// 03/07/06 gilgen      352197          set SocketIOChannel in connlink as soon as its obtained
// 03/14/06 gilgen      354499          improve trace
// 11/27/06 wigger      410109          store connection info in VC
// 02/22/07 wigger      LIDB4463-8      store connection info in VC
// 11/26/07 wigger      457142          Performance: don't access InetAddress methods until necessary                                                                                                                                                                                                   

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.channelfw.internal.ConnectionDescriptorImpl;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.wsspi.channelfw.ConnectionDescriptor;
import com.ibm.wsspi.tcpchannel.TCPConfigConstants;
import com.ibm.wsspi.tcpchannel.TCPConnectRequestContext;

/**
 * This class manages a connection to an address / port.
 */
public class ConnectionManager implements TCPConfigConstants {
    private static final TraceComponent tc = Tr.register(ConnectionManager.class, TCPChannelMessageConstants.TCP_TRACE_NAME, TCPChannelMessageConstants.TCP_BUNDLE);

    private WorkQueueManager workQueueMgr = null;
    private TCPChannel tcpChannel = null;

    /**
     * Constructor.
     * 
     * @param _tcpChannel
     * @param wqm
     */
    public ConnectionManager(TCPChannel _tcpChannel, WorkQueueManager wqm) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "ConnectionManager");
        }
        this.tcpChannel = _tcpChannel;
        this.workQueueMgr = wqm;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "ConnectionManager");
        }
    }

    /**
     * Get a connection.
     * 
     * @param connectContext
     * @param tcpConnLink
     * @param blocking
     * @return socket connection for this source/destination combination
     * @throws IOException
     *             if there is an error in creating a new connection.
     */
    SocketIOChannel getConnection(TCPConnectRequestContext connectContext, TCPConnLink tcpConnLink, SimpleSync blocking) throws IOException {

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc,
                     "getConnection for local: " + connectContext.getLocalAddress() + ", remote: " + connectContext.getRemoteAddress() + ", timeout: "
                                     + connectContext.getConnectTimeout());
        }

        // create will throw an IOException or return a non-null value
        SocketIOChannel ioSocket = create(connectContext.getLocalAddress(), tcpConnLink);
        // set ioSocket in TCPConnLink
        tcpConnLink.setSocketIOChannel(ioSocket);

        boolean isConnected = ioSocket.connect(connectContext.getRemoteAddress());

        // if isConnected is true, then connect happened immediately.
        // This could happen even for non-blocking
        if (isConnected) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "obtained connection without queuing to selector");
            }
            tcpConnLink.setCallCompleteLocal(true);
            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                Tr.exit(tc, "getConnection");
            }
            return ioSocket;
        }
        // Create new work for the nonblocking connect selector - register for
        // finishConnect.
        ConnectInfo ci = new ConnectInfo(connectContext, tcpConnLink, ioSocket);
        ci.timeout = connectContext.getConnectTimeout();

        if (blocking != null) {
            // synchronous connect, so we want to do the connect work on this thread
            ci.setSyncObject(blocking);
        }

        ci.setFinish();
        workQueueMgr.queueConnectForSelector(ci);

        if (blocking != null) {
            // synchronous blocking call
            boolean connectDone = false;

            while (!connectDone) {
                blocking.simpleWait();

                connectDone = workQueueMgr.attemptConnectWork(ci);
            }

            if (ci.getAction() == ConnectInfo.FINISH_COMPLETE) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                    Tr.exit(tc, "getConnection");
                }
                return ioSocket;
            }
            if (ci.getError() == null) {
                ci.setError(new IOException("Connection could not be established"));
            }
            throw ci.getError();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getConnection");
        }

        // Return a null to the (async) caller to indicate the connect is still in
        // progress.
        return null;
    }

    /**
     * Do the real work of creating and configuring the SocketIOChannel.
     * 
     * @param localAddress
     * @param tcpConnLink
     * @return SocketIOChannel
     * @throws IOException
     */
    private SocketIOChannel create(InetSocketAddress localAddress, TCPConnLink tcpConnLink) throws IOException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "create");
        }

        SocketIOChannel ioSocket = tcpChannel.createOutboundSocketIOChannel();
        Socket socket = ioSocket.getSocket();
        TCPChannelConfiguration tcpConfig = this.tcpChannel.getConfig();

        socket.setReuseAddress(tcpConfig.getSoReuseAddress());

        if ((tcpConfig.getReceiveBufferSize() >= RECEIVE_BUFFER_SIZE_MIN) && (tcpConfig.getReceiveBufferSize() <= RECEIVE_BUFFER_SIZE_MAX)) {
            socket.setReceiveBufferSize(tcpConfig.getReceiveBufferSize());
        }

        if ((tcpConfig.getSendBufferSize() >= SEND_BUFFER_SIZE_MIN) && (tcpConfig.getSendBufferSize() <= SEND_BUFFER_SIZE_MAX)) {
            socket.setSendBufferSize(tcpConfig.getSendBufferSize());
        }

        // need this check, or else the getLocalAddress call will return all zeroes
        // if we bind with a null localAddress here
        if (localAddress != null) {
            try {
                socket.bind(localAddress);

                InetAddress ia = localAddress.getAddress();
                if (ia != null) {
                    ConnectionDescriptor cd = tcpConnLink.getVirtualConnection().getConnectionDescriptor();

                    if (cd != null) {
                        cd.setAddrs(null, ia);
                    } else {
                        ConnectionDescriptorImpl cdi = new ConnectionDescriptorImpl(null, ia);
                        tcpConnLink.getVirtualConnection().setConnectionDescriptor(cdi);
                    }

                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                        Tr.debug(tc, "bound connection: " + tcpConnLink.getVirtualConnection().getConnectionDescriptor());
                    }
                }

            } catch (IOException ioe) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Bind error making outbound connection " + ioe);
                }
                FFDCFilter.processException(ioe, getClass().getName(), "create", this);
                throw ioe;
            }
        }

        if (tcpConfig.getSoLinger() >= 0) {
            socket.setSoLinger(true, tcpConfig.getSoLinger());
        } else {
            socket.setSoLinger(false, 0);
        }

        socket.setKeepAlive(tcpConfig.getKeepAlive());

        socket.setTcpNoDelay(tcpConfig.getTcpNoDelay());

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Socket created, local port: " + socket.getLocalPort());
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "create");
        }

        return ioSocket;
    }

    /**
     * Wraps up all the information we need to keep track of during a
     * connect attempt into a single object. We do this so that we can
     * associate this information with the selection keys of sockets that
     * we are waiting to connect.
     */
    protected static class ConnectInfo {
        protected InetSocketAddress localAddress;
        protected InetSocketAddress remoteAddress;
        protected TCPConnLink tcpConnLink;
        protected SocketIOChannel ioSocket;
        protected SocketChannel channel;
        protected IOException errorException = null;
        protected SimpleSync syncObject = null;
        protected int timeout = TCPConnectRequestContext.NO_TIMEOUT;
        protected long nextTimeoutTime = 0;

        protected static final int GET_CONNECTION = 0;
        protected static final int FINISH_CONNECTION = 1;
        protected static final int CALL_ERROR = 2;
        protected static final int FINISH_COMPLETE = 3;
        protected int action = GET_CONNECTION;

        protected ConnectInfo(TCPConnectRequestContext connectContext, TCPConnLink cl, SocketIOChannel socket) {
            this.localAddress = connectContext.getLocalAddress();
            this.remoteAddress = connectContext.getRemoteAddress();
            this.tcpConnLink = cl;
            this.ioSocket = socket;

            if (socket != null) {
                this.channel = socket.getChannel();
            } else {
                this.channel = null;
            }
        }

        protected int getAction() {
            return this.action;
        }

        protected void setError(IOException e) {
            this.action = CALL_ERROR;
            this.errorException = e;
        }

        protected IOException getError() {
            return this.errorException;
        }

        protected void setFinish() {
            this.action = FINISH_CONNECTION;
        }

        protected void setFinishComplete() {
            this.action = FINISH_COMPLETE;
        }

        protected void setSyncObject(SimpleSync newSync) {
            this.syncObject = newSync;
        }

        protected SimpleSync getSyncObject() {
            return this.syncObject;
        }

    }

}
