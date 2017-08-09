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
package com.ibm.ws.microprofile.faulttolerance.cdi;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;

/**
 *
 */
public class AggregatedFTPolicy {

    private boolean asynchronous = false;
    private RetryPolicy retryPolicy = null;
    private CircuitBreakerPolicy circuitBreakerPolicy = null;
    private BulkheadPolicy bulkheadPolicy = null;
    private TimeoutPolicy timeout;
    private FallbackPolicy<ExecutionContext, ?> fallbackPolicy;

    /**
     * @param asynchronous
     */
    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    /**
     * @param timeoutMillis
     */
    public void setTimeoutPolicy(TimeoutPolicy timeout) {
        this.timeout = timeout;
    }

    /**
     * @param retryPolicy
     */
    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    /**
     * @param circuitBreakerPolicy
     */
    public void setCircuitBreakerPolicy(CircuitBreakerPolicy circuitBreakerPolicy) {
        this.circuitBreakerPolicy = circuitBreakerPolicy;
    }

    /**
     * @param bulkheadPolicy
     */
    public void setBulkheadPolicy(BulkheadPolicy bulkheadPolicy) {
        this.bulkheadPolicy = bulkheadPolicy;
    }

    /**
     * @return
     */
    public boolean isAsynchronous() {
        return asynchronous;
    }

    /**
     * @return
     */
    public BulkheadPolicy getBulkheadPolicy() {
        return bulkheadPolicy;
    }

    /**
     * @return
     */
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /**
     * @return the timeoutMillis
     */
    public TimeoutPolicy getTimeoutPolicy() {
        return timeout;
    }

    /**
     * @return the circuitBreakerPolicy
     */
    public CircuitBreakerPolicy getCircuitBreakerPolicy() {
        return circuitBreakerPolicy;
    }

    public void setFallbackPolicy(FallbackPolicy<ExecutionContext, ?> fallbackPolicy) {
        this.fallbackPolicy = fallbackPolicy;
    }

    public FallbackPolicy<ExecutionContext, ?> getFallbackPolicy() {
        return this.fallbackPolicy;
    }

}
