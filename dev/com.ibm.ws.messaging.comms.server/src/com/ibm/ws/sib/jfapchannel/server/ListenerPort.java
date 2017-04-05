/*
 * @start_prolog@
 * Version: @(#) 1.8 SIB/ws/code/sib.jfapchannel.server/src/com/ibm/ws/sib/jfapchannel/ListenerPort.java, SIB.comms, WASX.SIB, aa1225.01 05/02/04 09:55:43 [7/2/12 05:59:06]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2003, 2005
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
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel 
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel.server;

import com.ibm.ws.sib.jfapchannel.AcceptListener;

/**
 * An abstraction of a port currently being listen upon for new connections.
 * Implementations can only be obtained by invoking the "listen" method of
 * the conneciton manager.
 * @author prestona
 */
public interface ListenerPort
{
   /**
    * Stop listening for new connections on this port.
    */
   void close();
   
   /**
    * Retrieve the accept listener previously associated with this port.
    * @return AcceptListener The Accept Listener.
    */
   AcceptListener getAcceptListener();
   
   /**
    * Retrieve the port number.
    * @return int Port number.
    */
   int getPortNumber();
}
