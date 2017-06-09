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

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.ibm.ws.javaee.dd.ejb.ComponentViewableBean;

abstract class ComponentViewableBeanMockery<T extends ComponentViewableBeanMockery<T>> extends EnterpriseBeanMockery<T> {
    private String remoteHome;
    private String remote;
    private String localHome;
    private String local;

    ComponentViewableBeanMockery(Mockery mockery, String name, int kind) {
        super(mockery, name, kind);
    }

    @SuppressWarnings("unchecked")
    public T remote(String remoteHome, String remote) {
        this.remoteHome = remoteHome;
        this.remote = remote;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T local(String localHome, String local) {
        this.localHome = localHome;
        this.local = local;
        return (T) this;
    }

    protected <B extends ComponentViewableBean> B mockComponentViewableBean(final Class<B> klass) {
        final B bean = super.mockEnterpriseBean(klass);
        mockery.checking(new Expectations() {
            {
                allowing(bean).getHomeInterfaceName();
                will(returnValue(remoteHome));

                allowing(bean).getRemoteInterfaceName();
                will(returnValue(remote));

                allowing(bean).getLocalHomeInterfaceName();
                will(returnValue(localHome));

                allowing(bean).getLocalInterfaceName();
                will(returnValue(local));
            }
        });
        return bean;
    }
}
