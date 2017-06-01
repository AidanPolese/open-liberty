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
package com.ibm.ejs.container.interceptors;

import java.util.HashMap;
import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * Provides a ManagedBean specific implementation of the InvocationContext
 * interface. <p>
 *
 * In general, the InvocationContext for a ManagedBean is very similar to that
 * of an EJB; the main difference being in the way the context data is managed.
 * When calling ManagedBeans methods, a method invocation context is not
 * available on the thread (i.e. EJSDeployedSupport), and the bean method
 * is not able to access the context data (i.e. no EJBContext.getContextData)
 * so the context data may just be created lazily and cached right on the
 * InvocationContext.
 */
public class ManagedBeanInvocationContext<T> extends InvocationContextImpl<T>
{
    private static final TraceComponent tc = Tr.register(ManagedBeanInvocationContext.class,
                                                         "EJB3Interceptors",
                                                         "com.ibm.ejs.container.container");

    /**
     * The ContextMap that is returned by getContextData() method.
     * The interceptor objects uses this map storing state information
     * to be shared by different interceptor instances. The lifetime
     * of the context data is only for the life of current invocation.
     */
    private Map<String, Object> contextData;

    @Override
    public Map<String, Object> getContextData()
    {
        if (contextData == null) {
            contextData = new HashMap<String, Object>();
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "MB.getContextData: created empty");
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "MB.getContextData: " + contextData.size());
        }
        return contextData;
    }

}
