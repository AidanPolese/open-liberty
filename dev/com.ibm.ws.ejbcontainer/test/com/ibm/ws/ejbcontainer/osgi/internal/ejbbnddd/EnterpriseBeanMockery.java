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

import com.ibm.ws.javaee.dd.ejbbnd.EnterpriseBean;

abstract class EnterpriseBeanMockery<T extends EnterpriseBeanMockery<T>> {
    private static final AtomicInteger numBeans = new AtomicInteger();

    final Mockery mockery;
    private final String name;

    EnterpriseBeanMockery(Mockery mockery, String name) {
        this.mockery = mockery;
        this.name = name;
    }

    protected <B extends EnterpriseBean> B mockEnterpriseBean(final Class<B> klass) {
        final B bean = mockery.mock(klass, "enterpriseBeanBnd-" + numBeans.incrementAndGet());
        mockery.checking(new Expectations() {
            {
                allowing(bean).getName();
                will(returnValue(name));
            }
        });
        return bean;
    }
}
