/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version 1.20 copied from CMVC
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.api.jms.ApiJmsConstants;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.ws.sib.utils.ras.SibTr;

public class JmsTopicPublisherImpl extends JmsMsgProducerImpl implements TopicPublisher
{
  // ************************** TRACE INITIALISATION ***************************

  private static TraceComponent tc = SibTr.register(JmsTopicPublisherImpl.class,  ApiJmsConstants.MSG_GROUP_EXT, ApiJmsConstants.MSG_BUNDLE_EXT);

  // ***************************** CONSTRUCTORS ********************************

  JmsTopicPublisherImpl(Destination theDest, SICoreConnection coreConnection, JmsSessionImpl newSession) throws JMSException {
    super(theDest, coreConnection, newSession);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "JmsTopicPublisherImpl", new Object[]{theDest, coreConnection, newSession});
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "JmsTopicPublisherImpl");
  }


  // *************************** INTERFACE METHODS *****************************

  /**
   * @see javax.jms.TopicPublisher#getTopic()
   */
  public Topic getTopic() throws JMSException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getTopic");
    checkClosed();
    Topic topic = (Topic) getDestination();
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getTopic",  (topic));
    return (topic);
  }

  /**
   * @see javax.jms.TopicPublisher#publish(Message)
   */
  public void publish(Message message) throws JMSException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "publish", message);
    send(message, getDeliveryMode(), getPriority(), getTimeToLive());
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "publish");
  }

  /**
   * @see javax.jms.TopicPublisher#publish(Message, int, int, long)
   */
  public void publish(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "publish", new Object[]{message, deliveryMode, priority, timeToLive});
    send(message, deliveryMode, priority, timeToLive);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "publish");
  }

  /**
   * @see javax.jms.TopicPublisher#publish(Topic, Message)
   */
  public void publish(Topic topic, Message message) throws JMSException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "publish", new Object[]{topic, message});
    send(topic, message, getDeliveryMode(), getPriority(), getTimeToLive());
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "publish");
  }

  /**
   * @see javax.jms.TopicPublisher#publish(Topic, Message, int, int, long)
   */
  public void publish(Topic topic, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "publish", new Object[]{topic, message, deliveryMode, priority, timeToLive});
    send(topic, message, getDeliveryMode(), getPriority(), getTimeToLive());
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "publish");
  }
}
