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
package com.ibm.ws.ejbcontainer.osgi.internal.ejbextdd;

import java.util.concurrent.atomic.AtomicInteger;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.ibm.ws.javaee.dd.ejbext.EnterpriseBean;

abstract class EnterpriseBeanMockery<T extends EnterpriseBeanMockery<T>> {
    private static final AtomicInteger numBeans = new AtomicInteger();

    final Mockery mockery;
    private final String name;

    EnterpriseBeanMockery(Mockery mockery, String name) {
        this.mockery = mockery;
        this.name = name;
    }

    protected <B extends EnterpriseBean> B mockEnterpriseBean(final Class<B> klass) {
        final B bean = mockery.mock(klass, "enterpriseBeanExt-" + numBeans.incrementAndGet());
        mockery.checking(new Expectations() {
            {
                allowing(bean).getName();
                will(returnValue(name));
            }
        });
        return bean;
    }
}
