/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.logging.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.ibm.ws.kernel.zos.NativeMethodManager;

/**
 * Keeps an eye out for the NativeMethodManager and forwards registrations/
 * deregistrations to ZosLoggingBundleActivator.
 */
public class NativeMethodManagerServiceTracker {

    /**
     * When the NativeMethodManager becomes available it is injected into this guy.
     */
    private final ZosLoggingBundleActivator zosLoggingBundleActivator;

    /**
     * ServiceTracker for NativeMethodManager.
     */
    private volatile ServiceTracker<NativeMethodManager, NativeMethodManager> serviceTracker;

    /**
     * CTOR.
     */
    public NativeMethodManagerServiceTracker(ZosLoggingBundleActivator zosLoggingBundleActivator) {
        this.zosLoggingBundleActivator = zosLoggingBundleActivator;
    }

    /**
     * Open the ServiceTracker.
     * 
     * @return this
     */
    public synchronized NativeMethodManagerServiceTracker open(final BundleContext bundleContext) {

        ServiceTrackerCustomizer<NativeMethodManager, NativeMethodManager> stc = new ServiceTrackerCustomizer<NativeMethodManager, NativeMethodManager>() {

            @Override
            public void modifiedService(ServiceReference<NativeMethodManager> reference, NativeMethodManager service) {}

            @Override
            public void removedService(ServiceReference<NativeMethodManager> reference, NativeMethodManager service) {
                zosLoggingBundleActivator.unsetNativeMethodManager(service);
            }

            @Override
            public NativeMethodManager addingService(ServiceReference<NativeMethodManager> reference) {
                NativeMethodManager retMe = bundleContext.getService(reference);
                zosLoggingBundleActivator.setNativeMethodManager(retMe);
                return retMe;
            }
        };

        serviceTracker = new ServiceTracker<NativeMethodManager, NativeMethodManager>(bundleContext,
                        NativeMethodManager.class,
                        stc);
        serviceTracker.open();

        return this;
    }

    /**
     * Close the ServiceTracker.
     */
    public synchronized void close() {
        if (serviceTracker != null) {
            serviceTracker.close();
            serviceTracker = null;
        }
    }

}