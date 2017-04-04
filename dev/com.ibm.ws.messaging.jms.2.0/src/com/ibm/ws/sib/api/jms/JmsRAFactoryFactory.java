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
 * 170807            270603 kingdon  Original
 * 170807.7          ??0803 amardeep Two arg methods added
 * 197921.1          040504 jhumber  New Javadoc tags
 * 225815            200804 matrober ibm-spi tags in internal files
 * ============================================================================
 */

package com.ibm.ws.sib.api.jms;

import javax.jms.*;

import com.ibm.ws.sib.api.jmsra.JmsJcaConnectionFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedConnectionFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedQueueConnectionFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedTopicConnectionFactory;

/**
 * This interface defines additional methods for creating factories that will
 * be used by the JCA resource adaptor. These methods are not intended for
 * public consumption, and hence are in the ws package rather than
 * com.ibm.websphere.
 * 
 * This class is specifically NOT tagged as ibm-spi because by definition it is not
 * intended for use by either customers or ISV's.
 */

public interface JmsRAFactoryFactory
{

  public ConnectionFactory createConnectionFactory(
   JmsJcaConnectionFactory jcaConnectionFactory);
   
  public ConnectionFactory createConnectionFactory(
   JmsJcaConnectionFactory jcaConnectionFactory,
   JmsJcaManagedConnectionFactory jcaManagedConnectionFactory);
   
  public TopicConnectionFactory createTopicConnectionFactory(
   JmsJcaConnectionFactory jcaConnectionFactory);
   
  public TopicConnectionFactory createTopicConnectionFactory(
   JmsJcaConnectionFactory jcaConnectionFactory,
   JmsJcaManagedTopicConnectionFactory jcaManagedTopicConnectionFactory);

  public QueueConnectionFactory createQueueConnectionFactory(
   JmsJcaConnectionFactory jcaConnectionFactory);

  public QueueConnectionFactory createQueueConnectionFactory(
   JmsJcaConnectionFactory jcaConnectionFactory,
   JmsJcaManagedQueueConnectionFactory jcaManagedQueueConnectionFactory);

}
