/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.faulttolerance.spi.impl;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProviderResolver;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.impl.policy.BulkheadPolicyImpl;
import com.ibm.ws.microprofile.faulttolerance.spi.impl.policy.CircuitBreakerPolicyImpl;
import com.ibm.ws.microprofile.faulttolerance.spi.impl.policy.FallbackPolicyImpl;
import com.ibm.ws.microprofile.faulttolerance.spi.impl.policy.RetryPolicyImpl;
import com.ibm.ws.microprofile.faulttolerance.spi.impl.policy.TimeoutPolicyImpl;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.threadcontext.WSContextService;

@Component(name = "com.ibm.ws.microprofile.faulttolerance.spi.impl.ProviderResolverImpl", service = { FaultToleranceProviderResolver.class }, property = { "service.vendor=IBM" }, immediate = true)
public class ProviderResolverImpl extends FaultToleranceProviderResolver {

    /**
     * Reference to the context service for this managed executor service.
     */
    private final AtomicServiceReference<WSContextService> contextSvcRef = new AtomicServiceReference<WSContextService>("ContextService");

    /**
     * Activate a context and set the instance
     *
     * @param cc
     */
    public void activate(ComponentContext cc) {
        contextSvcRef.activate(cc);
        FaultToleranceProviderResolver.setInstance(this);
    }

    /**
     * Deactivate a context and set the instance to null
     *
     * @param cc
     */
    public void deactivate(ComponentContext cc) throws IOException {
        FaultToleranceProviderResolver.setInstance(null);
        contextSvcRef.deactivate(cc);
    }

    /**
     * Declarative Services method for setting the context service reference
     *
     * @param ref reference to the service
     */
    @Reference(policy = ReferencePolicy.DYNAMIC)
    protected void setContextService(ServiceReference<WSContextService> ref) {
        contextSvcRef.setReference(ref);
    }

    /**
     * Declarative Services method for unsetting the context service reference
     *
     * @param ref reference to the service
     */
    protected void unsetContextService(ServiceReference<WSContextService> ref) {
        contextSvcRef.unsetReference(ref);
    }

    @Override
    public BulkheadPolicy newBulkheadPolicy() {
        BulkheadPolicyImpl bulkhead = new BulkheadPolicyImpl();
        return bulkhead;
    }

    @Override
    public RetryPolicy newRetryPolicy() {
        RetryPolicyImpl retry = new RetryPolicyImpl();
        return retry;
    }

    @Override
    public CircuitBreakerPolicy newCircuitBreakerPolicy() {
        CircuitBreakerPolicyImpl circuitBreaker = new CircuitBreakerPolicyImpl();
        return circuitBreaker;
    }

    @Override
    public <T, R> FallbackPolicy<T, R> newFallbackPolicy() {
        FallbackPolicyImpl<T, R> fallback = new FallbackPolicyImpl<T, R>();
        return fallback;
    }

    /** {@inheritDoc} */
    @Override
    public TimeoutPolicy newTimeoutPolicy() {
        TimeoutPolicyImpl timeout = new TimeoutPolicyImpl();
        return timeout;
    }

    @Override
    public <T, R> ExecutionBuilder<T, R> newExecutionBuilder() {
        WSContextService contextService = getContextService();
        ExecutionBuilderImpl<T, R> ex = new ExecutionBuilderImpl<T, R>(contextService);
        return ex;
    }

    private WSContextService getContextService() {
        WSContextService contextService = AccessController.doPrivileged(new PrivilegedAction<WSContextService>() {
            @Override
            public WSContextService run() {
                return contextSvcRef.getService();
            }
        });
        return contextService;
    }
}
