package com.ibm.ws.wsoc.outbound;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.transport.access.TransportConnectionAccess;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 *
 */
public class ClientTransportAccess implements TransportConnectionAccess {

    private static final TraceComponent tc = Tr.register(ClientTransportAccess.class);

    private TCPConnectionContext tcpConn;
    private VirtualConnection virtualConn;
    private ConnectionLink deviceConnLink;

    public ClientTransportAccess() {

    }

    @Override
    public TCPConnectionContext getTCPConnectionContext() {

        return tcpConn;
    }

    @Override
    public void setTCPConnectionContext(TCPConnectionContext input) {

        tcpConn = input;
    }

    @Override
    public ConnectionLink getDeviceConnLink() {

        return deviceConnLink;
    }

    @Override
    public void setDeviceConnLink(ConnectionLink input) {

        deviceConnLink = input;
    }

    @Override
    public VirtualConnection getVirtualConnection() {

        return virtualConn;
    }

    @Override
    public void setVirtualConnection(VirtualConnection input) {

        virtualConn = input;
    }

    @Override
    public void close() throws Exception {

    }

}
