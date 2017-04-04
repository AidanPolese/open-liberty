/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/NetworkTransportFactory.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:32 [4/12/12 22:14:18]
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
 * A factory which returns instances of NetworkConnectionFactory.  The instance of
 * NetworkConnectionFactory returned is based on the transport requested (which is
 * another way of saying - which protocol, TCP, SSL etc. you want).
 */
public interface NetworkTransportFactory
{
   /**
    * @param chainName a chain name used to select the transport.
    * @return a network connection factory for the specified transport, or null
    * if the transport it not supported.
    * 
    * @throws FrameworkException if an error occurs getting the connection factory
    */
   NetworkConnectionFactory getOutboundNetworkConnectionFactoryByName(String chainName) throws FrameworkException;

   /**
    * @param endPoint an end point used to select the transport (this must be
    * an instance of XMLEndPoint).
    * @return a network connection factory for the specified transport, or null
    * if the transport it not supported.
    */
   NetworkConnectionFactory getOutboundNetworkConnectionFactoryFromEndPoint(Object endPoint);
}
