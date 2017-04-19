/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.beanvalidation.v11.cdi.internal;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionTarget;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.beanvalidation.service.ValidationReleasable;

/**
 * Straight forward implementation when creating a CDI managed bean.
 */
public class ValidationReleasableImpl<T> implements ValidationReleasable<T> {

    private static final TraceComponent tc = Tr.register(ValidationReleasableImpl.class);

    private final CreationalContext<T> context;
    private final InjectionTarget<T> injectionTarget;
    private final T instance;

    public ValidationReleasableImpl(final CreationalContext<T> context,
                                    final InjectionTarget<T> injectionTarget,
                                    final T instance) {
        this.context = context;
        this.injectionTarget = injectionTarget;
        this.instance = instance;
    }

    @Override
    public void release() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "release", instance);
        }

        injectionTarget.preDestroy(instance);
        injectionTarget.dispose(instance);
        context.release();
    }

    @Override
    public T getInstance() {
        return instance;
    }
}
