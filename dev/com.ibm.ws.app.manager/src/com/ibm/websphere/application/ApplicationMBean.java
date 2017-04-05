/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.application;

/**
 * Management interface for MBeans with names of the form "WebSphere:service=com.ibm.websphere.application.ApplicationMBean,name=*"
 * where * is the name of an application under the Liberty profile. One such MBean for each application in the system is available
 * from the Liberty profile platform MBean server. This interface can be used to request a proxy object via the {@link javax.management.JMX#newMBeanProxy} method.
 * 
 * @ibm-api
 */
public interface ApplicationMBean {

    /**
     * User data key for the status of the notification. The value
     * is a java.lang.Boolean that indicates whether the event that
     * the notification was triggered for was successful.
     */
    public final String STATE_CHANGE_NOTIFICATION_KEY_STATUS = "status";

    /**
     * Retrieves the value of the read-only attribute State, which represents the current state of the application.
     * The value will be one of the following strings: STOPPED, STARTING, STARTED, PARTIALY_STARTED, STOPPING, INSTALLED
     * 
     * @return application state
     */
    public String getState();

    /**
     * Retrieves the value of the read-only attribute Pid (service persistent identifier).
     * 
     * @return application pid
     */
    public String getPid();

    /**
     * Invokes the start operation, requesting that the Liberty profile start the application.
     */
    public void start();

    /**
     * Invokes the stop operation, requesting that the Liberty profile stop the application.
     */
    public void stop();

    /**
     * Invokes the restart operation, requesting that the Liberty profile restart the application.
     */
    public void restart();
}
