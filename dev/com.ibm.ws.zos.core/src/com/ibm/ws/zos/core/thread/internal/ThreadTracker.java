/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.core.thread.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.ibm.ws.kernel.zos.NativeMethodManager;
import com.ibm.ws.zos.core.thread.ThreadLifecycleEventListener;

/**
 * Component that registered with JVMTI to handle thread lifecycle
 * events.
 */
public class ThreadTracker implements BundleActivator, ThreadLifecycleEventListener {

    /**
     * Reference to the native method manager that we'll use to drive
     * native registration functions
     */
    final NativeMethodManager nativeMethodManager;

    /**
     * Our host bundle's context.
     */
    BundleContext bundleContext;

    /**
     * Service tracker for {@link ThreadLifecycleEventListener}s.
     */
    ServiceTracker<ThreadLifecycleEventListener, ThreadLifecycleEventListener> listenerTracker;

    /**
     * Constructor.
     * 
     * @param nativeMethodManager the native method manager we'll use to
     *            drive native registration functions
     */
    public ThreadTracker(NativeMethodManager nativeMethodManager) {
        this.nativeMethodManager = nativeMethodManager;
    }

    /**
     * Bundle activation callback.
     * 
     * @param bundleContext the host bundle's context
     */
    @Override
    public void start(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        nativeMethodManager.registerNatives(ThreadTracker.class, new Object[] { this });

        // Setup a service tracker
        listenerTracker = new ServiceTracker<ThreadLifecycleEventListener, ThreadLifecycleEventListener>(bundleContext, ThreadLifecycleEventListener.class, null);
        listenerTracker.open();
    }

    /**
     * Bundle deactivation callback.
     * 
     * @param bundleContext the host bundle's context
     */
    @Override
    public void stop(BundleContext bundleContext) {
        listenerTracker.close();
        listenerTracker = null;

        this.bundleContext = null;
    }

    /**
     * Distribute the <em>threadStarted</em> event to registered listeners.
     */
    @Override
    public void threadStarted() {
        for (ThreadLifecycleEventListener listener : listenerTracker.getTracked().values()) {
            try {
                listener.threadStarted();
            } catch (Throwable t) {
            }
        }
    }

    /**
     * Distribute the <em>threadTerminated</em> event to registered listeners.
     */
    @Override
    public void threadTerminating() {
        for (ThreadLifecycleEventListener listener : listenerTracker.getTracked().values()) {
            try {
                listener.threadTerminating();
            } catch (Throwable t) {
            }
        }
    }
}
