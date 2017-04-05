/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.injection;

import java.util.Set;

import javax.ws.rs.core.Application;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.injection.metadata.InjectionRuntimeContext;

/**
 * A proxy that can be injected for Applications
 */
public class ApplicationInjectionProxy extends Application {

    private static final TraceComponent tc = Tr.register(ApplicationInjectionProxy.class);

    @Override
    public Set<Class<?>> getClasses() {
        final String methodName = "getClasses";
        if (tc.isEntryEnabled()) {
            Tr.entry(tc, methodName);
        }
        // use runtimeContext from TLS
        InjectionRuntimeContext runtimeContext = InjectionRuntimeContextHelper.getRuntimeContext();
        // get the real context from the
        // RuntimeContext
        Object context = runtimeContext.getRuntimeCtxObject(Application.class.getName());
        Set<Class<?>> classes = ((Application) context).getClasses();
        if (tc.isEntryEnabled()) {
            Tr.exit(tc, methodName, classes);
        }
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        final String methodName = "getSingletons";
        if (tc.isEntryEnabled())
            Tr.entry(tc, methodName);
        // use runtimeContext from TLS
        InjectionRuntimeContext runtimeContext = InjectionRuntimeContextHelper.getRuntimeContext();
        // get the real context from the
        // RuntimeContext
        Object context = runtimeContext.getRuntimeCtxObject(Application.class.getName());
        Set<Object> singletons = ((Application) context).getSingletons();
        if (tc.isEntryEnabled()) {
            Tr.exit(tc, methodName, singletons);
        }
        return singletons;
    }

}
