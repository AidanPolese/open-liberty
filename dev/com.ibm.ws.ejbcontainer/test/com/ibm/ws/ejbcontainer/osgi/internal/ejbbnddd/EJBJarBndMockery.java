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

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.ibm.ws.javaee.dd.commonbnd.Interceptor;
import com.ibm.ws.javaee.dd.commonbnd.MessageDestination;
import com.ibm.ws.javaee.dd.ejbbnd.EJBJarBnd;
import com.ibm.ws.javaee.dd.ejbbnd.EnterpriseBean;
import com.ibm.ws.javaee.dd.ejbbnd.MessageDriven;
import com.ibm.ws.javaee.dd.ejbbnd.Session;

public class EJBJarBndMockery {
    private final Mockery mockery;
    private final List<EnterpriseBean> enterpriseBeans = new ArrayList<EnterpriseBean>();
    private final List<Interceptor> interceptors = new ArrayList<Interceptor>();
    private final List<MessageDestination> messageDestinations = new ArrayList<MessageDestination>();

    public EJBJarBndMockery(Mockery mockery) {
        this.mockery = mockery;
    }

    public EJBJarBndMockery sessionBean(Session bean) {
        this.enterpriseBeans.add(bean);
        return this;
    }

    public EJBJarBndMockery messageDrivenBean(MessageDriven bean) {
        this.enterpriseBeans.add(bean);
        return this;
    }

    public EJBJarBndMockery interceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
        return this;
    }

    public SessionMockery session(String name) {
        return new SessionMockery(mockery, name);
    }

    public MessageDrivenMockery messageDriven(String name) {
        return new MessageDrivenMockery(mockery, name);
    }

    public InterceptorMockery interceptor(String className) {
        return new InterceptorMockery(mockery, className);
    }

    public EJBJarBnd mock() {
        final EJBJarBnd ejbJarBnd = mockery.mock(EJBJarBnd.class);
        mockery.checking(new Expectations() {
            {
                allowing(ejbJarBnd).getEnterpriseBeans();
                will(returnValue(enterpriseBeans));

                allowing(ejbJarBnd).getInterceptors();
                will(returnValue(interceptors)); // empty for now

                allowing(ejbJarBnd).getMessageDestinations();
                will(returnValue(messageDestinations)); // empty for now
            }
        });
        return ejbJarBnd;
    }
}