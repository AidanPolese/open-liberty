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
package com.ibm.ws.ejbcontainer.osgi.internal.ejbdd;

import java.util.concurrent.atomic.AtomicInteger;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.ibm.ws.javaee.dd.ejb.EnterpriseBean;

abstract class EnterpriseBeanMockery<T extends EnterpriseBeanMockery<T>> {
    private static final AtomicInteger numBeans = new AtomicInteger();

    final Mockery mockery;
    private final String name;
    private final int kind;
    private String className;

    EnterpriseBeanMockery(Mockery mockery, String name, int kind) {
        this.mockery = mockery;
        this.name = name;
        this.kind = kind;
    }

    @SuppressWarnings("unchecked")
    public T ejbClass(String name) {
        this.className = name;
        return (T) this;
    }

    protected <B extends EnterpriseBean> B mockEnterpriseBean(final Class<B> klass) {
        final B bean = mockery.mock(klass, "enterpriseBean-" + numBeans.incrementAndGet());
        mockery.checking(new Expectations() {
            {
                allowing(bean).getName();
                will(returnValue(name));

                allowing(bean).getKindValue();
                will(returnValue(kind));

                allowing(bean).getEjbClassName();
                will(returnValue(className));
            }
        });
        return bean;
    }
}
