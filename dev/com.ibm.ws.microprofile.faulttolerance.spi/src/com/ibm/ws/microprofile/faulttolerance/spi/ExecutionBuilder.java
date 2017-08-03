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
package com.ibm.ws.microprofile.faulttolerance.spi;

import java.util.concurrent.Future;

public interface ExecutionBuilder<T, R> {

    public ExecutionBuilder<T, R> setRetryPolicy(RetryPolicy retry);

    public ExecutionBuilder<T, R> setCircuitBreakerPolicy(CircuitBreakerPolicy circuitBreaker);

    public ExecutionBuilder<T, R> setBulkheadPolicy(BulkheadPolicy bulkhead);

    public ExecutionBuilder<T, R> setFallbackPolicy(FallbackPolicy<R> fallback);

    public ExecutionBuilder<T, R> setAsyncFallbackPolicy(FallbackPolicy<Future<R>> fallback);

    public ExecutionBuilder<T, R> setTimeoutPolicy(TimeoutPolicy timeout);

    public Execution<R> build();

    public Execution<Future<R>> buildAsync();
}
