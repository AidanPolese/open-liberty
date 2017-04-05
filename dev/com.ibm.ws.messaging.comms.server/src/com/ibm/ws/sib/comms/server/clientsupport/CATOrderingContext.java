/*
 * @start_prolog@
 * Version: @(#) 1.6 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/CATOrderingContext.java, SIB.comms, WASX.SIB, aa1225.01 05/02/04 07:57:00 [7/2/12 05:58:59]
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
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        040506 mattheg  Original
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.wsspi.sib.core.OrderingContext;

/**
 * This class encapsulates an ordering context. At first glance it may seem a pointless 
 * encapsulation - but we need to dispatch in the JFap channel by message order context and so this
 * class needs to extend the CATCommonDispatchable to allow the JFap to do this.
 * 
 * @author Gareth Matthews
 */
public class CATOrderingContext extends CATCommonDispatchable
{
   /** The ordering context */
   private OrderingContext orderContet = null;
   
   /**
    * Constructor.
    * 
    * @param orderContext
    */
   public CATOrderingContext(OrderingContext orderContext)
   {
      this.orderContet = orderContext;
   }
   
   /**
    * @return Returns the ordering context.
    */
   public OrderingContext getOrderingContext()
   {
      return orderContet;
   }
}
