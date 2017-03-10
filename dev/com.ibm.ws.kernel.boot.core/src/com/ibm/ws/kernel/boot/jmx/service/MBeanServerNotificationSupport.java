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
 * MBean notification support for a DelayedMBeanHelper.
 */
public interface MBeanServerNotificationSupport {

    public void sendRegisterNotification(ObjectName objectName);

    public void sendUnregisterNotification(ObjectName objectName);

}
