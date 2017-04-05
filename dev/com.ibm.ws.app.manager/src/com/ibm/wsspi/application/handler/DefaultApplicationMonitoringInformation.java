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
 * Default implementation of {@link ApplicationMonitoringInformation}
 */
public class DefaultApplicationMonitoringInformation implements ApplicationMonitoringInformation {

    private final Collection<Notification> notificationsToMonitor;
    private final boolean listeningForRootStructuralChanges;

    /**
     * @param notificationsToMonitor
     * @param listenForRootStructuralChanges
     */
    public DefaultApplicationMonitoringInformation(Collection<Notification> notificationsToMonitor, boolean listeningForRootStructuralChanges) {
        this.notificationsToMonitor = notificationsToMonitor;
        this.listeningForRootStructuralChanges = listeningForRootStructuralChanges;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Notification> getNotificationsToMonitor() {
        return notificationsToMonitor;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isListeningForRootStructuralChanges() {
        return listeningForRootStructuralChanges;
    }

}
