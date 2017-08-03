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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;
import com.ibm.wsspi.threadcontext.WSContextService;

/**
 *
 */
public class AsyncExecutorImpl<R> extends ExecutorImpl<Future<R>> {

    private InternalExecutor<Callable<Future<R>>, Future<R>> internalExecutor;
    private final WSContextService contextService;

    public AsyncExecutorImpl(RetryPolicy retryPolicy,
                             CircuitBreakerPolicy circuitBreakerPolicy,
                             TimeoutPolicy timeoutPolicy,
                             BulkheadPolicy bulkheadPolicy,
                             FallbackPolicy<Future<R>> fallbackPolicy,
                             WSContextService contextService) {

        super(retryPolicy, circuitBreakerPolicy, timeoutPolicy, bulkheadPolicy, fallbackPolicy);
        this.contextService = contextService;
    }

    @Override
    protected InternalExecutor<Callable<Future<R>>, Future<R>> getInternalExecutor(BulkheadPolicy bulkheadPolicy) {
        synchronized (this) {
            if (this.internalExecutor == null) {
                this.internalExecutor = new ThreadPoolExecutor<>(bulkheadPolicy, getDefaultThreadFactory(), contextService);
            }
            return this.internalExecutor;
        }
    }

    @Override
    protected Callable<Future<R>> getInternalExecution(Callable<Future<R>> callable, InternalExecutor<Callable<Future<R>>, Future<R>> executor, Timeout timeout) {
        Callable<Future<R>> execution = () -> {
            QueuedFuture<R> future = (QueuedFuture<R>) executor.execute(callable, timeout);
            return future;
        };
        return execution;
    }

}
