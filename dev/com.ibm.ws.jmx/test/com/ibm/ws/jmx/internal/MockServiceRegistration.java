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

import java.util.Dictionary;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Implement just enough of the ServiceRegistration interface to support the unit tests.
 */
public class MockServiceRegistration<S> implements ServiceRegistration<S> {

    @Override
    public ServiceReference<S> getReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperties(Dictionary<String, ?> properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregister() {}

}
