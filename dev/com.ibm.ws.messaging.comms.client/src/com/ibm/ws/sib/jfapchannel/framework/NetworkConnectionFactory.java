/*/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/NetworkConnectionFactory.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:29 [4/12/12 22:14:18]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2003, 2006 
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 336594          060109 prestona JFAP channel for thin client
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * 95897           041613 Chetan   Comms Outbound Chain revamp
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.framework;

import com.ibm.wsspi.channelfw.VirtualConnectionFactory;

/**
 * A factory for implementations of the NetworkConnection interface.
 * 
 * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnection
 */
public interface NetworkConnectionFactory
{
    /**
     * Creates a NetworkConnection interface implementation from an endpoint.
     * 
     * @see com.ibm.ws.sib.jfapchannel.XMLEndPoint
     * 
     * @param endpoint the endpoint to create a network connection from. This should be an instance
     *            of XMLEndPoint.
     * 
     * @return Returns a network connection which, at the point it is returned, is not connected to
     *         the remote peer.
     * 
     * @throws FrameworkException if the connection cannot be created.
     */
    NetworkConnection createConnection(Object endpoint) throws FrameworkException;

    /**
     * @return an unconnected network connection.
     * 
     * @throws FrameworkException if the connection cannot be created.
     */
    NetworkConnection createConnection() throws FrameworkException;

    /**
     * 
     * @return an VirtualConnectionFactory
     */
    VirtualConnectionFactory getOutboundVirtualConFactory();
}
