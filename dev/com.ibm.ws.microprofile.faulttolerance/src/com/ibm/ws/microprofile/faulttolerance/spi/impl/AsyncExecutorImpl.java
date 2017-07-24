/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.faulttolerance.spi.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;
import com.ibm.wsspi.threadcontext.WSContextService;

/**
 *
 */
public class AsyncExecutorImpl<T, R> extends ExecutorImpl<T, Future<R>> {

    private InternalExecutor<Callable<Future<R>>, Future<R>> internalExecutor;
    private final WSContextService contextService;

    public AsyncExecutorImpl(RetryPolicy retryPolicy,
                             CircuitBreakerPolicy circuitBreakerPolicy,
                             TimeoutPolicy timeoutPolicy,
                             BulkheadPolicy bulkheadPolicy,
                             FallbackPolicy<T, Future<R>> fallbackPolicy,
                             WSContextService contextService) {

        super(retryPolicy, circuitBreakerPolicy, timeoutPolicy, bulkheadPolicy, fallbackPolicy);
        this.contextService = contextService;
    }

    @Override
    protected InternalExecutor<Callable<Future<R>>, Future<R>> getInternalExecutor(BulkheadPolicy bulkheadPolicy) {
        synchronized (this) {
            if (this.internalExecutor == null) {
                this.internalExecutor = new ThreadPoolExecutor<>(bulkheadPolicy, getDefaultThreadFactory(), contextService);
            }
            return this.internalExecutor;
        }
    }

    @Override
    protected Callable<Future<R>> getInternalExecution(Callable<Future<R>> callable, InternalExecutor<Callable<Future<R>>, Future<R>> executor, Timeout timeout) {
        Callable<Future<R>> execution = () -> {
            QueuedFuture<R> future = (QueuedFuture<R>) executor.execute(callable, timeout);
            return future;
        };
        return execution;
    }

}
