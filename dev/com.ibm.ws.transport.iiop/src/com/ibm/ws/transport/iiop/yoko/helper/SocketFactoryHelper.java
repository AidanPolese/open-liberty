/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.yoko.helper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.yoko.orb.OCI.IIOP.ExtendedConnectionHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.IIOP.ProfileBody_1_0;
import org.omg.IIOP.ProfileBody_1_0Helper;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactoryHelper;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.IOP.Encoding;
import org.omg.IOP.IOR;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 *
 */
public abstract class SocketFactoryHelper implements ExtendedConnectionHelper {

    private final TraceComponent tc;
    private static final Encoding CDR_1_2_ENCODING = new Encoding(ENCODING_CDR_ENCAPS.value, (byte) 1, (byte) 2);

    protected SocketFactoryHelper(TraceComponent tc) {
        this.tc = tc;
    }

    @FFDCIgnore(IOException.class)
    protected IOException openSocket(int port, int backlog, InetAddress address, ServerSocket socket, boolean soReuseAddr) {
        SocketAddress socketAddress = new InetSocketAddress(address, port);

        //This code borrowed from TCPPort in channelFw:
        IOException bindError = null;
        if (!soReuseAddr) {
            //Forced re-use==false custom property
            try {
                attemptSocketBind(socket, socketAddress, false, backlog);
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
                attemptSocketBind(socket, socketAddress, false, backlog);
                //If we are not on Windows and the bind succeeded, we should set reuseAddr=true
                //for future binds.
                if (!isWindows()) {
                    socket.setReuseAddress(true);
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
                    String hostName = address == null ? "localhost" : address.getHostName();
                    InetSocketAddress testAddr = new InetSocketAddress(hostName, port);
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
                        attemptSocketBind(socket, socketAddress, true, backlog);
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
        return bindError;
    }

    /**
     * Attempt a socket bind to the input address with the given re-use option
     * flag.
     * 
     * @param address
     * @param reuseflag
     * @throws IOException
     */
    private void attemptSocketBind(ServerSocket serverSocket, SocketAddress address, boolean reuseflag, int backlog) throws IOException {
        serverSocket.setReuseAddress(reuseflag);
        serverSocket.bind(address, backlog);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "ServerSocket bind worked, reuse=" + serverSocket.getReuseAddress());
        }
    }

    private static String osName;

    private static String getOSName() {
        if (osName == null) {
            osName = System.getProperty("os.name", "unknown");
        }
        return osName;
    }

    private static boolean isWindows() {
        String name = getOSName();
        return name.toLowerCase().startsWith("windows");
    }

    protected ORB orb;
    protected Codec codec;

    /**
     * Initialize the socket factory instance.
     * 
     * @param orb The hosting ORB.
     * @param configName The initialization parameter passed to the socket factor.
     *            This contains the abstract name of our configurator,
     *            which we retrieve from a registry.
     */
    @FFDCIgnore({ UnknownEncoding.class, InvalidName.class })
    @Override
    public void init(ORB orb, String configName) {
        this.orb = orb;
        try {
            this.codec = CodecFactoryHelper.narrow(orb.resolve_initial_references("CodecFactory")).create_codec(CDR_1_2_ENCODING);
        } catch (UnknownEncoding e) {
            // TODO Auto-generated catch block
            // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
            // https://websphere.pok.ibm.com/~alpine/secure/docs/dev/API/com.ibm.ws.ras/com/ibm/ws/ffdc/annotation/FFDCIgnore.html
            // e.printStackTrace();
        } catch (InvalidName e) {
            // TODO Auto-generated catch block
            // Do you need FFDC here? Remember FFDC instrumentation and @FFDCIgnore
            // https://websphere.pok.ibm.com/~alpine/secure/docs/dev/API/com.ibm.ws.ras/com/ibm/ws/ffdc/annotation/FFDCIgnore.html
            // e.printStackTrace();
        }
    }

    protected Socket createSocketFromProfile(IOR ior, String host, int port) throws IOException {
        // the Yoko ORB will use both the primary and secondary targets for connections, which
        // sometimes gets us into trouble, forcing us to use an SSL target when we really need to
        // use the plain socket connection.  Therefore, we will ignore what's passed to us,
        // and extract the primary port information directly from the profile.
        for (int i = 0; i < ior.profiles.length; i++) {
            if (ior.profiles[i].tag == org.omg.IOP.TAG_INTERNET_IOP.value) {
                try {
                    //
                    // Get the IIOP profile body
                    //
                    byte[] data = ior.profiles[i].profile_data;
                    ProfileBody_1_0 body = ProfileBody_1_0Helper.extract(codec.decode_value(data, ProfileBody_1_0Helper.type()));

                    //
                    // Create new connector for this profile
                    //
                    if (body.port < 0) {
                        port = 0xffff + body.port + 1;
                    } else {
                        port = body.port;
                    }
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "set port from IOR: " + port);
                    break; // TODO: Verify under what conditions we can break.
                } catch (org.omg.IOP.CodecPackage.FormatMismatch e) {
                    // just keep the original port.
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "could not set port from IOR: ", e);
                    break;
                } catch (org.omg.IOP.CodecPackage.TypeMismatch e) {
                    // just keep the original port.
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                        Tr.debug(tc, "could not set port from IOR: ", e);
                    break;
                }

            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "Creating plain endpoint to " + host + ":" + port);
        return new Socket(host, port);
    }

}
