/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/NetworkConnectionContext.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:28 [4/12/12 22:14:18]
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

import com.ibm.ws.sib.jfapchannel.ConversationMetaData;

/**
 * Contextual information about a network connection.  Implementations of
 * this interface may be obtained by invoking the getNetworkConnectionContext
 * method of a NetworkConnection interface implementation.
 * 
 * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnection
 */
public interface NetworkConnectionContext
{
   /**
    * Close the network connection.  This drops the link between this
    * process and its peer.  The user should ensure that a successful
    * call to NetworkConnection.requestPermissionToClose has been made
    * (and no further read or write requests have been made) prior to 
    * invoking this method.
    * @param networkConnection the network connection to close.
    * @param throwable an (optional) throwable which is used to indicate
    * the reason for closing the network connection.
    */
   void close(NetworkConnection networkConnection, 
              Throwable throwable);

   /**
    * @return an IO context used for reading and writing data to this
    * network connection.
    */
   IOConnectionContext getIOContextForDevice();
   
   /**
    * @return meta data about the connection.
    */
   ConversationMetaData getMetaData();
}
