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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;

import org.jboss.weld.ejb.spi.InterceptorBindings;

import com.ibm.websphere.csi.J2EEName;

public class EJBInterceptorRegistry {

    private final Map<J2EEName, InterceptorBindings> allInterceptors = new ConcurrentHashMap<J2EEName, InterceptorBindings>();

    /**
     * Register some interceptors for a given ejb
     * 
     * @param <T>
     * 
     * @param ejbDescriptor the ejb descriptor
     * @param interceptorBindings the interceptor bindings
     */
    public void registerInterceptors(J2EEName ejbJ2EEName, InterceptorBindings interceptorBindings) {
        allInterceptors.put(ejbJ2EEName, interceptorBindings);
    }

    /**
     * Find all the interceptors of a given type for a given method on an ejb
     * 
     * @param ejbName the J2EEName of the ejb
     * @param method the method to be intercepted
     * @param interceptionType the type of interception
     */
    public List<Interceptor<?>> getInterceptors(J2EEName ejbJ2EEName, Method method, InterceptionType interceptionType) {
        List<Interceptor<?>> interceptors = new ArrayList<Interceptor<?>>();

        //find the Interceptor Bindings for the ejb
        InterceptorBindings ejbInterceptors = allInterceptors.get(ejbJ2EEName);

        if (ejbInterceptors != null) {
            //get the method interceptors of the right type

            org.jboss.weld.interceptor.spi.model.InterceptionType internalInterceptionType =
                            org.jboss.weld.interceptor.spi.model.InterceptionType.valueOf(interceptionType.name());

            if (internalInterceptionType.isLifecycleCallback()) {
                List<Interceptor<?>> lifecycleInterceptors = ejbInterceptors.getLifecycleInterceptors(interceptionType);
                interceptors.addAll(lifecycleInterceptors);
            }
            else {
                List<Interceptor<?>> methodInterceptors = ejbInterceptors.getMethodInterceptors(interceptionType, method);
                interceptors.addAll(methodInterceptors);
            }
        }

        return interceptors;

    }

    /**
     * Clear out the contents of the registry, generally to only be used during CDI shutdown.
     */
    public void clearRegistry() {
        allInterceptors.clear();
    }
}
