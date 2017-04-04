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

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;

import org.jboss.weld.ejb.spi.EjbServices;

import com.ibm.websphere.csi.J2EEName;

public interface WebSphereEjbServices extends EjbServices {

    /**
     * Find all the interceptors of a given type for a given method on an ejb
     * 
     * @param ejbName the J2EEName of the ejb
     * @param method the method to be intercepted
     * @param interceptionType the type of interception
     */
    public List<Interceptor<?>> getInterceptors(J2EEName ejbJ2EEName, Method method, InterceptionType interceptionType);
}
