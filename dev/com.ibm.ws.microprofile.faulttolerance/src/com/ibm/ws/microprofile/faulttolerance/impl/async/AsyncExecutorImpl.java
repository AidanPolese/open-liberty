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
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.microprofile.faulttolerance.impl.ExecutionContextImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.FTConstants;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
import com.ibm.ws.microprofile.faulttolerance.impl.sync.SynchronousExecutorImpl;
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
public class AsyncExecutorImpl<R> extends SynchronousExecutorImpl<Future<R>> {

    private static final TraceComponent tc = Tr.register(AsyncExecutorImpl.class);

    private TaskRunner<Future<R>> taskRunner;

    private final NestedExecutorImpl<Future<R>> nestedExecutor;

    public AsyncExecutorImpl(RetryPolicy retryPolicy,
                             CircuitBreakerPolicy circuitBreakerPolicy,
                             TimeoutPolicy timeoutPolicy,
                             BulkheadPolicy bulkheadPolicy,
                             FallbackPolicy fallbackPolicy,
                             WSContextService contextService,
                             PolicyExecutorProvider policyExecutorProvider,
                             ScheduledExecutorService scheduledExecutorService) {

        super(retryPolicy, circuitBreakerPolicy, timeoutPolicy, bulkheadPolicy, fallbackPolicy, scheduledExecutorService);

        if (policyExecutorProvider == null) {
            if ("true".equalsIgnoreCase(System.getProperty(FTConstants.JSE_FLAG))) {
                //this is really intended for unittest only, running outside of Liberty
                this.taskRunner = new JSEThreadPoolTaskRunner<R>(bulkheadPolicy, contextService);
            } else {
                throw new FaultToleranceException(Tr.formatMessage(tc, "internal.error.CWMFT4999E"));
            }
        } else {
            this.taskRunner = new PolicyExecutorTaskRunner<R>(bulkheadPolicy, contextService, policyExecutorProvider);
        }
        this.nestedExecutor = new NestedExecutorImpl<>();
    }

    @Override
    protected TaskRunner<Future<R>> getTaskRunner() {
        return this.taskRunner;
    }

    @Override
    protected Callable<Future<R>> createTask(Callable<Future<R>> callable, ExecutionContextImpl executionContext) {
        Callable<Future<R>> wrapped = () -> {
            Future<R> future = this.nestedExecutor.execute(callable, executionContext);
            return future;
        };

        Callable<Future<R>> task = () -> {
            Future<R> future = getTaskRunner().runTask(wrapped, executionContext);
            return future;
        };
        return task;
    }
}
