/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.jmx.service;

import javax.management.ObjectName;

/**
 * Implemented by an MBeanServerForwarderDelegate capable of delayed MBean activation.
 */
public interface DelayedMBeanHelper {

    public boolean isDelayedMBean(ObjectName name);

    public void setMBeanServerNotificationSupport(MBeanServerNotificationSupport support);

}
