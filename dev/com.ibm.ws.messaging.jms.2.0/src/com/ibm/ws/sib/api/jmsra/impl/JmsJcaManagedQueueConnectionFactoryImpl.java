/**
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
 * Reason          Date      Origin   Description
 * --------------  ------    -------- ------------------------------------------
 *                 16-May-03 djhoward Creation
 *                 13-Jun-03 pnickoll Updated with latest Rational Rose design 
 * 170067.3	   	   23-Jun-03 djhoward Refactor JMS package imports
 * 169626.1        26-Jun-03 djhoward First implementation of Outbound diagrams 1-5
 * 171054          03-Jul-03 djhoward Utilise JMS RA changes to JMSRAFactoryFactory
 * 169626.5        17-Jul-03 pnickoll First implementation for outbound
 *                                    diagrams 19-21
 * 169626.6        22-Jul-03 pnickoll Updates from code review
 * 174531          21-Aug-03 pnickoll Added getConnectionType
 * 182279          11-Nov-03 pnickoll Added serialVersionUID field
 * 188479.2        05-Feb-04 dcurrie  Remove unneccessary exeptions
 * 203656          17-May-04 dcurrie  Code cleanup
 * ============================================================================
 */
package com.ibm.ws.sib.api.jmsra.impl;

import javax.jms.ConnectionFactory;

import com.ibm.ws.sib.api.jms.JmsRAFactoryFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedConnectionFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedQueueConnectionFactory;

/**
 * Managed connection factory for point-to-point domain.
 */
public final class JmsJcaManagedQueueConnectionFactoryImpl extends
        JmsJcaManagedConnectionFactoryImpl implements
        JmsJcaManagedQueueConnectionFactory {

    static final String QUEUE_CONN_FACTORY_TYPE = "javax.jms.QueueConnectionFactory";

    private static final long serialVersionUID = 114300589728595741L;

    ConnectionFactory createJmsConnFactory(
            final JmsRAFactoryFactory jmsFactory,
            final JmsJcaConnectionFactoryImpl connectionFactory) {

        return jmsFactory.createQueueConnectionFactory(connectionFactory);

    }

    ConnectionFactory createJmsConnFactory(
            final JmsRAFactoryFactory jmsFactory,
            final JmsJcaConnectionFactoryImpl connectionFactory,
            final JmsJcaManagedConnectionFactory managedConnectionFactory) {

        return jmsFactory.createQueueConnectionFactory(connectionFactory,
                (JmsJcaManagedQueueConnectionFactory) managedConnectionFactory);

    }

    /**
     * Returns the connection type.
     * 
     * @return the connection type
     */
    String getConnectionType() {
        return QUEUE_CONN_FACTORY_TYPE;
    }

}
