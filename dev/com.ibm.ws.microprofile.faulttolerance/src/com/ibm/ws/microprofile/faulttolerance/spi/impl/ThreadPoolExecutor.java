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

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.wsspi.threadcontext.ThreadContextDescriptor;
import com.ibm.wsspi.threadcontext.WSContextService;

/**
 *
 */
public class ThreadPoolExecutor<R> implements InternalExecutor<Callable<Future<R>>, Future<R>> {

    private final ExecutorService executorService;
    private final WSContextService contextService;

    public ThreadPoolExecutor(BulkheadPolicy bulkheadPolicy, ThreadFactory threadFactory, WSContextService contextService) {
        int maxThreads = Integer.MAX_VALUE;
        int queueSize = 1000;
        if (bulkheadPolicy != null) {
            maxThreads = bulkheadPolicy.getMaxThreads();
            queueSize = bulkheadPolicy.getQueueSize();
        }
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueSize, true);
        this.executorService = new java.util.concurrent.ThreadPoolExecutor(maxThreads, maxThreads, 0l, TimeUnit.MILLISECONDS, queue, threadFactory);
        this.contextService = contextService;
    }

    @Override
    public QueuedFuture<R> execute(Callable<Future<R>> callable, Timeout timeout) {
        ThreadContextDescriptor threadContext = null;
        if (this.contextService != null) {
            threadContext = this.contextService.captureThreadContext(new HashMap<String, String>());
        }
        QueuedFuture<R> queuedFuture = new QueuedFuture<>(callable, timeout, threadContext);
        queuedFuture.start(this.executorService);

        return queuedFuture;
    }
}
