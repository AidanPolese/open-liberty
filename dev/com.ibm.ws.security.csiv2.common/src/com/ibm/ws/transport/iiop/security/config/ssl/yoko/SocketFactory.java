/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security.config.ssl.yoko;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.yoko.orb.OB.IORDump;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.ProfileInfoHolder;
import org.omg.CORBA.Policy;
import org.omg.CSIIOP.EstablishTrustInClient;
import org.omg.CSIIOP.NoProtection;
import org.omg.CSIIOP.TAG_CSI_SEC_MECH_LIST;
import org.omg.CSIIOP.TransportAddress;
import org.omg.IOP.IOR;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.SSLException;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.csiv2.config.CompatibleMechanisms;
import com.ibm.ws.security.csiv2.config.ssl.SSLConfig;
import com.ibm.ws.security.csiv2.config.tss.ServerTransportAddress;
import com.ibm.ws.security.csiv2.util.SecurityServices;
import com.ibm.ws.transport.iiop.security.ClientPolicy;
import com.ibm.ws.transport.iiop.security.config.css.CSSConfig;
import com.ibm.ws.transport.iiop.security.config.css.CSSTransportMechConfig;
import com.ibm.ws.transport.iiop.security.config.tss.OptionsKey;
import com.ibm.ws.transport.iiop.security.config.tss.TSSCompoundSecMechListConfig;
import com.ibm.ws.transport.iiop.security.config.tss.TSSSSLTransportConfig;
import com.ibm.ws.transport.iiop.security.config.tss.TSSTransportMechConfig;
import com.ibm.ws.transport.iiop.yoko.helper.SocketFactoryHelper;

/**
 * Socket factory instance used to interface openejb2
 * with the Yoko ORB. Also enables the ORB for
 * SSL-type connections.
 *
 * @version $Revision: 505035 $ $Date: 2007-02-08 16:01:06 -0500 (Thu, 08 Feb 2007) $
 */
public class SocketFactory extends SocketFactoryHelper {
    private static final TraceComponent tc = Tr.register(SocketFactory.class);

    private final Map<String, SSLSocketFactory> socketFactoryMap = new HashMap<String, SSLSocketFactory>(1);
    private final Map<String, SSLServerSocketFactory> serverSocketFactoryMap = new HashMap<String, SSLServerSocketFactory>(1);
    // The initialized SSLConfig we use to retrieve the SSL socket factories.
    private final SSLConfig sslConfig;

    private static final class SocketInfo {
        final InetAddress addr;
        final int port;
        final OptionsKey key;
        final String sslConfigName;

        /**
         * @param addr
         * @param port
         * @param key
         * @param sslConfigName
         */
        public SocketInfo(InetAddress addr, int port, OptionsKey key, String sslConfigName) {
            super();
            this.addr = addr;
            this.port = port;
            this.key = key;
            this.sslConfigName = sslConfigName;
        }

    }

    //Liberty TODO remove socketInfos when server sockets are closed???
    private final List<SocketInfo> socketInfos = new ArrayList<SocketInfo>();

    public SocketFactory() {
        super(tc);
        sslConfig = SecurityServices.getSSLConfig();
    }

    /**
     * Create a client socket of the appropriate
     * type using the provided IOR and Policy information.
     *
     * @param ior The target IOR of the connection.
     * @param policies Policies in effect for this ORB.
     * @param address The target address of the connection.
     * @param port The connection port.
     *
     * @return A Socket (either plain or SSL) configured for connection
     *         to the target.
     * @exception IOException
     * @exception ConnectException
     */
    @Override
    public Socket createSocket(IOR ior, Policy[] policies, InetAddress address, int port) throws IOException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "SocketFactory attempting to create socket for address: " + address + " port: " + port);
            Tr.debug(tc, "Policies: " + Arrays.asList(policies));
            Tr.debug(tc, IORDump.PrintObjref(orb, ior));
        }

        String host = address.getHostName();

        CSSConfig cssConfig = null;
        for (Policy policy : policies) {
            if (policy instanceof ClientPolicy) {
                cssConfig = ((ClientPolicy) policy).getConfig();
                break;
            }
        }
        if (cssConfig != null) {

            ProfileInfoHolder holder = new ProfileInfoHolder();
            // we need to extract the profile information from the IOR to see if this connection has
            // any transport-level security defined.
            if (org.apache.yoko.orb.OCI.IIOP.Util.extractProfileInfo(ior, holder)) {
                ProfileInfo profileInfo = holder.value;
                for (int i = 0; i < profileInfo.components.length; i++) {
                    // we're looking for the security mechanism items.
                    if (profileInfo.components[i].tag == TAG_CSI_SEC_MECH_LIST.value) {
                        // decode and pull the transport information.
                        TSSCompoundSecMechListConfig config;
                        try {
                            config = TSSCompoundSecMechListConfig.decodeIOR(codec, profileInfo.components[i]);
                        } catch (Exception e) {
                            throw new IOException("Could not decode IOR TSSCompoundSecMechListConfig", e);
                        }
                        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                            Tr.debug(tc, "looking at tss: " + config);
                        }
                        LinkedList<CompatibleMechanisms> compatibleMechanismsList = cssConfig.findCompatibleList(config);
                        for (CompatibleMechanisms compatibleMechanisms : compatibleMechanismsList) {
                            Map<ServerTransportAddress, CSSTransportMechConfig> cssTransport_mechs = compatibleMechanisms.getCSSCompoundSecMechConfig().getTransportMechMap();
                            TSSTransportMechConfig transport_mech = compatibleMechanisms.getTSSCompoundSecMechConfig().getTransport_mech();

                            if (transport_mech instanceof TSSSSLTransportConfig) {
                                // if cssTransport_mechs is empty there we are not dealing with dynamic SSL config
                                if (!cssTransport_mechs.isEmpty()) {
                                    Socket socket = createSocketFromTransportMechList(cssTransport_mechs, transport_mech);
                                    if (socket != null)
                                        return socket;
                                } else {
                                    String sslConfigName = compatibleMechanisms.getCSSCompoundSecMechConfig().getTransport_mech().getSslConfigName();
                                    if (((TSSSSLTransportConfig) transport_mech).getTransportAddresses().length > 0) {
                                        Socket socket = createSocketFromTransportMech(transport_mech, sslConfigName);
                                        if (socket != null)
                                            return socket;
                                    } else
                                        continue;
                                }
                            } else {
                                try {
                                    return createSocketFromProfile(ior, host, port);
                                } catch (IOException e) {
                                    // Ignore so that the next mechanism can be tried, should be logged?
                                }
                            }
                        }
                        throw new IOException("No connection  possible with any matching address");
                    }
                }
            }
        }

        //SSL not needed, look in the profile for host/port
        return createSocketFromProfile(ior, host, port);
    }

    /**
     * @param transport_mech
     * @param sslConfigName
     * @return
     * @throws IOException
     */
    private Socket createSocketFromTransportMech(TSSTransportMechConfig transport_mech, String sslConfigName) throws IOException {
        TSSSSLTransportConfig transportConfig = (TSSSSLTransportConfig) transport_mech;

        int requires = transportConfig.getRequires();
        TransportAddress[] addresses = transportConfig.getTransportAddresses();
        for (TransportAddress addr : addresses) {
            int sslPort = addr.port;
            String sslHost = addr.host_name;
            InetAddress ina = InetAddress.getByName(sslHost);
            sslHost = ina.getCanonicalHostName();
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "IOR to target " + sslHost + ":" + sslPort + " using client sslConfig " + sslConfigName);
            }
            // TLS is configured.  If this is explicitly noprotection, then
            // just go create a plain socket using the configured port.
            try {
                if ((NoProtection.value & requires) == NoProtection.value) {
                    return new Socket(ina, sslPort);
                }
                //TODO should we additionally filter the cipher suites by what the target requires and supports, or let the negotiation do that?
                // we need SSL, so create an SSLSocket for this connection.
                return createSSLSocket(sslHost, sslPort, sslConfigName);
            } catch (IOException e) {
                //ignore, should be logged?
            }
        }

        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param cssTransport_mechs
     * @param transport_mech
     * @return
     * @throws IOException
     */
    private Socket createSocketFromTransportMechList(Map<ServerTransportAddress, CSSTransportMechConfig> cssTransport_mechs,
                                                     TSSTransportMechConfig transport_mech) throws IOException {
        TSSSSLTransportConfig transportConfig = (TSSSSLTransportConfig) transport_mech;
        int requires = transportConfig.getRequires();

        for (Map.Entry<ServerTransportAddress, CSSTransportMechConfig> entry : cssTransport_mechs.entrySet()) {

            ServerTransportAddress addr = entry.getKey();
            CSSTransportMechConfig mech_cfg = entry.getValue();

            String sslConfigName = mech_cfg.getSslConfigName();

            int sslPort = addr.getPort();
            String sslHost = addr.getHost();
            InetAddress ina = InetAddress.getByName(sslHost);
            sslHost = ina.getCanonicalHostName();
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "IOR to target " + sslHost + ":" + sslPort + " using client sslConfig " + sslConfigName);
            }
            // TLS is configured.  If this is explicitly noprotection, then
            // just go create a plain socket using the configured port.
            try {
                if ((NoProtection.value & requires) == NoProtection.value) {
                    return new Socket(ina, sslPort);
                }
                //TODO should we additionally filter the cipher suites by what the target requires and supports, or let the negotiation do that?
                // we need SSL, so create an SSLSocket for this connection.
                return createSSLSocket(sslHost, sslPort, sslConfigName);
            } catch (IOException e) {
                //ignore, should be logged?
            }
        }

        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Create a loopback connection to the hosting
     * ORB.
     *
     * @param address The address information for the server.
     * @param port The target port.
     *
     * @return An appropriately configured socket based on the
     *         listener characteristics.
     * @exception IOException
     * @exception ConnectException
     */
    @Override
    @FFDCIgnore(IOException.class)
    public Socket createSelfConnection(InetAddress address, int port) throws IOException {
        try {
            SocketInfo info = null;
            for (SocketInfo test : socketInfos) {
                if (test.port == port && test.addr.equals(address)) {
                    info = test;
                }
            }
            if (info == null) {
                throw new IOException("No inbound socket matching address " + address + " and port " + port);
            }
            OptionsKey key = info.key;
            // the requires information tells us whether we created a plain or SSL listener.  We need to create one
            // of the matching type.

            if ((NoProtection.value & key.requires) == NoProtection.value) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                    Tr.debug(tc, "Created plain endpoint to " + address.getHostName() + ":" + port);
                return new Socket(address, port);
            } else {
                return createSSLSocket(address.getHostName(), port, info.sslConfigName);
            }
        } catch (IOException ex) {
            Tr.error(tc, "Exception creating a client socket to " + address.getHostName() + ":" + port, ex);
            throw ex;
        }
    }

    /**
     * Create a server socket listening on the given port.
     *
     * @param port The target listening port.
     * @param backlog The desired backlog value.
     *
     * @return An appropriate server socket for this connection.
     * @exception IOException
     * @exception ConnectException
     */
    @Override
    public ServerSocket createServerSocket(int port, int backlog, String[] params) throws IOException {
        return createServerSocket(port, backlog, null, params);
    }

    /**
     * Create a server socket for this connection.
     *
     * @param port The target listener port.
     * @param backlog The requested backlog value for the connection.
     * @param address The host address information we're publishing under.
     *
     * @return An appropriately configured ServerSocket for this
     *         connection.
     * @exception IOException
     * @exception ConnectException
     */
    @SuppressWarnings("resource")
    @Override
    public ServerSocket createServerSocket(int port, int backlog, InetAddress address, String[] params) throws IOException {
        try {
            ServerSocket socket;
            String sslConfigName = null;
            boolean soReuseAddr = true;
            for (int i = 0; i < params.length - 1; i++) {
                String param = params[i];
                if ("--sslConfigName".equals(param)) {
                    sslConfigName = params[++i];
                }
                if ("--soReuseAddr".equals(param)) {
                    soReuseAddr = Boolean.parseBoolean(params[++i]);
                }
            }
            OptionsKey options = sslConfig.getAssociationOptions(sslConfigName);
            // if no protection is required, just create a plain socket.
            if ((NoProtection.value & options.requires) == NoProtection.value) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                    Tr.debug(tc, "Created plain server socket for port " + port);
                socket = new ServerSocket();
            } else {
                // SSL is required.  Create one from the SSLServerFactory retrieved from the config.  This will
                // require additional QOS configuration after creation.
                SSLServerSocketFactory serverSocketFactory = getServerSocketFactory(sslConfigName);
                SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket();
                configureServerSocket(serverSocket, serverSocketFactory, sslConfigName, options);
                socket = serverSocket;
            }
            // there is a situation that yoko closes and opens a server socket quickly upon updating
            // the configuration, and occasionally, the openSocket is invoked while closeSocket is processing.
            // To avoid the issue, try binding the socket a few times. Since this is the error scenario,
            // it is less impact for the performance.
            IOException bindError = null;
            for (int i = 0; i < 3; i++) {
                bindError = openSocket(port, backlog, address, socket, soReuseAddr);
                if (bindError == null) {
                    break;
                }
                try {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                        Tr.debug(tc, "bind error, retry binding... count : " + i);
                    Thread.sleep(500L);
                } catch (Exception e) {
                    Tr.debug(tc, "An exception is caught while retrying binding. the error message is  " + e.getMessage());
                }
            }
            if (bindError == null) {
                // listen port can be different than config port if configed port is '0'
                int listenPort = socket.getLocalPort();
                SocketInfo info = new SocketInfo(address, listenPort, options, sslConfigName);
                socketInfos.add(info);
            } else {
                Tr.error(tc, "SOCKET_BIND_ERROR", address.getHostName(), port, bindError.getLocalizedMessage());
                throw bindError;
            }

            return socket;
        } catch (SSLException e) {
            throw new IOException("Could not retrieve association options from ssl configuration", e);
        }
    }

    /**
     * On-demand creation of an SSL socket factory for the ssl alias provided
     *
     * @return The SSLSocketFactory this connection should be using to create
     *         secure connections.
     * @throws java.io.IOException if we can't get a socket factory
     */
    private SSLSocketFactory getSocketFactory(String id) throws IOException {
        // first use?
        SSLSocketFactory socketFactory = socketFactoryMap.get(id);
        if (socketFactory == null) {
            // the SSLConfig is optional, so if it's not there, use the default SSLSocketFactory.
            if (id == null) {
                socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            } else {
                // ask the SSLConfig bean to create a factory for us.
                try {
                    socketFactory = sslConfig.createSSLFactory(id);
                } catch (Exception e) {
                    Tr.error(tc, "Unable to create client SSL socket factory", e);
                    throw (IOException) new IOException("Unable to create client SSL socket factory: " + e.getMessage()).initCause(e);
                }
            }
            socketFactoryMap.put(id, socketFactory);
        }
        return socketFactory;
    }

    /**
     * On-demand creation of an SSL server socket factory for an ssl alias
     *
     * @return The SSLServerSocketFactory this connection should be using to create
     *         secure connections.
     * @throws java.io.IOException if we can't get a server socket factory
     */
    private SSLServerSocketFactory getServerSocketFactory(String id) throws IOException {
        // first use?
        SSLServerSocketFactory serverSocketFactory = serverSocketFactoryMap.get(id);
        if (serverSocketFactory == null) {
            // the SSLConfig is optional, so if it's not there, use the default SSLSocketFactory.
            if (id == null) {
                serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            } else {
                try {
                    serverSocketFactory = sslConfig.createSSLServerFactory(id);
                } catch (Exception e) {
                    Tr.error(tc, "Unable to create server SSL socket factory", e);
                    throw (IOException) new IOException("Unable to create server SSL socket factory: " + e.getMessage()).initCause(e);
                }
                serverSocketFactoryMap.put(id, serverSocketFactory);
            }
            // There's a bit of a timing problem with server-side ORBs.  Part of the ORB shutdown is to
            // establish a self-connection to shutdown the acceptor threads.  This requires a client
            // SSL socket factory.  Unfortunately, if this is occurring during server shutdown, the
            // FileKeystoreManager will get a NullPointerException because some name queries fail because
            // things are getting shutdown.  Therefore, if we need the server factory, assume we'll also
            // need the client factory to shutdown, and request it now.
            getSocketFactory(id);
        }
        return serverSocketFactory;
    }

    /**
     * Set the server socket configuration to our required
     * QOS values.
     *
     * A small experiment shows that setting either (want, need) parameter to either true or false sets the
     * other parameter to false.
     *
     * @param serverSocket
     *            The newly created SSLServerSocket.
     * @param sslConfigName name of the sslConfig used to select cipher suites
     * @param options supported/required flags
     * @throws IOException if server socket can't be configured
     * @throws SSLException
     */
    private void configureServerSocket(SSLServerSocket serverSocket, SSLServerSocketFactory serverSocketFactory, String sslConfigName, OptionsKey options) throws IOException {
        try {
            String[] cipherSuites = sslConfig.getCipherSuites(sslConfigName, serverSocketFactory.getSupportedCipherSuites());

            serverSocket.setEnabledCipherSuites(cipherSuites);

            boolean clientAuthRequired = ((options.requires & EstablishTrustInClient.value) == EstablishTrustInClient.value);
            boolean clientAuthSupported = ((options.supports & EstablishTrustInClient.value) == EstablishTrustInClient.value);
            if (clientAuthRequired) {
                serverSocket.setNeedClientAuth(true);
            } else if (clientAuthSupported) {
                serverSocket.setWantClientAuth(true);
            } else {
                serverSocket.setNeedClientAuth(false); //could set want with the same effect
            }
            serverSocket.setSoTimeout(60 * 1000);

            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.debug(tc, "Created SSL server socket on port " + serverSocket.getLocalPort());
                Tr.debug(tc, "    client authentication " + (clientAuthSupported ? "SUPPORTED" : "UNSUPPORTED"));
                Tr.debug(tc, "    client authentication " + (clientAuthRequired ? "REQUIRED" : "OPTIONAL"));
                Tr.debug(tc, "    cipher suites:");

                for (int i = 0; i < cipherSuites.length; i++) {
                    Tr.debug(tc, "    " + cipherSuites[i]);
                }
            }
        } catch (SSLException e) {
            throw new IOException("Could not configure server socket", e);
        }
    }

    /**
     * Create an SSL client socket using the IOR-encoded
     * security characteristics.
     * Setting want/need client auth on a client socket has no effect so all we can do is use the right host, port, ciphers
     *
     * @param host The target host name.
     * @param port The target connection port.
     * @param clientSSLConfigName name of the sslConfig used for cipher suite selection
     * @return An appropriately configured client SSLSocket.
     * @exception IOException if ssl socket can't be obtained and configured.
     */
    private Socket createSSLSocket(String host, int port, final String clientSSLConfigName) throws IOException {
        final SSLSocketFactory factory = getSocketFactory(clientSSLConfigName);
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

        socket.setSoTimeout(60 * 1000);

        // get a set of cipher suites appropriate for this connections requirements.
        // We request this for each connection, since the outgoing IOR's requirements may be different from
        // our server listener requirements.
        String[] iorSuites;
        try {
            iorSuites = (String[]) AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    return sslConfig.getCipherSuites(clientSSLConfigName, factory.getSupportedCipherSuites());
                }
            });
        } catch (PrivilegedActionException pae) {
            throw new IOException("Could not configure client socket", pae.getCause());
        }
        socket.setEnabledCipherSuites(iorSuites);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.debug(tc, "Created SSL socket to " + host + ":" + port);
            Tr.debug(tc, "    cipher suites:");

            for (int i = 0; i < iorSuites.length; i++) {
                Tr.debug(tc, "    " + iorSuites[i]);
            }
            socket.addHandshakeCompletedListener(new HandshakeCompletedListener() {

                @Override
                public void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent) {
                    Certificate[] certs = handshakeCompletedEvent.getLocalCertificates();
                    if (certs != null) {
                        Tr.debug(tc, "handshake returned local certs count: " + certs.length);
                        for (int i = 0; i < certs.length; i++) {
                            Certificate cert = certs[i];
                            Tr.debug(tc, "cert: " + cert.toString());
                        }
                    } else {
                        Tr.debug(tc, "handshake returned no local certs");
                    }
                }
            });
        }
        return socket;
    }
}
