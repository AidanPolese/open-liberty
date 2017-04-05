/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.collective.member.connection;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/**
 * Provide the connection service to expose a MBean server connection to the collective controller that can be used
 * for MBean invocations.
 */
public interface CollectiveConnectionService {
    /**
     * The getMBeanServerConnection operation retrieves the
     * MBean server connection to the controller
     * 
     * @param listener a listener to receive connection status
     *            notifications.
     * @param filter a filter to select which notifications are to be
     *            delivered to the listener, or null if all notifications are to
     *            be delivered.
     * @param handback an object to be given to the listener along
     *            with each notification. Can be null.
     * 
     * @return MBeanServerConnection
     * @throws IOException If there was any problem completing the request
     */
    MBeanServerConnection getMBeanServerConnection(NotificationListener listener, NotificationFilter filter, Object handback) throws IOException;

}
