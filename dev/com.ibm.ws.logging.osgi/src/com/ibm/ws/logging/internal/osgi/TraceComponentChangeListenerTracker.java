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
package com.ibm.ws.logging.internal.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.ibm.websphere.ras.TrConfigurator;
import com.ibm.websphere.ras.TraceComponentChangeListener;

public class TraceComponentChangeListenerTracker extends ServiceTracker<TraceComponentChangeListener, TraceComponentChangeListener> {
    public TraceComponentChangeListenerTracker(BundleContext context) {
        super(context, TraceComponentChangeListener.class.getName(), null);
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Throwable t) {
            // odds are very good that this will blow up due to an already dead
            // bundle context. We don't much care, as we're shutting down.
        }
    }

    /**
     *
     */
    @Override
    public TraceComponentChangeListener addingService(ServiceReference<TraceComponentChangeListener> reference) {
        TraceComponentChangeListener listener = super.addingService(reference);
        TrConfigurator.addTraceComponentListener(listener);
        return listener;
    }

    /**
     *
     */
    @Override
    public void modifiedService(ServiceReference<TraceComponentChangeListener> reference, TraceComponentChangeListener service) {
        // so far, nothing to do here. Modifications to the
        // TraceComponentChangeListener shouldn't impact whether or not they are
        // interested in trace component changes
    }

    /**
     *
     */
    @Override
    public void removedService(ServiceReference<TraceComponentChangeListener> reference, TraceComponentChangeListener service) {
        TrConfigurator.removeTraceComponentListener(service);
        super.removedService(reference, service);
    }
}