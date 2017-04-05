/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/NetworkConnectionTarget.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:31 [4/12/12 22:14:18]
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
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.framework;

import java.net.InetSocketAddress;

/**
 * Identifies a remote end point with which a network connection may
 * be established.  Users of this package should supply their implementation
 * of this interface to the connectAsynch method of a NetworkConnection
 * interface implementation in order to establish a network connection.
 * 
 * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnection
 */
public interface NetworkConnectionTarget
{
   /**
    * @return the local address to which the network connection should
    * be bound (or null if the system should assign an address).
    */
   InetSocketAddress getLocalAddress();
   
   /**
    * @return the remote address to which the network connection should
    * be established.
    */
   InetSocketAddress getRemoteAddress();
   
   /**
    * @return the amount of time (in milliseconds) to wait for the network 
    * connection to be established.
    */
   int getConnectTimeout();
}
