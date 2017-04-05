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
package com.ibm.wsspi.application.handler;

import java.util.Collection;

import com.ibm.wsspi.adaptable.module.Notifier.Notification;

/**
 * An object implementing this interface will provide information about what elements in an application need monitoring.
 */
public interface ApplicationMonitoringInformation {

    /**
     * Returns a collection of notification objects that define which paths in which containers should be monitored for this application.
     * 
     * @return
     */
    public Collection<Notification> getNotificationsToMonitor();

    /**
     * Returns <code>true</code> if additions and deletions from the root element of this application should trigger the application to be updated. As an example, this can be
     * useful to listen to WAR files being added to the root of an EAR without having to recursively listen to the whole EAR.
     * 
     * @return
     */
    public boolean isListeningForRootStructuralChanges();

}
