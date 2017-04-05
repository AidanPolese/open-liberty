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
package com.ibm.ws.cdi.interfaces;

import java.util.Set;

import org.jboss.weld.ejb.spi.EjbDescriptor;

import com.ibm.ws.cdi.CDIException;

/**
 *
 */
public interface EndPointsInfo {

    Set<ManagedBeanDescriptor<?>> getManagedBeanDescriptors();

    Set<EjbDescriptor<?>> getEJBDescriptors();

    Set<Class<?>> getNonCDIInterceptors() throws CDIException;
}
