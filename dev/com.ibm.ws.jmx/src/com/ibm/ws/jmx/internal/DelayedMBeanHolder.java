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
package com.ibm.ws.jmx.internal;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.ServiceReference;

/**
 *
 */
final class DelayedMBeanHolder {

    /**
     * Current registration state of this DelayedMBeanHolder
     */
    public final AtomicReference<DelayedMBeanRegistrationState> registrationState =
                    new AtomicReference<DelayedMBeanRegistrationState>(DelayedMBeanRegistrationState.DELAYED);

    /**
     * Latch for threads to wait on while this DelayedMBeanHolder is in PROCESSING state.
     */
    public final CountDownLatch processingCompleteSignal = new CountDownLatch(1);

    private final ServiceReference<?> ref;

    public DelayedMBeanHolder(ServiceReference<?> ref) {
        this.ref = ref;
    }

    /**
     * @return the ref
     */
    public ServiceReference<?> getRef() {
        return ref;
    }

}
