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
 *                                 Version 1.14 copied from CMVC
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms.impl;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.TopicConnection;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.api.jms.ApiJmsConstants;
import com.ibm.websphere.sib.api.jms.JmsTopicConnectionFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaConnection;
import com.ibm.ws.sib.api.jmsra.JmsJcaConnectionFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedConnectionFactory;
import com.ibm.ws.sib.utils.ras.SibTr;

public class JmsTopicConnectionFactoryImpl extends JmsConnectionFactoryImpl implements JmsTopicConnectionFactory
{
  private static final long serialVersionUID = -1655456826407056319L;

  // ************************** TRACE INITIALISATION ***************************

  private static TraceComponent tc = SibTr.register(JmsTopicConnectionFactoryImpl.class, ApiJmsConstants.MSG_GROUP_EXT, ApiJmsConstants.MSG_BUNDLE_EXT);

  // ***************************** CONSTRUCTORS ********************************

  /**
   * Constructor that stores references to the associated jca connection
   * factory and the associated jca managed connection factory by delegating
   * to the superclass constructor.
   */
  JmsTopicConnectionFactoryImpl(JmsJcaConnectionFactory jcaConnectionFactory, JmsJcaManagedConnectionFactory jcaManagedConnectionFactory) {
    super(jcaConnectionFactory, jcaManagedConnectionFactory);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "JmsTopicConnectionFactoryImpl", new Object[]{jcaConnectionFactory, jcaManagedConnectionFactory});
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "JmsTopicConnectionFactoryImpl");
  }


  // *************************** INTERFACE METHODS *****************************

  /**
   * @see javax.jms.TopicConnectionFactory#createTopicConnection()
   */
  public TopicConnection createTopicConnection() throws JMSException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "createTopicConnection");
    TopicConnection topicConnection = createTopicConnection(null, null);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "createTopicConnection",  topicConnection);
    return topicConnection;
  }

  /**
   * @see javax.jms.TopicConnectionFactory#createTopicConnection(String, String)
   */
  public TopicConnection createTopicConnection(String userName, String password) throws JMSException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "createTopicConnection", new Object[]{userName, (password == null ? "<null>" : "<non-null>")});
    // createConnection() will call this.instantiateConnection()
    TopicConnection topicConnection = (TopicConnection) createConnection(userName, password);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "createTopicConnection",  topicConnection);
    return topicConnection;
  }

  // ************************* IMPLEMENTATION METHODS **************************

  /**
   * This overrides a superclass method, so that the superclass's
   * createConnection() method can be inherited, but still return an object of
   * this class's type.
   */
  JmsConnectionImpl instantiateConnection(JmsJcaConnection jcaConnection, Map _passThruProps) throws JMSException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "instantiateConnection", jcaConnection);
    JmsTopicConnectionImpl jmsTopicConnection = new JmsTopicConnectionImpl(jcaConnection, isManaged(), _passThruProps);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "instantiateConnection",  jmsTopicConnection);
    return jmsTopicConnection;
  }
}
