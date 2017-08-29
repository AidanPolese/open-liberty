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

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.microprofile.faulttolerance.impl.FTConstants;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskContext;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
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

    private static final TraceComponent tc = Tr.register(AsyncExecutionImpl.class);

    private TaskRunner<Future<R>> taskRunner;

    public AsyncExecutionImpl(RetryPolicy retryPolicy,
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
    }

    @Override
    protected Callable<Future<R>> createTask(Callable<Future<R>> callable, ExecutionContext executionContext, TaskContext taskContext) {
        Callable<Future<R>> wrapped = () -> {
            taskContext.setNested();
            SynchronousExecutionImpl<Future<R>> nestedExecution = new SynchronousExecutionImpl<Future<R>>(taskContext);
            Future<R> future = nestedExecution.execute(callable, executionContext);
            return future;
        };

        Callable<Future<R>> task = () -> {
            Future<R> future = this.taskRunner.runTask(wrapped, executionContext, taskContext);
            return future;
        };
        return task;
    }
}
