/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javamail.management.j2ee;

import javax.management.ObjectName;
import javax.management.StandardMBean;

import com.ibm.websphere.management.j2ee.JavaMailResourceMBean;

/**
 * This MBean represents JavaMailResource and is registered on the MBeanServer by JavaMailResourceRegistrar
 */
public class JavaMailResourceMBeanImpl extends StandardMBean implements JavaMailResourceMBean {

    private final ObjectName objectName;

    public JavaMailResourceMBeanImpl(ObjectName objectName) {
        super(JavaMailResourceMBean.class, false);
        this.objectName = objectName;
    }

    /** {@inheritDoc} */
    @Override
    public String getobjectName() {
        return objectName == null ? "" : objectName.toString();
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
