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
 *                                 Version 1.24 copied from CMVC
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms.impl;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueReceiver;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.api.jms.*;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.ws.sib.utils.ras.SibTr;

public class JmsQueueReceiverImpl extends JmsMsgConsumerImpl implements QueueReceiver
{
  // ************************** TRACE INITIALISATION ***************************

  private static TraceComponent tc = SibTr.register(JmsQueueReceiverImpl.class, ApiJmsConstants.MSG_GROUP_EXT, ApiJmsConstants.MSG_BUNDLE_EXT);


  // ***************************** CONSTRUCTORS ********************************

  JmsQueueReceiverImpl(SICoreConnection coreConnection, JmsSessionImpl newSession, ConsumerProperties newProps) throws JMSException {
    // Set noLocal to false for PtP
    super(coreConnection, newSession, newProps);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "JmsQueueReceiverImpl", new Object[]{coreConnection, newSession});
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "dest: "+newProps.getJmsDestination()+", selector: " + newProps.getSelector());
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "JmsQueueReceiverImpl");
  }

  // *************************** INTERFACE METHODS *****************************

  /**
   * @see javax.jms.QueueReceiver#getQueue()
   */
  public Queue getQueue() throws JMSException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "getQueue");
    checkClosed();
    Queue queue = (Queue)getDestination();
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "getQueue",  queue);
    return queue;
  }
}
