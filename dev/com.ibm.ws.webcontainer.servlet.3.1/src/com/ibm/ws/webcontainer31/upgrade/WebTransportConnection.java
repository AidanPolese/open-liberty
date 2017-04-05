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
package com.ibm.ws.webcontainer31.upgrade;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.transport.access.TransportConnectionAccess;
import com.ibm.ws.webcontainer31.osgi.osgi.WebContainerConstants;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 *
 */
public class WebTransportConnection implements TransportConnectionAccess {


    private final static TraceComponent tc = Tr.register(WebTransportConnection.class, WebContainerConstants.TR_GROUP, WebContainerConstants.NLS_PROPS);

    private TCPConnectionContext tcpConn; 
    private VirtualConnection    virtualConn;
    private ConnectionLink deviceConnLink;
    private HttpUpgradeHandlerWrapper handler;

    public WebTransportConnection(HttpUpgradeHandlerWrapper upgradeHandler) {
        this.handler = upgradeHandler;
    }


    public TCPConnectionContext getTCPConnectionContext() {

        return tcpConn;
    }

    public void setTCPConnectionContext(TCPConnectionContext input) {

        tcpConn = input;
    }

    public ConnectionLink getDeviceConnLink() {

        return deviceConnLink;
    }

    public void setDeviceConnLink(ConnectionLink input) {

        deviceConnLink = input;
    }


    public VirtualConnection getVirtualConnection() {

        return virtualConn;
    }

    public void setVirtualConnection(VirtualConnection input) {

        virtualConn = input;
    }

    public void close() throws Exception {
        if (handler!=null) {
            handler.destroy();
        }
    }

}
