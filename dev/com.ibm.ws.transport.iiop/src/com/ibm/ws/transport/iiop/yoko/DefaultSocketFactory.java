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
package com.ibm.ws.transport.iiop.yoko;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.omg.CORBA.Policy;
import org.omg.IOP.IOR;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.transport.iiop.yoko.helper.SocketFactoryHelper;

/**
 *
 */
public class DefaultSocketFactory extends SocketFactoryHelper {

    private static final TraceComponent tc = Tr.register(DefaultSocketFactory.class);

    public DefaultSocketFactory() {
        super(tc);
    }

    @Override
    public Socket createSocket(IOR ior, Policy[] policies, InetAddress address, int port) throws IOException, ConnectException {
        String host = address.getHostName();
        if (port == 0) {
            Tr.error(tc, "PORT_ZERO", host);
        }
        return createSocketFromProfile(ior, host, port);
    }

    @Override
    public Socket createSelfConnection(InetAddress address, int port) throws IOException, ConnectException {
        return new Socket(address, port);
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog, String[] params) throws IOException {
        return createServerSocket(port, backlog, null, params);
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog, InetAddress address, String[] params) throws IOException {
        boolean soReuseAddr = true;
        for (int i = 0; i < params.length - 1; i++) {
            String param = params[i];
            if ("--soReuseAddr".equals(param)) {
                soReuseAddr = Boolean.parseBoolean(params[++i]);
                break;
            }
        }
        ServerSocket socket = new ServerSocket();
        IOException bindError = openSocket(port, backlog, address, socket, soReuseAddr);
        if (bindError != null) {
            throw bindError;
        }
        return socket;
    }

}
