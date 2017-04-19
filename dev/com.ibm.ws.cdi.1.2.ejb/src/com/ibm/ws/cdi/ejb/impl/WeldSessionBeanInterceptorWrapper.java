/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.ejb.impl;

import java.lang.reflect.UndeclaredThrowableException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.enterprise.inject.spi.InterceptionType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;

import org.jboss.weld.Container;
import org.jboss.weld.ejb.AbstractEJBRequestScopeActivationInterceptor;
import org.jboss.weld.manager.BeanManagerImpl;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;

/**
 * Wrapper around {@link AbstractEJBRequestScopeActivationInterceptor} so that we can add annotations to register it for the right callbacks and to create an
 * EJBSecurityContextStore.
 */
public class WeldSessionBeanInterceptorWrapper extends AbstractEJBRequestScopeActivationInterceptor {

    /** serial version id for serialization */
    private static final long serialVersionUID = -4630548195644884051L;

    private static final TraceComponent tc = Tr.register(WeldSessionBeanInterceptorWrapper.class);

    private transient BeanManagerImpl beanManagerImpl = null;

    @Resource
    transient EJBContext ejbContext;

    @Override
    @AroundTimeout
    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocation) throws Exception {
        // Note: Per spec, @PostConstruct may throw Exception if same method is also
        //       used for @AroundInvoke or @AroundTimeout, but WELD doesn't permit it.
        try {
            EjbSecurityContextStore.getCurrentInstance().storeEJBContext(this.ejbContext);
            return super.aroundInvoke(invocation);
        } finally {
            EjbSecurityContextStore.getCurrentInstance().removeEJBContext();
        }
    }

    @PostConstruct
    public void postConstruct(InvocationContext invocation) {
        try {
            aroundInvoke(invocation);
        } catch (Exception e) {
            Tr.error(tc, "lifecycle.interceptor.exception.CWOWB2001E", InterceptionType.POST_CONSTRUCT, e);
            throw new UndeclaredThrowableException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected BeanManagerImpl getBeanManager() {

        // Lifetime of the interceptor is the same as the lifetime of the object, so cache the bean manager
        if (beanManagerImpl == null) {
            // Get the current application ID
            ComponentMetaData cmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
            J2EEName applicationJ2EEName = cmd.getModuleMetaData().getApplicationMetaData().getJ2EEName();
            String id = applicationJ2EEName.toString();

            // Now look up the weld container for the application and get it's BeanManagerImpl
            beanManagerImpl = Container.instance(id).deploymentManager();
        }

        return beanManagerImpl;
    }
}
