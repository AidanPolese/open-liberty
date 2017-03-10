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
package com.ibm.websphere.runtime.update;

/**
 * The RuntimeUpdateNotificationMBean provides notifications for server runtime updates.
 * The user data object attached to the notification is a java.util.Map.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * 
 * @ibm-api
 */
public interface RuntimeUpdateNotificationMBean {
	
    /**
     * A string representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    public String OBJECT_NAME = "WebSphere:name=com.ibm.websphere.runtime.update.RuntimeUpdateNotificationMBean";
    
    /**
     * Notification type for runtime update notifications emitted by this MBean.
     */
    public String RUNTIME_UPDATE_NOTIFICATION_TYPE = "com.ibm.websphere.runtime.update.notification";
    
    //
    // Notification user data keys
    //
    
    /**
     * User data key for the name of the notification. The value is
     * a java.lang.String.
     */
    public String RUNTIME_UPDATE_NOTIFICATION_KEY_NAME = "name";
    
    /**
     * User data key for the status of the notification. The value
     * is a java.lang.Boolean that indicates whether the event that
     * the notification was triggered for was successful.
     */
    public String RUNTIME_UPDATE_NOTIFICATION_KEY_STATUS = "status";
    
    /**
     * User data key for the error message of the notification. The value
     * is a java.lang.String. A non-null error message may be provided
     * when the value of status is <code>false</code>.
     */
    public String RUNTIME_UPDATE_NOTIFICATION_KEY_MESSAGE = "message";

}
