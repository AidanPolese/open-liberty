/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package javax.management.j2ee;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

/**
 * Provides the methods to add and remove event listeners
 */
public interface ListenerRegistration extends Serializable {

    /*
     * Add a listener to a registered managed object.
     * Throws:
     * javax.management.InstanceNotFoundException
     * java.rmi.RemoteException
     * Parameters:
     * name - The name of the managed object on which the listener should be
     * added.
     * listener - The listener object which will handle the events emitted
     * by the registered managed object.
     * filter - The filter object. If filter is null, no filtering will be
     * performed before handling events.
     * handback - An opaque object to be sent back to the listener when a
     * notification is emitted which helps the listener to associate information
     * regarding the MBean emitter. This object cannot be used by the
     * Notification broadcaster object. It should be resent unchanged with the
     * notification to the listener.
     */
    public void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) throws RemoteException, InstanceNotFoundException;

    /*
     * Enables to remove a listener from a registered managed object.
     * Throws:
     * javax.management.InstanceNotFoundException
     * javax.management.ListenerNotFoundException
     * java.rmi.RemoteException
     * Parameters:
     * name - The name of the managed object on which the listener should be
     * removed.
     * listener - The listener object which will handle the events emitted
     * by the registered managed object. This method will remove all the
     * information related to this listener.
     */
    public void removeNotificationListener(ObjectName name, NotificationListener listener) throws InstanceNotFoundException, ListenerNotFoundException, RemoteException;

}
