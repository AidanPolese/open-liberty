/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.messaging.jms.j2ee.mbeans;

import javax.management.ObjectName;
import javax.management.StandardMBean;

import com.ibm.websphere.management.j2ee.JMSResourceMBean;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * This is JMS Queue implementation for MBeans for JSR 77 spec. As of now only
 * getObjectName() is implemented. Other methods are dummy i.e return false.
 */
public class JmsServiceProviderMBeanImpl extends StandardMBean implements JMSResourceMBean {
    // because we use a pack-info.java for trace options our group and message file is already there
    // We just need to register the class here
    private static final TraceComponent tc = Tr.register(JmsServiceProviderMBeanImpl.class);

    private final ObjectName objectName;

    public JmsServiceProviderMBeanImpl(String serverName, String jmsResourceName) {
        //in future improved interface which extends JMSResourceMBean can be used
        super(JMSResourceMBean.class, false);

        //construct ObjectName with JmsMBeanHelper
        objectName = JmsMBeanHelper.getMBeanObject(serverName, jmsResourceName);

    }

    /** {@inheritDoc} */
    @Override
    public String getobjectName() {
        return objectName.getCanonicalName();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isstateManageable() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isstatisticsProvider() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean iseventProvider() {
        return false;
    }
}
