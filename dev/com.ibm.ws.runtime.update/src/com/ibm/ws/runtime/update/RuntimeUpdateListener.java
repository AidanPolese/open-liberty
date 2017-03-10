/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.runtime.update;

/**
 * Service interface for components interested in being informed of runtime update
 * notifications as they are created.
 */
public interface RuntimeUpdateListener {
    /**
     * Called as notifications are created on the set of all listeners known to
     * the runtime update manager at the time the notification is created.
     * 
     * @param updateManager the runtime update manager
     * @param notification the newly created notification
     */
    public void notificationCreated(RuntimeUpdateManager updateManager, RuntimeUpdateNotification notification);
}
