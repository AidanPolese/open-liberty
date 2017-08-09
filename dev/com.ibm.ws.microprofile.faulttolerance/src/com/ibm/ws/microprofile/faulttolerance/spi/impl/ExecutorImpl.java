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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.ExecutionException;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.SyncFailsafe;

/**
 *
 */
public class ExecutorImpl<T, R> implements Executor<T, R> {

    private final FallbackPolicy<T, R> fallbackPolicy;
    private net.jodah.failsafe.CircuitBreaker circuitBreaker;
    private InternalExecutor<Callable<R>, R> internalExecutor;
    private final BulkheadPolicy bulkheadPolicy;
    private final RetryPolicy retryPolicy;
    private final TimeoutPolicy timeoutPolicy;
    private ExecutorService defaultExecutorService;
    private ThreadFactory defaultThreadFactory;

    public ExecutorImpl(RetryPolicy retryPolicy,
                        CircuitBreakerPolicy circuitBreakerPolicy,
                        TimeoutPolicy timeoutPolicy,
                        BulkheadPolicy bulkheadPolicy,
                        FallbackPolicy<T, R> fallbackPolicy) {

        this.fallbackPolicy = fallbackPolicy;
        this.bulkheadPolicy = bulkheadPolicy;
        this.retryPolicy = retryPolicy;
        this.timeoutPolicy = timeoutPolicy;

        if (circuitBreakerPolicy != null) {
            this.circuitBreaker = new CircuitBreakerImpl(circuitBreakerPolicy);
        }
    }

    protected InternalExecutor<Callable<R>, R> getInternalExecutor(BulkheadPolicy bulkheadPolicy) {
        synchronized (this) {
            if (this.internalExecutor == null) {
                this.internalExecutor = new SemaphoreExecutor<R>(bulkheadPolicy);
            }
            return this.internalExecutor;
        }
    }

    protected Callable<R> getInternalExecution(Callable<R> callable, InternalExecutor<Callable<R>, R> executor, Timeout timeout) {
        Callable<R> execution = () -> {
            R result = null;
            try {
                result = executor.execute(callable, timeout);
            } finally {
                if (timeout != null) {
                    timeout.stop(true);
                }
            }
            return result;
        };
        return execution;
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore({ net.jodah.failsafe.CircuitBreakerOpenException.class, net.jodah.failsafe.FailsafeException.class })
    public R execute(Callable<R> callable, T context) {

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
                return this.fallbackPolicy.getFallback().execute(context);
            };
            failsafe = failsafe.withFallback(fallback);
        }
        InternalExecutor<Callable<R>, R> executor = getInternalExecutor(bulkheadPolicy);
        Callable<R> execution = getInternalExecution(callable, executor, timeout);

        R result = null;
        try {
            result = failsafe.get(execution);
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

    protected ThreadFactory getDefaultThreadFactory() {
        if (this.defaultThreadFactory == null) {
            defaultThreadFactory = ThreadUtils.getThreadFactory();
        }
        return defaultThreadFactory;
    }
}
