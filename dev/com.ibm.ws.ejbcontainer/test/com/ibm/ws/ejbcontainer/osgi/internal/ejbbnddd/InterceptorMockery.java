/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.ejbbnddd;

import java.util.concurrent.atomic.AtomicInteger;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.ibm.ws.javaee.dd.commonbnd.Interceptor;

public class InterceptorMockery {
    private static final AtomicInteger numInterceptors = new AtomicInteger();

    final Mockery mockery;
    private final String className;

    InterceptorMockery(Mockery mockery, String name) {
        this.mockery = mockery;
        this.className = name;
    }

    public Interceptor mock() {
        final Interceptor interceptor = mockery.mock(Interceptor.class, "interceptor-" + numInterceptors.incrementAndGet());
        mockery.checking(new Expectations() {
            {
                allowing(interceptor).getClassName();
                will(returnValue(className));
            }
        });
        return interceptor;
    }
}
