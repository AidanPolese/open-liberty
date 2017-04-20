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

import java.io.Serializable;

import org.jboss.weld.ejb.spi.BusinessInterfaceDescriptor;

public class BusinessInterfaceDescriptorImpl<T> implements BusinessInterfaceDescriptor<T>, Serializable
{
    private static final long serialVersionUID = 8407700456763662820L;

    private final Class<T> interfaceClass;

    public BusinessInterfaceDescriptorImpl(Class<T> interfaceClass) throws ClassNotFoundException
    {
        this.interfaceClass = interfaceClass;
    }

    public static <K> BusinessInterfaceDescriptor<K> newInstance(Class<K> interfaceClass) throws ClassNotFoundException
    {
        return new BusinessInterfaceDescriptorImpl<K>(interfaceClass);
    }

    @Override
    public Class<T> getInterface()
    {
        return interfaceClass;
    }

    @Override
    public String toString() {
        return "BusinessInterfaceDescriptor: " + interfaceClass.getName();
    }
}
