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
package com.ibm.ws.microprofile.faulttolerance.impl.async;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
import com.ibm.ws.microprofile.faulttolerance.impl.ThreadUtils;
import com.ibm.ws.microprofile.faulttolerance.impl.Timeout;
import com.ibm.ws.microprofile.faulttolerance.impl.sync.SynchronousExecutionImpl;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;
import com.ibm.ws.threading.PolicyExecutorProvider;
import com.ibm.wsspi.threadcontext.WSContextService;

/**
 *
 */
public class AsyncExecutionImpl<R> extends SynchronousExecutionImpl<Future<R>> {

    private TaskRunner<Callable<Future<R>>, Future<R>> taskRunner;
    private final WSContextService contextService;
    private ThreadFactory defaultThreadFactory;
    private final PolicyExecutorProvider policyExecutorProvider;

    public AsyncExecutionImpl(RetryPolicy retryPolicy,
                              CircuitBreakerPolicy circuitBreakerPolicy,
                              TimeoutPolicy timeoutPolicy,
                              BulkheadPolicy bulkheadPolicy,
                              FallbackPolicy<Future<R>> fallbackPolicy,
                              WSContextService contextService,
                              PolicyExecutorProvider policyExecutorProvider) {

        super(retryPolicy, circuitBreakerPolicy, timeoutPolicy, bulkheadPolicy, fallbackPolicy);
        this.contextService = contextService;
        this.policyExecutorProvider = policyExecutorProvider;
    }

    @Override
    protected TaskRunner<Callable<Future<R>>, Future<R>> getTaskRunner(BulkheadPolicy bulkheadPolicy) {
        synchronized (this) {
            if (this.taskRunner == null) {
                if (this.policyExecutorProvider == null) {
                    this.taskRunner = new ThreadPoolTaskRunner<R>(bulkheadPolicy, getDefaultThreadFactory(), contextService);
                } else {
                    this.taskRunner = new PolicyExecutorTaskRunner<R>(bulkheadPolicy, getDefaultThreadFactory(), contextService, policyExecutorProvider);
                }
            }
            return this.taskRunner;
        }
    }

    @Override
    protected Callable<Future<R>> createTask(Callable<Future<R>> callable, TaskRunner<Callable<Future<R>>, Future<R>> runner, Timeout timeout) {
        Callable<Future<R>> task = () -> {
            Future<R> future = runner.runTask(callable, timeout);
            return future;
        };
        return task;
    }

    protected ThreadFactory getDefaultThreadFactory() {
        if (this.defaultThreadFactory == null) {
            defaultThreadFactory = ThreadUtils.getThreadFactory();
        }
        return defaultThreadFactory;
    }

}
