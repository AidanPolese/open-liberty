/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
