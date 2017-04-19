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

import java.util.HashSet;
import java.util.Set;

import org.jboss.weld.ejb.spi.EjbDescriptor;

import com.ibm.ws.cdi.CDIException;
import com.ibm.ws.cdi.interfaces.EndPointsInfo;
import com.ibm.ws.cdi.interfaces.ManagedBeanDescriptor;

/**
 * The implementation class of the EndPointsInf, which holds the mangedbean descriptors, ejb descriptors, no-cdi interceptors and the module classloader
 */
public class EndPointsInfoImpl implements EndPointsInfo {

    private final Set<ManagedBeanDescriptor<?>> managedBeanDescs;
    private final Set<EjbDescriptor<?>> ejbDescs;
    private Set<Class<?>> nonCDIInterceptors;
    private final Set<String> nonCDIInterceptorClassNames;
    private final ClassLoader classloader;

    public EndPointsInfoImpl(Set<ManagedBeanDescriptor<?>> managedBeanDescs, Set<EjbDescriptor<?>> ejbDescs, Set<String> nonCDIinterceptors, ClassLoader classloader) {
        this.managedBeanDescs = managedBeanDescs;
        this.ejbDescs = ejbDescs;
        this.nonCDIInterceptorClassNames = nonCDIinterceptors;
        this.classloader = classloader;
    }

    /** {@inheritDoc} */
    @Override
    public Set<ManagedBeanDescriptor<?>> getManagedBeanDescriptors() {
        return this.managedBeanDescs;
    }

    /** {@inheritDoc} */
    @Override
    public Set<EjbDescriptor<?>> getEJBDescriptors() {
        return this.ejbDescs;
    }

    /** {@inheritDoc} */
    @Override
    public Set<Class<?>> getNonCDIInterceptors() throws CDIException {
        if (nonCDIInterceptors == null) {
            nonCDIInterceptors = new HashSet<Class<?>>();
            try {
                for (String interceptor : nonCDIInterceptorClassNames) {
                    Class<?> interceptorClass = classloader.loadClass(interceptor);
                    nonCDIInterceptors.add(interceptorClass);
                }
            } catch (ClassNotFoundException e) {
                throw new CDIException(e);
            }
        }
        return this.nonCDIInterceptors;
    }

}