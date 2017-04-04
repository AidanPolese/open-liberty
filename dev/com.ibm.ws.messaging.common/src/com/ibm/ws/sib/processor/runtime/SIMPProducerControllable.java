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
 * 201691           050504 gatfora  More javadoc corrections.
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.wsspi.sib.core.OrderingContext;
/**
 * 
 */
public interface SIMPProducerControllable extends SIMPControllable
{
  /**
   * Locates the Connection relating to the Producer. 
   *
   * @return Connection  The Connection object. 
   *
   */
  SIMPConnectionControllable getConnection();

  /**
   * Locates the administration destination that the producer is producing to.  
   *
   * @return SIMPMessageHandlerControllable A ForeignBus, Queue or TopicSpace. 
   *
   */
  SIMPMessageHandlerControllable getMessageHandler();
  
  /**
   * Locates the ordering context for the producer.  
   *
   * @return OrderingContext  An OrderingContext or null if there is none. 
   *
   */
  OrderingContext getOrderingContext();
}
