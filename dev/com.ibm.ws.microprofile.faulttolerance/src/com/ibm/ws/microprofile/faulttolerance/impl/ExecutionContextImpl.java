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

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import com.ibm.ws.microprofile.faulttolerance.impl.async.QueuedFuture;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;

public class ExecutionContextImpl implements ExecutionContext {

    private static final AtomicLong ID_GEN = new AtomicLong(0);

    private final Method method;
    private final Object[] params;
    private final TimeoutImpl timeout;
    private final RetryImpl retry;

    private final CircuitBreakerImpl circuitBreaker;
    private final FallbackPolicy fallbackPolicy;

    private volatile int retries = 0;
    private volatile long startTime;
    private final long id;

    public ExecutionContextImpl(Method method, Object[] params, TimeoutImpl timeout, CircuitBreakerImpl circuitBreaker, FallbackPolicy fallbackPolicy, RetryImpl retry) {
        this.method = method;
        this.params = new Object[params.length];
        //TODO is an arraycopy really required here?
        System.arraycopy(params, 0, this.params, 0, params.length);

        this.timeout = timeout;
        this.circuitBreaker = circuitBreaker;
        this.fallbackPolicy = fallbackPolicy;
        this.retry = retry;

        this.id = ID_GEN.incrementAndGet();
    }

    /** {@inheritDoc} */
    @Override
    public Method getMethod() {
        return method;
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getParameters() {
        return params;
    }

    @Override
    public String toString() {
        return "Execution Context: " + method;
    }

    /**
    *
    */
    public void start() {
        this.startTime = System.nanoTime();
        //System.out.println("TaskContext[" + id + "] start: " + this.startTime);
        if (timeout != null) {
            timeout.start(Thread.currentThread());
        }
    }

    public void start(QueuedFuture<?> future) {
        this.startTime = System.nanoTime();
        //System.out.println("TaskContext[" + id + "] start: " + this.startTime);
        if (timeout != null) {
            timeout.start(future);
        }
    }

    /**
    *
    */
    public void end() {
        //long endTime = System.nanoTime();
        //System.out.println("TaskContext[" + id + "] stop: " + endTime + " (" + (endTime - this.startTime) + ")");
        if (timeout != null) {
            timeout.stop(true);
        }
    }

    /**
    *
    */
    public void check() {
//        long checkTime = System.nanoTime();
//        System.out.println("TaskContext[" + id + "] check: " + checkTime + " (" + (checkTime - this.startTime) + ")");
        if (timeout != null) {
            timeout.check();
        }
    }

    public void onRetry() {
//        long retryTime = System.nanoTime();
//        System.out.println("TaskContext[" + id + "] onRetry: " + retryTime + " (" + (retryTime - this.startTime) + ")");
        this.retries++;
        if (timeout != null) {
            timeout.restart();
        }
    }

    public RetryImpl getRetry() {
        return retry;
    }

    public FallbackPolicy getFallbackPolicy() {
        return fallbackPolicy;
    }

    public CircuitBreakerImpl getCircuitBreaker() {
        return circuitBreaker;
    }

    /**
    *
    */
    public void setNested() {
        if (this.circuitBreaker != null) {
            this.circuitBreaker.setNested();
        }

        int retriesRemaining = this.retry.getMaxRetries() - this.retries;
        this.retry.withMaxRetries(retriesRemaining);
        if (this.retry.getMaxDuration() != null) {
            long maxDuration = this.retry.getMaxDuration().toNanos();
            long now = System.nanoTime();
            long elapsed = now - this.startTime;

            long delay = this.retry.getDelay().toNanos();
            maxDuration = maxDuration - elapsed;
            //TODO rather than setting maxDuration to delay, better to just not execute at all ... but then have to work out the right exception to throw
            if (maxDuration < delay) {
                maxDuration = delay;
            }
            this.retry.withMaxDuration(maxDuration, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * @return
     */
    public TimeoutImpl getTimeout() {
        return this.timeout;
    }

}
