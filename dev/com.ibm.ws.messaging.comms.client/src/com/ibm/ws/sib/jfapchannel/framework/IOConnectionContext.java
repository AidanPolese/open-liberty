/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/IOConnectionContext.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:20 [4/12/12 22:14:18]
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

import java.net.InetAddress;

/**
 * Provides contextual information about a network connection.  Instances of this
 * class may be obtained by invoking the getNetworkConnectionContext method of
 * implementations of the the NetworkConnection interface.
 *  
 * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnection
 */
public interface IOConnectionContext
{
   /**
    * @return the address of the local network adapter to which this connection is bound.
    */
   InetAddress getLocalAddress();
   
   /**
    * @return the address of the remote network adapter to which this connection is bound.
    */
   InetAddress getRemoteAddress();
   
   /**
    * @return the local port number that this network connection is using.
    */
   int getLocalPort();
   
   /**
    * @return the remote port number that this network connection is using.
    */
   int getRemotePort();
   
   /**
    * @return a read context that may be used for reading data from this network connection.
    */
   IOReadRequestContext getReadInterface();
   
   /**
    * @return a write context that may be used for writing data to this network connection.
    */   
   IOWriteRequestContext getWriteInterface();
}
