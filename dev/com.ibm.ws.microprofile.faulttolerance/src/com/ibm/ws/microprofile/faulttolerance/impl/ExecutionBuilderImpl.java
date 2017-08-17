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
package com.ibm.ws.microprofile.faulttolerance.impl;

import java.util.concurrent.Future;

import com.ibm.ws.microprofile.faulttolerance.impl.async.AsyncExecutionImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.sync.SynchronousExecutionImpl;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.Execution;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;
import com.ibm.ws.threading.PolicyExecutorProvider;
import com.ibm.wsspi.threadcontext.WSContextService;

public class ExecutionBuilderImpl<T, R> implements ExecutionBuilder<T, R> {

    private CircuitBreakerPolicy circuitBreakerPolicy = null;
    private RetryPolicy retryPolicy = null;
    private BulkheadPolicy bulkheadPolicy = null;
    private FallbackPolicy fallbackPolicy = null;
    private TimeoutPolicy timeoutPolicy = null;
    private final WSContextService contextService;
    private final PolicyExecutorProvider policyExecutorProvider;

    public ExecutionBuilderImpl(WSContextService contextService, PolicyExecutorProvider policyExecutorProvider) {
        this.contextService = contextService;
        this.policyExecutorProvider = policyExecutorProvider;
    }

    /** {@inheritDoc} */
    @Override
    public ExecutionBuilder<T, R> setRetryPolicy(RetryPolicy retry) {
        this.retryPolicy = retry;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ExecutionBuilder<T, R> setCircuitBreakerPolicy(CircuitBreakerPolicy circuitBreaker) {
        this.circuitBreakerPolicy = circuitBreaker;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ExecutionBuilder<T, R> setBulkheadPolicy(BulkheadPolicy bulkhead) {
        this.bulkheadPolicy = bulkhead;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ExecutionBuilder<T, R> setFallbackPolicy(FallbackPolicy fallback) {
        this.fallbackPolicy = fallback;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public ExecutionBuilder<T, R> setTimeoutPolicy(TimeoutPolicy timeout) {
        this.timeoutPolicy = timeout;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Execution<R> build() {
        Execution<R> executor = new SynchronousExecutionImpl<R>(this.retryPolicy, this.circuitBreakerPolicy, this.timeoutPolicy, this.bulkheadPolicy, this.fallbackPolicy);

        return executor;
    }

    /** {@inheritDoc} */
    @Override
    public Execution<Future<R>> buildAsync() {
        Execution<Future<R>> executor = new AsyncExecutionImpl<R>(this.retryPolicy, this.circuitBreakerPolicy, this.timeoutPolicy, this.bulkheadPolicy, this.fallbackPolicy, this.contextService, this.policyExecutorProvider);

        return executor;
    }
}
