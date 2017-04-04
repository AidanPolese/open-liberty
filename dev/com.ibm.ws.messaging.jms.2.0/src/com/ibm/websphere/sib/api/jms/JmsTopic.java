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
 * 176645.1.3        250903 kingdon  Add get/set for TopicSpace and TopicName
 * 197921.1          040504 jhumber  New Javadoc tags 
 * ============================================================================
 */
package com.ibm.websphere.sib.api.jms;

import javax.jms.*;

/**
 * Contains provider specific methods relating to the javax.jms.Topic interface.
 * 
 * @ibm-api
 * @ibm-was-base 
 */
public interface JmsTopic extends Topic, JmsDestination
{

  /**
   * Set the TopicSpace for this topic.
   * @param topicSpace
   * @throws JMSException if there is a problem setting this property.
   */
  void setTopicSpace(String topicSpace) throws JMSException;
  /**
   * Get the TopicSpace for this topic.
   * @return the TopicSpace.
   * @throws JMSException if there is a problem getting this property.
   */
  String getTopicSpace() throws JMSException;
  
  /**
   * Set the TopicName for this topic.
   * @param topicName
   * @throws JMSException if there is a problem setting this property.
   */
  void setTopicName(String topicName) throws JMSException;

  /**
   * Get the TopicName for this topic
   * @return the TopicName
   * @throws JMSException if there is a problem getting this property.
   */
  String getTopicName() throws JMSException;

}
