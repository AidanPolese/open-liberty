/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx_test.mbeans;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

/**
 *
 */
public class StringHolder extends NotificationBroadcasterSupport implements StringHolderMBean {

    private String value;
    private long sequenceNumber = 1;

    @Override
    public synchronized String getValue() {
        return value;
    }

    @Override
    public synchronized void setValue(String value) {
        String oldValue = this.value;
        this.value = value;
        Notification n =
                        new AttributeChangeNotification(this,
                                            sequenceNumber++,
                                            System.currentTimeMillis(),
                                            "Value changed",
                                            "Value",
                                            "String",
                                            oldValue,
                                            this.value);

        sendNotification(n);
    }

    @Override
    public synchronized void print() {
        System.out.println(value);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[] {
                        AttributeChangeNotification.ATTRIBUTE_CHANGE
        };
        String name = AttributeChangeNotification.class.getName();
        String description = "An attribute of this MBean has changed";
        MBeanNotificationInfo info =
                        new MBeanNotificationInfo(types, name, description);
        return new MBeanNotificationInfo[] { info };
    }
}
