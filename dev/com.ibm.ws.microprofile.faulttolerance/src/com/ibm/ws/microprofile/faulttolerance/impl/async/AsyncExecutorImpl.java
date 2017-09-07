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

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.microprofile.faulttolerance.impl.ExecutionContextImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.FTConstants;
import com.ibm.ws.microprofile.faulttolerance.impl.sync.SynchronousExecutorImpl;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;
import com.ibm.ws.threading.PolicyExecutor;
import com.ibm.ws.threading.PolicyExecutor.QueueFullAction;
import com.ibm.ws.threading.PolicyExecutorProvider;
import com.ibm.wsspi.threadcontext.ThreadContextDescriptor;
import com.ibm.wsspi.threadcontext.WSContextService;

/**
 * An AsyncExecutorImpl builds on SynchronousExecutorImpl but the task which is run actually submits another task to be run asynchronously.
 *
 * Ultimately an Asynchronous execution consists of two synchronous executions, on different threads but with a shared execution context.
 */
public class AsyncExecutorImpl<R> extends SynchronousExecutorImpl<Future<R>> {

    private static final TraceComponent tc = Tr.register(AsyncExecutorImpl.class);

    private final NestedExecutorImpl<Future<R>> nestedExecutor;
    private final BulkheadPolicy bulkheadPolicy;
    private final WSContextService contextService;
    private ExecutorService executorService;

    public AsyncExecutorImpl(RetryPolicy retryPolicy,
                             CircuitBreakerPolicy circuitBreakerPolicy,
                             TimeoutPolicy timeoutPolicy,
                             BulkheadPolicy bulkheadPolicy,
                             FallbackPolicy fallbackPolicy,
                             WSContextService contextService,
                             PolicyExecutorProvider policyExecutorProvider,
                             ScheduledExecutorService scheduledExecutorService) {

        super(retryPolicy, circuitBreakerPolicy, timeoutPolicy, bulkheadPolicy, fallbackPolicy, scheduledExecutorService);

        this.nestedExecutor = new NestedExecutorImpl<>();

        this.bulkheadPolicy = bulkheadPolicy;
        this.contextService = contextService;

        if (policyExecutorProvider != null) {
            //this is the normal case when running in Liberty
            //create a policy executor to run things asynchronously

            //TODO make the ID more human readable
            PolicyExecutor policyExecutor = policyExecutorProvider.create("FaultTolerance_" + UUID.randomUUID().toString());
            policyExecutor.queueFullAction(QueueFullAction.Abort);

            //if there is supposed to be a bulkhead then restrict the size of the policy executor
            if (this.bulkheadPolicy != null) {
                int maxThreads = bulkheadPolicy.getMaxThreads();
                int queueSize = bulkheadPolicy.getQueueSize();
                policyExecutor.maxConcurrency(maxThreads);
                policyExecutor.maxQueueSize(queueSize);
            }

            this.executorService = policyExecutor;
        } else {
            //this is really intended for unittest only, running outside of Liberty
            //create a "basic" Thread Pool to run things asynchronously
            if ("true".equalsIgnoreCase(System.getProperty(FTConstants.JSE_FLAG))) {

                //even if there is no bulkhead, we don't want an unlimited queue
                int maxThreads = Integer.MAX_VALUE;
                int queueSize = 1000;
                if (this.bulkheadPolicy != null) {
                    maxThreads = bulkheadPolicy.getMaxThreads();
                    queueSize = bulkheadPolicy.getQueueSize();
                }

                ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueSize, true);
                this.executorService = new java.util.concurrent.ThreadPoolExecutor(maxThreads, maxThreads, 0l, TimeUnit.MILLISECONDS, queue, Executors.defaultThreadFactory());
            } else {
                throw new FaultToleranceException(Tr.formatMessage(tc, "internal.error.CWMFT4999E"));
            }
        }
    }

    @Override
    protected Callable<Future<R>> createTask(Callable<Future<R>> callable, ExecutionContextImpl executionContext) {
        //this is the inner nested task that will be run synchronously
        Callable<Future<R>> innerTask = () -> {
            Future<R> future = this.nestedExecutor.execute(callable, executionContext);
            return future;
        };

        //this is the outer task which will be run synchronously but launch a new thread to to run the inner one
        Callable<Future<R>> outerTask = () -> {
            ThreadContextDescriptor threadContext = null;
            if (this.contextService != null) {
                threadContext = this.contextService.captureThreadContext(new HashMap<String, String>());
            }
            QueuedFuture<R> queuedFuture = new QueuedFuture<>(innerTask, executionContext, threadContext);
            queuedFuture.start(this.executorService);
            return queuedFuture;
        };
        return outerTask;
    }
}
