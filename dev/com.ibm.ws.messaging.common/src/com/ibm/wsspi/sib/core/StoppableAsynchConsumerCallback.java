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
 * SIB0115.core.1   250407 ajw      Support Pausing/Resuming of MDB's
 * 569303           181208 dware    Fix javadoc for V7 common criteria
 * ===========================================================================
 */
package com.ibm.wsspi.sib.core;

 /**
  * StoppableAsynchConsumerCallback is an interface that can be implemented by the client 
  * application (or API layer), in order to receive messages asynchronously. It extends
  * the <code>com.ibm.wsspi.sib.core.AsynhConsumerCallback</code> and provides the ability
  * to be calledback when the consumer session associated with this consumer is stopped
  * due to the max sequential message failure threshold been reached. 
  * <p>
  * This class has no security implications.
  * 
  * @see com.ibm.wsspi.sib.core.AsynchConsumerCallback
  *
  * @see com.ibm.wsspi.sib.core.ConsumerSession#registerStoppableAsynchConsumerCallback
  * @see com.ibm.wsspi.sib.core.ConsumerSession#deregisterStoppableAsynchConsumerCallback
  */
public interface StoppableAsynchConsumerCallback extends AsynchConsumerCallback
{
  /**
   * Indicates that the consumer session has been stopped due to the
   * max sequential failures threshold been reached. 
   * 
   * Once this method has been called no messages will be sent to the asynch consumer
   * till the consumer session is started again. 
   *
   */
  public void consumerSessionStopped();
}
