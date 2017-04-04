/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030426 prestona Original
 * f166313         030514 Niall    Modify getReference() to return an actual instance
 * d170527         030625 mattheg  Tidy and change to SibTr
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D372319         061206 mleming  Remove synchronization modifier
 * D379781         070130 mattheg  Fix compile warnings
 * ============================================================================
 */
package com.ibm.ws.sib.comms;


import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Factory class for ClientConnection objects.  Intended for use in the
 * client code by TRM.
 */
public abstract class ClientConnectionFactory
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = ClientConnectionFactory.class.getName();

   private static final TraceComponent tc =
      SibTr.register(
         ClientConnectionFactory.class,
         CommsConstants.MSG_GROUP,
         CommsConstants.MSG_BUNDLE);


   /**
    * TODO: comment
    * 
    * Creates an instance of a class which implements the ClientConnection
    * interface.
    * @return ClientConnection
    */
   public abstract ClientConnection createClientConnection();

}
