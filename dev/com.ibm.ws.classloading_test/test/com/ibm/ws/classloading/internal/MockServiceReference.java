/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

public class MockServiceReference<S> implements ServiceReference<S> {

    private static final AtomicInteger rankingCounter = new AtomicInteger(0);
    private final int serviceRanking;
    final S s;

    private MockServiceReference(S s) {
        this.s = s;
        serviceRanking = rankingCounter.getAndIncrement();
    }

    static <S> MockServiceReference<S> wrap(S s) {
        return new MockServiceReference<S>(s);
    }

    S unwrap() {
        return s;
    }

    @Override
    public Object getProperty(String key) {
        if ("service.ranking".equals(key)) {
            return serviceRanking;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getPropertyKeys() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getBundle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle[] getUsingBundles() {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean isAssignableTo(Bundle bundle, String className) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Object reference) {
        throw new UnsupportedOperationException();
    }

}
