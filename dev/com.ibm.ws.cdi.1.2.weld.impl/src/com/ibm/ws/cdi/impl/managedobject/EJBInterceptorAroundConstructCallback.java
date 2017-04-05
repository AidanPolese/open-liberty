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
package com.ibm.ws.cdi.impl.managedobject;

import java.util.Map;

import javax.enterprise.inject.spi.AnnotatedConstructor;

import org.jboss.weld.construction.api.AroundConstructCallback;
import org.jboss.weld.construction.api.ConstructionHandle;

import com.ibm.ws.managedobject.ManagedObjectInvocationContext;

/**
 * Implementation of AroundConstructCallback that interposes the
 * AroundCallback interceptors on the call to the constructor.
 */
public class EJBInterceptorAroundConstructCallback<T> implements AroundConstructCallback<T> {

    private final ManagedObjectInvocationContext<T> aciCtx;

    public EJBInterceptorAroundConstructCallback(ManagedObjectInvocationContext<T> aciCtx) {

        this.aciCtx = aciCtx;
    }

    /** {@inheritDoc} */
    @Override
    public T aroundConstruct(ConstructionHandle<T> handle, AnnotatedConstructor<T> constructor, Object[] parameters, Map<String, Object> data) throws Exception {
        return aciCtx.aroundConstruct(new ConstructionCallbackImpl<T>(handle, constructor), parameters, data);
    }
}
