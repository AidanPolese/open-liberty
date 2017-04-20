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

import com.ibm.ws.cdi.interfaces.ManagedBeanDescriptor;
import com.ibm.ws.ejbcontainer.ManagedBeanEndpoint;

public class ManagedBeanDescriptorImpl<T> implements ManagedBeanDescriptor<T> {
    private final Class<T> beanClass;
    private final String mbJ2EENameString;

    private ManagedBeanDescriptorImpl(ManagedBeanEndpoint mb, Class<T> beanClass)
    {
        this.beanClass = beanClass;
        this.mbJ2EENameString = mb.getJ2EEName().toString();
    }

    public static ManagedBeanDescriptor<?> newInstance(ManagedBeanEndpoint mb, ClassLoader classLoader) throws ClassNotFoundException
    {
        String beanClassName = mb.getClassName();
        Class<?> beanClass = classLoader.loadClass(beanClassName);

        return newInstance(mb, beanClass);
    }

    private static <K> ManagedBeanDescriptor<K> newInstance(ManagedBeanEndpoint mb, Class<K> beanClass)
    {
        return new ManagedBeanDescriptorImpl<K>(mb, beanClass);
    }

    @Override
    public Class<T> getBeanClass() {
        return beanClass;
    }

    @Override
    public String toString() {
        return "ManagedBeanDescriptor: " + this.mbJ2EENameString;
    }
}
