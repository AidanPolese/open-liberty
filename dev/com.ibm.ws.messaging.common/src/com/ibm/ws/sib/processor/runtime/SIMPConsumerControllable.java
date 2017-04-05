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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 186484.5         210404 ajw      Further Continued controllable interfaces
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.wsspi.sib.core.OrderingContext;
/**
 * 
 */
public interface SIMPConsumerControllable extends SIMPControllable
{
  /**
   * Locates the Connection relating to the Consumer. 
   *
   * @return SIMPConnectionControllable The Connection object. 
   *
   */
  SIMPConnectionControllable getConnection();
  
  /**
   * Locates the administration destination that the consumer is consuming from.  
   *
   * @return Object  A Queue, AttachedRemoteSubscription or LocalSubscription. 
   *
   */
  Object getDestinationObject();
  
  /**
   * Locates the ordering context for the consumer.  
   *
   * @return OrderingContext  An OrderingContext or null if there is none. 
   */
  OrderingContext getOrderingContext();

}
