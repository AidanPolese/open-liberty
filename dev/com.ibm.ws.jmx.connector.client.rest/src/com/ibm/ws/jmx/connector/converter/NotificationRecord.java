/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.converter;

import javax.management.Notification;
import javax.management.ObjectName;

/**
 * Data structure containing a JMX Notification and information about which target it came from.
 */
public final class NotificationRecord {

    private final Notification n;
    private final NotificationTargetInformation nti;

    public NotificationRecord(Notification n, ObjectName name) {
        this.n = n;
        this.nti = new NotificationTargetInformation(name);
    }

    public NotificationRecord(Notification n, String name) {
        this.n = n;
        this.nti = new NotificationTargetInformation(name);
    }

    public NotificationRecord(Notification n, ObjectName name, String hostName, String serverName, String serverUserDir) {
        this.n = n;
        this.nti = new NotificationTargetInformation(name, hostName, serverName, serverUserDir);
    }

    public NotificationRecord(Notification n, String name, String hostName, String serverName, String serverUserDir) {
        this.n = n;
        this.nti = new NotificationTargetInformation(name, hostName, serverName, serverUserDir);
    }

    public Notification getNotification() {
        return n;
    }

    public NotificationTargetInformation getNotificationTargetInformation() {
        return nti;
    }
}
