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
 * Reason            Date   Origin   Description
 * ---------------   ------ -------- ------------------------------------------
 *                          matrober Original (Created on 17-Feb-03)
 * 170067            200603 matrober Refactor JMS interfaces (com.ibm.websphere)
 * 174896            220803 matrober JavaDoc public interfaces
 * 197921.1          040504 jhumber  New Javadoc tags 
 * ============================================================================
 */
package com.ibm.websphere.sib.api.jms;

import javax.jms.MessageConsumer;
import javax.jms.*;

/**
 * Contains provider specific methods relating to the javax.jms.MessageConsumer interface. 
 * 
 * @ibm-api
 * @ibm-was-base 
 */
public interface JmsMsgConsumer extends MessageConsumer
{
	
	/**
   * Get the Destination associated with this MessageConsumer.<p>
   * 
   * This method should be part of the standard JMS interfaces (to provide
   * symmetry with the MessageProducer, and also QueueReceiver/TopicSubscriber,
   * however it appears to have been omitted.
   * 
   * <!-- Javadoc'd: matrober 220803 -->
   * 
   * @return This consumer's Destination.
   * @throws JMSException If the JMS provider fails to get the destination for
   *                      this MessageProducer due to some internal error.
   */
  public Destination getDestination() throws JMSException;
  

}
