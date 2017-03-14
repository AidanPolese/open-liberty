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
package com.ibm.wsspi.http.ee7;

import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.http.HttpInboundConnection;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 *
 */
public interface HttpInboundConnectionExtended extends HttpInboundConnection {

    // todo add javadoc to these three methods when the code has been finalized.  This is in the SPI, so would be better to minimize additions here

    /**
     * This API will return the TCPConnectionContext.
     * 
     * @return
     */
    TCPConnectionContext getTCPConnectionContext();

    /**
     * This API will return the VirtualConnection.
     * 
     * @return
     */
    VirtualConnection getVC();

    /**
     * This API will return the device link.
     * 
     * @return
     */
    ConnectionLink getHttpInboundDeviceLink();

    /**
     * This API will return the application link.
     * 
     * @return
     */
    ConnectionLink getHttpInboundLink();

    /**
     * This API will return the HttpDispatcherLink.
     * 
     * @return
     */
    ConnectionLink getHttpDispatcherLink();
}
