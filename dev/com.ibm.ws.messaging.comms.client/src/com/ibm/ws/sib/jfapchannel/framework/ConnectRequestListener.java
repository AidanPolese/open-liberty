/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/ConnectRequestListener.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:13 [4/12/12 22:14:17]
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
 * A listener for events pertaining to the establishment of a network connection.
 * Typically, a user of this package will obtain a NetworkConnection implementation
 * and invoke the connectAsynch method supplying an implementation of this class.
 * The implementation of this class will then be used to provide notification
 * as to the successful (or otherwise) establishment of the network connection.
 * @see com.ibm.ws.sib.jfapchannel.framework.NetworkConnection
 */
public interface ConnectRequestListener
{
   /**
    * Invoked to provide notification that a connect request has completed
    * succesfully.
    * @param networkConnection the network connection that has sucesfully been
    * established.
    */
   void connectRequestSucceededNotification(NetworkConnection networkConnection);
   
   /**
    * Invoked to provide notification that a connect request has failed to
    * be sucessfully completeted.
    * @param exception an exception with information about the condition which
    * caused the network connect request to fail.
    */
   void connectRequestFailedNotification(Exception exception);
}
