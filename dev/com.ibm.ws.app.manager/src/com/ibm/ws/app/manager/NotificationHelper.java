/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import com.ibm.websphere.application.ApplicationMBean;

public class NotificationHelper {

    private final static AtomicLong sequence = new AtomicLong();

    public static void broadcastChange(NotificationBroadcasterSupport mbeanSupport, String appName, String operation, Boolean result, String msg) {
        if (mbeanSupport != null) {
            //Make and send notification
            Notification notification = new Notification(operation, appName, sequence.incrementAndGet(), msg);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ApplicationMBean.STATE_CHANGE_NOTIFICATION_KEY_STATUS, result);
            notification.setUserData(map);
            mbeanSupport.sendNotification(notification);
        }
    }
}
