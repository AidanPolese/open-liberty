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
package com.ibm.ws.cache;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/**
 *
 */
public class MockAtomicServiceReference<T> extends AtomicServiceReference<T> {

    public MockAtomicServiceReference(String name, MockServiceReference<T> boundService) {
        super(name);

        this.boundService = boundService;
    }

    //

    private final MockServiceReference<T> boundService;

    @Override
    public ServiceReference<T> getReference() {
        return boundService;
    }

    @Override
    public T getService() {
        return ((MockServiceReference<T>) getReference()).getBoundService();
    }

    @Override
    public T getServiceWithException() {
        return ((MockServiceReference<T>) getReference()).getBoundService();
    }

    @Override
    public void activate(ComponentContext context) {
    // NO-OP
    }

    @Override
    public void deactivate(ComponentContext context) {
    // NO-OP
    }

    //

    @Override
    public boolean setReference(ServiceReference<T> reference) {
        // NO-OP
        return true;
    }

    @Override
    public boolean unsetReference(ServiceReference<T> reference) {
        // NO-OP
        return true;
    }
}
