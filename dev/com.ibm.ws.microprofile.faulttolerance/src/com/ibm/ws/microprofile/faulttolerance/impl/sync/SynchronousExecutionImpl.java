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
import java.util.concurrent.ExecutorService;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.ExecutionException;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.faulttolerance.impl.CircuitBreakerImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.RetryImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
import com.ibm.ws.microprofile.faulttolerance.impl.ThreadUtils;
import com.ibm.ws.microprofile.faulttolerance.impl.Timeout;
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

    private final FallbackPolicy fallbackPolicy;
    private net.jodah.failsafe.CircuitBreaker circuitBreaker;
    private TaskRunner<Callable<R>, R> taskRunner;
    private final BulkheadPolicy bulkheadPolicy;
    private final RetryPolicy retryPolicy;
    private final TimeoutPolicy timeoutPolicy;
    private ExecutorService defaultExecutorService;

    public SynchronousExecutionImpl(RetryPolicy retryPolicy,
                                    CircuitBreakerPolicy circuitBreakerPolicy,
                                    TimeoutPolicy timeoutPolicy,
                                    BulkheadPolicy bulkheadPolicy,
                                    FallbackPolicy fallbackPolicy) {

        this.fallbackPolicy = fallbackPolicy;
        this.bulkheadPolicy = bulkheadPolicy;
        this.retryPolicy = retryPolicy;
        this.timeoutPolicy = timeoutPolicy;

        if (circuitBreakerPolicy != null) {
            this.circuitBreaker = new CircuitBreakerImpl(circuitBreakerPolicy);
        }
    }

    protected TaskRunner<Callable<R>, R> getTaskRunner(BulkheadPolicy bulkheadPolicy) {
        synchronized (this) {
            if (this.taskRunner == null) {
                this.taskRunner = new SemaphoreTaskRunner<R>(bulkheadPolicy);
            }
            return this.taskRunner;
        }
    }

    protected Callable<R> createTask(Callable<R> callable, TaskRunner<Callable<R>, R> runner, Timeout timeout) {
        Callable<R> task = () -> {
            R result = null;
            try {
                result = runner.runTask(callable, timeout);
            } finally {
                if (timeout != null) {
                    timeout.stop(true);
                }
            }
            return result;
        };
        return task;
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore({ net.jodah.failsafe.CircuitBreakerOpenException.class, net.jodah.failsafe.FailsafeException.class })
    public R execute(Callable<R> callable, ExecutionContext context) {

        RetryImpl retry = new RetryImpl(this.retryPolicy);

        Timeout timeout = null;
        if (this.timeoutPolicy != null) {
            timeout = new Timeout(this.timeoutPolicy, getDefaultExecutorService());
        }

        SyncFailsafe<R> failsafe = Failsafe.with(retry);

        if (this.circuitBreaker != null) {
            failsafe = failsafe.with(this.circuitBreaker);
        }

        if (this.fallbackPolicy != null) {
            Callable<R> fallback = () -> {
                return (R) this.fallbackPolicy.getFallbackFunction().execute(context);
            };
            failsafe = failsafe.withFallback(fallback);
        }
        TaskRunner<Callable<R>, R> runner = getTaskRunner(bulkheadPolicy);
        Callable<R> task = createTask(callable, runner, timeout);

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

    protected ExecutorService getDefaultExecutorService() {
        if (this.defaultExecutorService == null) {
            defaultExecutorService = ThreadUtils.getDefaultExecutorService();
        }
        return defaultExecutorService;
    }
}
