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
package com.ibm.ws.jmx.connector.client.rest.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.NotificationFilter;
import javax.management.ObjectName;

import com.ibm.ws.jmx.connector.converter.NotificationTargetInformation;

class ServerNotificationListenerEntry {
    public final NotificationTargetInformation nti;
    public final ObjectName listener;
    public final NotificationFilter filter;
    public final Object handback;

    private static final Logger logger = Logger.getLogger(ServerNotificationListenerEntry.class.getName());

    /**
     * @param listener
     * @param filter
     * @param handback
     */
    ServerNotificationListenerEntry(NotificationTargetInformation nti, ObjectName listener, NotificationFilter filter, Object handback) {
        this.nti = nti;
        this.listener = listener;
        this.filter = filter;
        this.handback = handback;

        if (logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, logger.getName(), "init", "targetInfo: " + nti + " | listener: " + listener + " | filter: " + filter + " | handback: " + handback);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServerNotificationListenerEntry))
            return false;
        ServerNotificationListenerEntry other = (ServerNotificationListenerEntry) o;
        return nti.equals(other.nti) && listener.equals(other.listener) && filter == other.filter && handback == other.handback;
    }

    @Override
    public int hashCode() {
        return nti.hashCode() + listener.hashCode()
               + (filter != null ? filter.hashCode() : 0)
               + (handback != null ? handback.hashCode() : 0);
    }

}
