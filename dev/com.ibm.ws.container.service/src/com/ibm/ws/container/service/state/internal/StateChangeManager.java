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
package com.ibm.ws.container.service.state.internal;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceSet;

/**
 * The manager of state changes for a type of deployed info.
 * 
 * @param <L> the deployed info listener type
 */
abstract class StateChangeManager<L> {
    /**
     * The listeners for this deployed info type.
     */
    protected final ConcurrentServiceReferenceSet<L> listeners;

    StateChangeManager(String listenerRefName) {
        listeners = new ConcurrentServiceReferenceSet<L>(listenerRefName);
    }

    void activate(ComponentContext cc) {
        listeners.activate(cc);
    }

    void deactivate(ComponentContext cc) {
        listeners.deactivate(cc);
    }

    final void addListener(ServiceReference<L> ref) {
        listeners.addReference(ref);
    }

    final void removeListener(ServiceReference<L> ref) {
        listeners.removeReference(ref);
    }
}
