//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2008
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
// 09/30/04 gilgen      233025          Try re-open if port is in closing state
// 10/08/04 gilgen      235345.1        Do bind at init time instead of start time.
// 06/29/05 gilgen      287777          Support AIO extensions in WAS
// 04/08/06 clanzen     358818          Add remote address to vc statemap for ssl channel on Z/CRA
// 11/27/06 wigger      410109          store connection info in VC
// 01/17/07 leeja       PK37541         Socket re-use flag custom property
// 02/22/07 wigger      LIDB4463-8      store connection info in VC
// 03/08/07 leeja       PK40743         handle unresolvable localhost
// 06/15/07 wigger      LIDB4301-17     look for and use early bind objects
// 11/26/07 wigger      457142          Performance: don't access InetAddress methods until necessary
// 02/20/08 leeja       PK60924         Stop listening port on first quiesce pass
// 04/24/08 wigger      515681          Add FileChannel buffer support

package com.ibm.ws.tcpchannel.internal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.channelfw.internal.ConnectionDescriptorImpl;
import com.ibm.wsspi.channelfw.ConnectionDescriptor;
import com.ibm.wsspi.channelfw.InboundVirtualConnectionFactory;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.channelfw.VirtualConnectionFactory;
import com.ibm.wsspi.channelfw.exception.RetryableChannelException;
import com.ibm.wsspi.tcpchannel.TCPConfigConstants;
import com.ibm.wsspi.tcpchannel.TCPReadCompletedCallback;
import com.ibm.wsspi.tcpchannel.TCPRequestContext;

/**
 * A class which represents a specific end point. This class
 * links together a protocol name and an end point name. It is also
 * the "natural" place to manage the set of protocol inspectors which
 * get chance to look at an inbound request to decide how to handle
 * it.
 */
public class TCPPort {
    private TCPChannel tcpChannel = null;
    private ServerSocket serverSocket = null;
    protected InboundVirtualConnectionFactory vcf = null;
    private TCPReadCompletedCallback cc = null;
    private int listenPort = 0;

    private static final TraceComponent tc = Tr.register(TCPPort.class, TCPChannelMessageConstants.TCP_TRACE_NAME, TCPChannelMessageConstants.TCP_BUNDLE);

    /**
     * Constructor.
     * 
     * @param _tcpChannel
     * @param _vcf
     */
    protected TCPPort(TCPChannel _tcpChannel, VirtualConnectionFactory _vcf) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "TCPPort");
        }
        this.tcpChannel = _tcpChannel;
        this.vcf = (InboundVirtualConnectionFactory) _vcf;
        this.cc = new NewConnectionInitialReadCallback(tcpChannel);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "TCPPort");
        }
    }

    /**
     * Returns the server socket associated with this end point.
     * 
     * @return ServerSocket
     */
    protected synchronized ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    /**
     * Attempt a socket bind to the input address with the given re-use option
     * flag.
     * 
     * @param address
     * @param reuseflag
     * @throws IOException
     */
    private void attemptSocketBind(InetSocketAddress address, boolean reuseflag) throws IOException {
        this.serverSocket.setReuseAddress(reuseflag);
        this.serverSocket.bind(address, this.tcpChannel.getConfig().getListenBacklog());
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "ServerSocket bind worked, reuse=" + this.serverSocket.getReuseAddress());
        }
    }

    private BindInfo portBoundEarly(int port) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "portBoundEarly(int): " + port);
        }

        Map binds = TCPFactoryConfiguration.getEarlyBinds();

        if (binds != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Got Map of early binds");
            }

            BindInfo b = (BindInfo) binds.get(Integer.valueOf(port));

            if (b != null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Found Bind: " + b);
                }
            }

            if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
                Tr.exit(tc, "portBoundEarly(int)");
            }

            return b;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "portBoundEarly(int)");
        }
        return null;
    }

    /**
     * Initializes the server socket associated with this end point.
     * 
     * @return ServerSocket
     * @throws IOException
     * @throws RetryableChannelException
     */
    protected synchronized ServerSocket initServerSocket() throws IOException, RetryableChannelException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "ServerSocket called, new ServerSocket needs to be created");
        }
        TCPChannelConfiguration channelConfig = tcpChannel.getConfig();

        IOException bindError = null;
        BindInfo earlyBind = portBoundEarly(channelConfig.getPort());

        if (earlyBind == null) {
            InetSocketAddress socketAddress = null;

            if (channelConfig.getHostname() == null) {
                socketAddress = new InetSocketAddress((InetAddress) null, channelConfig.getPort());
            } else {
                socketAddress = new InetSocketAddress(channelConfig.getHostname(), channelConfig.getPort());
            }

            if (!socketAddress.isUnresolved()) {
                serverSocket = openServerSocket();

                // receieve buffer size for accepted sockets is set on serverSocket,
                // send buffer size is set on individual sockets

                if ((channelConfig.getReceiveBufferSize() >= TCPConfigConstants.RECEIVE_BUFFER_SIZE_MIN)
                    && (channelConfig.getReceiveBufferSize() <= TCPConfigConstants.RECEIVE_BUFFER_SIZE_MAX)) {
                    serverSocket.setReceiveBufferSize(channelConfig.getReceiveBufferSize());
                }
                if (!channelConfig.getSoReuseAddress()) {
                    //Forced re-use==false custom property
                    try {
                        attemptSocketBind(socketAddress, false);
                    } catch (IOException e) {
                        // no FFDC
                        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                            Tr.debug(tc, "Forced re-use==false bind attempt failed, ioe=" + e);
                        }
                        bindError = e;
                    }
                } else {
                    //re-use==true (default)
                    // try the standard startup attempts
                    try {
                        attemptSocketBind(socketAddress, false);
                        //If we are not on Windows and the bind succeeded, we should set reuseAddr=true
                        //for future binds.
                        if (!TCPFactoryConfiguration.isWindows()) {
                            this.serverSocket.setReuseAddress(true);
                            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                                Tr.debug(tc, "ServerSocket reuse set to true to allow for later override");
                            }
                        }
                    } catch (IOException ioe) {
                        // See if we got the error because the port is in waiting to be cleaned up.
                        // If so, no one should be accepting connections on it, and open should fail.
                        // If that's the case, we can set ReuseAddr to expedite the bind process.
                        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                            Tr.debug(tc, "ServerSocket bind failed on first attempt with IOException: " + ioe.getMessage());
                        }
                        bindError = ioe;
                        try {
                            String hostName = channelConfig.getHostname();
                            if (hostName == null) {
                                hostName = "localhost";
                            }
                            InetSocketAddress testAddr = new InetSocketAddress(hostName, channelConfig.getPort());
                            // PK40741 - test for localhost being resolvable before using it
                            if (!testAddr.isUnresolved()) {
                                SocketChannel testChannel = SocketChannel.open(testAddr);
                                // if we get here, socket opened successfully, which means
                                // someone is really listening
                                // so close connection and don't bother trying to bind again
                                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                                    Tr.debug(tc, "attempt to connect to port to check listen status worked, someone else is using the port!");
                                }
                                testChannel.close();
                            } else {
                                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                                    Tr.debug(tc, "Test connection addr is unresolvable; " + testAddr);
                                }
                            }
                        } catch (IOException testioe) {
                            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                                Tr.debug(tc, "attempt to connect to port to check listen status failed with IOException: " + testioe.getMessage());
                            }
                            try {
                                // open (or close) got IOException, retry with reuseAddr on
                                attemptSocketBind(socketAddress, true);
                                bindError = null;

                            } catch (IOException newioe) {
                                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                                    Tr.debug(tc, "ServerSocket bind failed on second attempt with IOException: " + newioe.getMessage());
                                }
                                bindError = newioe;
                            }
                        }
                    }
                }

                if (bindError == null) {
                    // listen port can be different than config port if configed port is '0'
                    listenPort = serverSocket.getLocalPort();
                } else {
                    String displayableHostName = channelConfig.getDisplayableHostname();
                    Tr.error(tc, TCPChannelMessageConstants.BIND_ERROR,
                             new Object[] { channelConfig.getChannelData().getExternalName(), displayableHostName, String.valueOf(channelConfig.getPort()) });
                    throw new RetryableChannelException(bindError.getMessage());
                }

            } else { // unresolved socket address
                String displayableHostName = channelConfig.getDisplayableHostname();
                Tr.error(tc, TCPChannelMessageConstants.LOCAL_HOST_UNRESOLVED,
                         new Object[] { channelConfig.getChannelData().getExternalName(), displayableHostName, String.valueOf(channelConfig.getPort()) });

                throw (new IOException("local address unresolved"));
            }

        } else {
            // this port was bound earlier by a different service
            Exception e = earlyBind.getBindException();
            if (e == null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Found early bind, setting serverSocket and listenPort");
                }

                serverSocket = earlyBind.getServerSocket();
                // listen port can be different than config port if configed port is '0'
                listenPort = serverSocket.getLocalPort();
            } else {

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Early Bind generated the following exception: " + e);
                }

                Tr.error(tc, TCPChannelMessageConstants.BIND_ERROR,
                         new Object[] { channelConfig.getChannelData().getExternalName(), earlyBind.getHostname(), String.valueOf(earlyBind.getPort()) });

                if (e instanceof IOException) {
                    throw (IOException) e;
                }
                if (e instanceof RetryableChannelException) {
                    throw (RetryableChannelException) e;
                }
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "new ServerSocket successfully created");
        }

        return this.serverSocket;
    }

    /**
     * Open the listening server socket.
     * 
     * @return ServerSocket
     * @throws IOException
     */
    protected ServerSocket openServerSocket() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        return ssc.socket();
    }

    /**
     * Destroys the server socket associated with this end point.
     */
    protected synchronized void destroyServerSocket() {
        if (null == this.serverSocket) {
            // already closed
            return;
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "ServerSocket being closed for port " + this.listenPort);
        }
        closeServerSocket();
        this.serverSocket = null;
    }

    /**
     * Destroys the server socket associated with this end point.
     */
    protected void closeServerSocket() {
        try {
            this.serverSocket.close();
        } catch (IOException ioe) {
            // no need to do anything except log it and dereference
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "IOExeption on ServerSocket.close " + ioe.getMessage());
            }
        }
    }

    /**
     * Returns the channel associated with this port.
     * 
     * @return TCPChannel
     */
    protected TCPChannel getTCPChannel() {
        return this.tcpChannel;
    }

    /**
     * Processes a new connection by scheduling initial read.
     * 
     * @param socket
     */
    public void processNewConnection(SocketIOChannel socket) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "processNewConnection");
        }

        VirtualConnection vc = vcf.createConnection();
        TCPConnLink bc = (TCPConnLink) tcpChannel.getConnectionLink(vc);
        TCPReadRequestContextImpl bcRead = bc.getTCPReadConnLink();

        bc.setSocketIOChannel(socket);

        ConnectionDescriptor cd = vc.getConnectionDescriptor();
        Socket s = socket.getSocket();
        InetAddress remote = s.getInetAddress();
        InetAddress local = s.getLocalAddress();

        if (cd != null) {
            cd.setAddrs(remote, local);
        } else {
            ConnectionDescriptorImpl cdi = new ConnectionDescriptorImpl(remote, local);

            vc.setConnectionDescriptor(cdi);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Processing Connection: " + vc.getConnectionDescriptor());
        }

        int rc = vc.attemptToSetFileChannelCapable(VirtualConnection.FILE_CHANNEL_CAPABLE_ENABLED);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "FileChannelCapable set in VC to: " + rc);
        }

        bcRead.setJITAllocateSize(bc.getConfig().getNewConnectionBufferSize());
        int timeout = bc.getConfig().getInactivityTimeout();
        if (timeout == ValidateUtils.INACTIVITY_TIMEOUT_NO_TIMEOUT) {
            timeout = TCPRequestContext.NO_TIMEOUT;
        }

        // Set a chain property that is used later by the SSL channel on Z only
        // (CRA)
        // This is needed to support a Z unique client certificate mapping function
        // which may be moved to non Z platforms in the future.
        vc.getStateMap().put("REMOTE_ADDRESS", bc.getRemoteAddress().getHostAddress());

        bcRead.read(1, cc, true, timeout);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "processNewConnection");
        }
    }

    /**
     * Query the listening port number.
     * 
     * @return int
     */
    protected int getListenPort() {
        return this.listenPort;
    }
}
