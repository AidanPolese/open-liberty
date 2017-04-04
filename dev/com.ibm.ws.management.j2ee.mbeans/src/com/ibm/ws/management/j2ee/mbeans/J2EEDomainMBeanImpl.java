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
package com.ibm.ws.management.j2ee.mbeans;

import java.util.Arrays;
import java.util.List;

import javax.management.ObjectName;
import javax.management.StandardMBean;

import com.ibm.websphere.management.j2ee.J2EEDomainMBean;
import com.ibm.websphere.management.j2ee.J2EEManagementObjectNameFactory;
import com.ibm.ws.management.j2ee.mbeans.internal.MBeanServerHelper;

/**
 * This MBean represents the J2EEDomain and is registered on the MBeanServer by SMActivator
 */
public class J2EEDomainMBeanImpl extends StandardMBean implements J2EEDomainMBean {
    private final ObjectName objectName;

    public J2EEDomainMBeanImpl() {
        super(J2EEDomainMBean.class, false);
        objectName = J2EEManagementObjectNameFactory.createJ2EEDomainObjectName();
    }

    /** {@inheritDoc} */
    @Override
    public String getobjectName() {
        return objectName.toString();
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

    /** {@inheritDoc} */
    @Override
    public String[] getservers() {
        List<String> servers = MBeanServerHelper.queryObjectName(
                        J2EEManagementObjectNameFactory.createJ2EEServerObjectName("*"));

        return servers.toArray(new String[servers.size()]);
    }

    @Override
    public String toString() {
        return "J2EEDomainMBeanImpl : objectName=" + objectName + " : servers=" + Arrays.toString(getservers());
    }
}