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
package com.ibm.ws.jmx.internal;

import java.util.ArrayList;
import java.util.List;

import javax.management.Notification;
import javax.management.NotificationListener;

/**
 *
 */
public class NotificationRecorder implements NotificationListener {

    public final Object handback = new Object();
    public final List<Notification> notifications = new ArrayList<Notification>();

    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (this.handback == handback) {
            notifications.add(notification);
        }
    }
}
