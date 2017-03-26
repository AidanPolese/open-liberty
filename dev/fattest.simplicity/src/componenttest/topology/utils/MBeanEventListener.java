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
package componenttest.topology.utils;

import java.util.concurrent.CountDownLatch;

import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;

/**
 *
 */
public class MBeanEventListener implements NotificationListener {

    /*
     * (non-Javadoc)
     * 
     * @see javax.management.NotificationListener#handleNotification(javax.management.Notification, java.lang.Object)
     */
    private boolean registered = false;

    public final CountDownLatch latchForListener = new CountDownLatch(1);

    /*
     * (non-Javadoc)
     * 
     * @see javax.management.NotificationListener#handleNotification(javax.management.Notification, java.lang.Object)
     */
    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (MBeanServerNotification.REGISTRATION_NOTIFICATION.equals(notification.getType())) {
            registered = true;
            latchForListener.countDown();
        }
    }

    public boolean isRegistered() {
        return registered;
    }

}
