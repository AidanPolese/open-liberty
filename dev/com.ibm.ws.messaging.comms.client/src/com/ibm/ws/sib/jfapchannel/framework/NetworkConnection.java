/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/NetworkConnection.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:26 [4/12/12 22:14:18]
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

/**
 * Represents a network connection which may be used to send and
 * receive data with a peer.  Network connections may be created using
 * an implementation of the NetworkConnectionFactory interface.
 * 
 * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionFactory
 */
public interface NetworkConnection
{
   /**
    * Request permission to close this network connection. 
    * @param timeout amount of time to wait for processing to complete such
    * that it is safe to close the network connection.
    * @return true if it is now safe to close the network connection.  False
    * otherwise.
    */
   boolean requestPermissionToClose(long timeout);
   
   /**
    * Asynchronously establish a network connection to the specified target.
    * @param target information about the remote network endpoint with which the
    * network connection should be established.
    * @param listener a listener implementation used to report successful (or
    * otherwise) establishement of a network connection.
    */
   void connectAsynch(NetworkConnectionTarget target,
                      ConnectRequestListener listener);
   
   /**
    * @return a network connection context which can be used to send or
    * receive data over a network connection.  A value of null will be returned
    * if the network connection is not connected, or was not successfully
    * connected.
    */
   NetworkConnectionContext getNetworkConnectionContext();
}
