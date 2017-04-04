/*
 * @start_prolog@
 * Version: @(#) 1.9 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/ClientConnectionFactoryImpl.java, SIB.comms, WASX.SIB, uu1215.01 05/02/04 07:49:46 [4/12/12 22:15:08]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2004, 2005
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * Change activity:
 * 
 * CORE API 0.4b Implementation
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030507 niall    Original
 * F166959         030521 niall    Rebase on non-prototype CF + TCP Channel
 * d170527         030625 mattheg  Tidy and change to SibTr 
 * ============================================================================
 */

package com.ibm.ws.sib.comms.client;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.ClientConnection;
import com.ibm.ws.sib.comms.ClientConnectionFactory;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * @author Niall
 *
 * Used by TRM to return a ClientSideConnection. The ClientSideConnection is used by
 * TRM on the client to handshake with TRM on the server and is also the root object
 * of the Comms client code. So in effect, the Comms client side component is
 * bootstrapped by TRM by the createClientConnection method call.
 */
public class ClientConnectionFactoryImpl extends ClientConnectionFactory
{
   private static final TraceComponent tc =
      SibTr.register(
         ClientConnectionFactoryImpl.class,
         CommsConstants.MSG_GROUP,
         CommsConstants.MSG_BUNDLE);

   /**
    * Static constructor - creates an instance of the ConnectionFactory
    * class we will return from the "getInstance" method.
    * @see CommsFactory#getInstance()
    */
   static {
      if (tc.isDebugEnabled())
         SibTr.debug(
            tc,
            "Source info: @(#)SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/ClientConnectionFactoryImpl.java, SIB.comms, WASX.SIB, uu1215.01 1.9");
   }

   /**
    * Returns a ClientSideConnection that the TRM client side uses to 
    * handshake with the Server
    * @return ClientConnection
    */
   public ClientConnection createClientConnection()
   {
      return new ClientSideConnection();
   }

}
