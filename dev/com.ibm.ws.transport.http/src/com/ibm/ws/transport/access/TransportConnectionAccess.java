/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.access;

import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

//import java.io.InputStream;
//import java.io.OutputStream;

/**
 *
 */
public interface TransportConnectionAccess {

    public TCPConnectionContext getTCPConnectionContext();

    public void setTCPConnectionContext(TCPConnectionContext x);

    public ConnectionLink getDeviceConnLink();

    public void setDeviceConnLink(ConnectionLink x);

    public VirtualConnection getVirtualConnection();

    public void setVirtualConnection(VirtualConnection x);

    public void close() throws Exception;

}
