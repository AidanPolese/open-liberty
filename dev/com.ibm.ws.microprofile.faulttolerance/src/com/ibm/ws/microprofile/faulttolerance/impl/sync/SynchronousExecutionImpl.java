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
package com.ibm.ws.microprofile.faulttolerance.impl.sync;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.ExecutionException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.faulttolerance.impl.CircuitBreakerImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.RetryImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskContext;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
import com.ibm.ws.microprofile.faulttolerance.impl.TimeoutImpl;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.Execution;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.SyncFailsafe;

/**
 *
 */
public class SynchronousExecutionImpl<R> implements Execution<R> {

    private static final TraceComponent tc = Tr.register(SynchronousExecutionImpl.class);

    private final TaskRunner<R> taskRunner;
    private final TaskContext taskContext;

    //Standard constructor for a synchronous execution
    public SynchronousExecutionImpl(RetryPolicy retryPolicy,
                                    CircuitBreakerPolicy circuitBreakerPolicy,
                                    TimeoutPolicy timeoutPolicy,
                                    BulkheadPolicy bulkheadPolicy,
                                    FallbackPolicy fallbackPolicy,
                                    ScheduledExecutorService scheduledExecutorService) {

        TimeoutImpl timeout = null;
        if (timeoutPolicy != null) {
            timeout = new TimeoutImpl(timeoutPolicy, scheduledExecutorService);
        }

        CircuitBreakerImpl circuitBreaker = null;
        if (circuitBreakerPolicy != null) {
            circuitBreaker = new CircuitBreakerImpl(circuitBreakerPolicy, false);
        }

        this.taskRunner = new SemaphoreTaskRunner<R>(bulkheadPolicy);

        RetryImpl retry = new RetryImpl(retryPolicy);
        this.taskContext = new TaskContext(timeout, circuitBreaker, fallbackPolicy, retry);
    }

    //internal constructor for the nested synchronous part of an asynchronous execution
    public SynchronousExecutionImpl(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.taskRunner = new NestedSynchronousTaskRunner<>();
    }

    protected Callable<R> createTask(Callable<R> callable, ExecutionContext executionContext, TaskContext taskContext) {
        Callable<R> task = () -> {
            R result = null;
            try {
                result = this.taskRunner.runTask(callable, executionContext, taskContext);
            } finally {
                taskContext.end();
            }
            return result;
        };
        return task;
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore({ net.jodah.failsafe.CircuitBreakerOpenException.class, net.jodah.failsafe.FailsafeException.class })
    public R execute(Callable<R> callable, ExecutionContext executionContext) {

        SyncFailsafe<R> failsafe = Failsafe.with(this.taskContext.getRetry());

        TimeoutImpl timeout = taskContext.getTimeout();
        if (timeout != null) {
            failsafe.onRetry((t) -> {
                taskContext.onRetry();
            });
        }

        if (this.taskContext.getCircuitBreaker() != null) {
            failsafe = failsafe.with(this.taskContext.getCircuitBreaker());
        }

        if (this.taskContext.getFallbackPolicy() != null) {
            Callable<R> fallback = () -> {
                return (R) this.taskContext.getFallbackPolicy().getFallbackFunction().execute(executionContext);
            };
            failsafe = failsafe.withFallback(fallback);
        }
        Callable<R> task = createTask(callable, executionContext, this.taskContext);

        R result = null;
        try {
            result = failsafe.get(task);
        } catch (net.jodah.failsafe.CircuitBreakerOpenException e) {
            throw new CircuitBreakerOpenException(e);
        } catch (net.jodah.failsafe.FailsafeException e) {
            throw new ExecutionException(e.getCause());
        }

        return result;
    }
}
