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
package com.ibm.ws.classloading.internal.providers;

import static com.ibm.ws.classloading.internal.providers.AbstractLibraryListener.registerListener;

import java.lang.ref.WeakReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.ibm.ws.classloading.internal.util.RefQueue;
import com.ibm.wsspi.library.LibraryChangeListener;

/**
 * This listener receives notification of any changes to shared libraries, and
 * performs some appropriate update action (to be defined by subclasses).
 * 
 * It uses a weak reference and a reference queue to clean up any unowned
 * listeners.
 */
public abstract class WeakLibraryListener extends WeakReference<Object> implements LibraryChangeListener {

    private static final RefQueue<Object, WeakLibraryListener> QUEUE = new RefQueue<Object, WeakLibraryListener>();
    private volatile ServiceRegistration<LibraryChangeListener> listenerReg;

    protected WeakLibraryListener(String libraryId, String ownerId, Object owner, BundleContext ctx) {
        super(owner, QUEUE);
        // clean up any enqueued references
        removeStaleListeners();
        listenerReg = registerListener(this, libraryId, ownerId, ctx);
    }

    private static void removeStaleListeners() {
        for (WeakLibraryListener ref = QUEUE.poll(); ref != null; ref = QUEUE.poll())
            ref.deregister();
    }

    @Override
    public void libraryNotification() {
        removeStaleListeners();
        if (get() != null) {
            update();
        }
    }

    /**
     * This method is called by the library listener when an update occurs
     * This could be the arrival of the library into the service registry.
     * Or it could be the addition or modification of files within a Fileset
     */
    protected abstract void update();

    protected void deregister() {
        Object lock = listenerReg;
        if (lock == null)
            return;
        synchronized (lock) {
            if (listenerReg == null)
                return;
            listenerReg.unregister();
            listenerReg = null;
        }
    }
}
