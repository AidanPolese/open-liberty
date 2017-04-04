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
 * --------------- ------    -------- --------------------------------------------
 * 173041          30-Aug-03 dcurrie  Original
 * 203656          17-May-04 dcurrie  Code cleanup
 * ============================================================================
 */

package com.ibm.ws.sib.api.jmsra.impl;

import com.ibm.ws.sib.api.jmsra.JmsJcaFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedConnectionFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedQueueConnectionFactory;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedTopicConnectionFactory;

/**
 * Implementation of the abstract JmsJcaFactory class used for creating
 * instances of the managed connection factories. Used by the JMS API layer in a
 * non-JCA environment. If the managed connection factories are made part of the
 * public API then this factory will no longer be required.
 *  
 */
public final class JmsJcaFactoryImpl extends JmsJcaFactory {

    /*
     * @see com.ibm.ws.sib.api.jmsra.JmsJcaFactory#createManagedConnectionFactory()
     */
    public JmsJcaManagedConnectionFactory createManagedConnectionFactory() {
        return new JmsJcaManagedConnectionFactoryImpl();
    }

    /*
     * @see com.ibm.ws.sib.api.jmsra.JmsJcaFactory#createManagedQueueConnectionFactory()
     */
    public JmsJcaManagedQueueConnectionFactory createManagedQueueConnectionFactory() {
        return new JmsJcaManagedQueueConnectionFactoryImpl();
    }

    /*
     * @see com.ibm.ws.sib.api.jmsra.JmsJcaFactory#createManagedTopicConnectionFactory()
     */
    public JmsJcaManagedTopicConnectionFactory createManagedTopicConnectionFactory() {
        return new JmsJcaManagedTopicConnectionFactoryImpl();
    }

}
