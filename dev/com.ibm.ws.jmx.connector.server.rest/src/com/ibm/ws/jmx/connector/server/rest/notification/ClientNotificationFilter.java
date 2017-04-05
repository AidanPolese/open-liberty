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
package com.ibm.ws.jmx.connector.server.rest.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.management.Notification;
import javax.management.NotificationFilter;

/**
 * This class acts as a filter for a single MBean and for a single clientID
 */
public class ClientNotificationFilter implements NotificationFilter {

    private static final long serialVersionUID = 1L;

    /**
     * List of filters we are wrapping
     */
    private final List<NotificationFilter> filters;

    /**
     * Construct
     */
    public ClientNotificationFilter() {
        //Not using a Collections.synchronizedList here because both methods that access the list need to have atomic
        //control of the list for more than 1 operation, one to update the filters (involves clearing all and then adding 
        //elements), and another is iterating over the list, which requires synchronization even for a Collections.synchronizedList
        filters = new ArrayList<NotificationFilter>();
    }

    /**
     * Get the current filters
     * 
     */
    public synchronized NotificationFilter[] getFilters() {
        return filters.toArray(new NotificationFilter[filters.size()]);
    }

    /**
     * Override the list of filters with given array.
     */
    public synchronized void updateFilters(NotificationFilter[] filtersArray) {
        filters.clear();
        if (filtersArray != null)
            Collections.addAll(filters, filtersArray);
    }

    /**
     * Handle notification filtering according to stored filters.
     */
    @Override
    public synchronized boolean isNotificationEnabled(Notification notification) {
        if (filters.isEmpty()) {
            //Allow all
            return true;
        }

        final Iterator<NotificationFilter> i = filters.iterator();
        while (i.hasNext()) {
            if (i.next().isNotificationEnabled(notification)) {
                return true;
            }
        }

        return false;
    }
}
