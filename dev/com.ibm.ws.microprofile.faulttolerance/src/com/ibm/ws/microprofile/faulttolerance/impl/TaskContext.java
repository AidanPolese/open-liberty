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

import java.util.concurrent.TimeUnit;

import com.ibm.ws.microprofile.faulttolerance.impl.async.QueuedFuture;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;

/**
 *
 */
public class TaskContext {

    private final TimeoutImpl timeout;
    private final CircuitBreakerImpl circuitBreaker;
    private final RetryImpl retry;
    private final FallbackPolicy fallbackPolicy;
    private volatile int retries = 0;
    private volatile long startTime;

    /**
     * @param timeout
     */
    public TaskContext(TimeoutImpl timeout, CircuitBreakerImpl circuitBreaker, FallbackPolicy fallbackPolicy, RetryImpl retry) {
        this.timeout = timeout;
        this.circuitBreaker = circuitBreaker;
        this.fallbackPolicy = fallbackPolicy;
        this.retry = retry;
    }

    /**
     * @return
     */
    public FallbackPolicy getFallbackPolicy() {
        return fallbackPolicy;
    }

    /**
     * @return
     */
    public RetryImpl getRetry() {
        return retry;
    }

    /**
     * @return
     */
    public CircuitBreakerImpl getCircuitBreaker() {
        return circuitBreaker;
    }

    /**
     * @return
     */
    public TimeoutImpl getTimeout() {
        return this.timeout;
    }

    public long getStartTime() {
        return this.startTime;
    }

    /**
     *
     */
    public void start() {
        this.startTime = System.nanoTime();
        if (timeout != null) {
            timeout.start(Thread.currentThread());
        }
    }

    public void start(QueuedFuture<?> future) {
        this.startTime = System.nanoTime();
        if (timeout != null) {
            timeout.start(future);
        }
    }

    /**
    *
    */
    public void end() {
        if (timeout != null) {
            timeout.stop(true);
        }
    }

    /**
     *
     */
    public void check() {
        if (timeout != null) {
            timeout.check();
        }
    }

    public void onRetry() {
        this.retries++;
        if (timeout != null) {
            timeout.restart();
        }
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

}
