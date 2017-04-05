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
package com.ibm.ws.jaxws.support;

import java.util.Map;

import com.ibm.ws.jaxws.support.JaxWsInstanceManager.InstanceInterceptor;
import com.ibm.ws.jaxws.support.JaxWsInstanceManager.InterceptException;
import com.ibm.ws.jaxws.support.JaxWsInstanceManager.InterceptorContext;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionTarget;
import com.ibm.wsspi.injectionengine.ReferenceContext;

/**
 *
 */
public class ReferenceContextInjectionInstanceInterceptor implements InstanceInterceptor {

    private final Map<Class<?>, ReferenceContext> referenceContextMap;

    public ReferenceContextInjectionInstanceInterceptor(Map<Class<?>, ReferenceContext> referenceContextMap) {
        this.referenceContextMap = referenceContextMap;
    }

    @Override
    public void postNewInstance(InterceptorContext ctx) throws InterceptException {
        try {
            Object instance = ctx.getInstance();
            ReferenceContext referenceContext = referenceContextMap.get(instance.getClass());
            InjectionTarget[] injectionTargets = referenceContext.getInjectionTargets(instance.getClass());
            if (injectionTargets == null || injectionTargets.length == 0) {
                return;
            }
            for (InjectionTarget injectionTarget : injectionTargets) {
                injectionTarget.inject(instance, null);
            }
        } catch (InjectionException e) {
            throw new InterceptException(e);
        }
    }

    @Override
    public void postInjectInstance(InterceptorContext ctx) {}

    @Override
    public void preDestroyInstance(InterceptorContext ctx) throws InterceptException {}

}
