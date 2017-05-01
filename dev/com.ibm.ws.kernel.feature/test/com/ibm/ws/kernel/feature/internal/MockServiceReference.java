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
package com.ibm.ws.kernel.feature.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 *
 */
public class MockServiceReference<T> implements ServiceReference<T> {
    T service;

    public MockServiceReference(T serv) {
        this.service = serv;
    }

    public T getService() {
        return this.service;
    }

    @Override
    public Object getProperty(String key) {
        return null;
    }

    @Override
    public String[] getPropertyKeys() {
        return null;
    }

    @Override
    public Bundle getBundle() {
        return null;
    }

    @Override
    public Bundle[] getUsingBundles() {
        return null;
    }

    @Override
    public boolean isAssignableTo(Bundle bundle, String className) {
        return false;
    }

    @Override
    public int compareTo(Object reference) {
        return 0;
    }
}