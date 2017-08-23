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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
import com.ibm.ws.microprofile.faulttolerance.impl.Timeout;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.threading.PolicyExecutor;
import com.ibm.ws.threading.PolicyExecutor.QueueFullAction;
import com.ibm.ws.threading.PolicyExecutorProvider;
import com.ibm.wsspi.threadcontext.ThreadContextDescriptor;
import com.ibm.wsspi.threadcontext.WSContextService;

/**
 *
 */
public class PolicyExecutorTaskRunner<R> implements TaskRunner<Callable<Future<R>>, Future<R>> {

    private final ExecutorService executorService;
    private final WSContextService contextService;

    public PolicyExecutorTaskRunner(BulkheadPolicy bulkheadPolicy, ThreadFactory threadFactory, WSContextService contextService, PolicyExecutorProvider policyExecutorProvider) {
        int maxThreads = Integer.MAX_VALUE;
        int queueSize = 1000;
        if (bulkheadPolicy != null) {
            maxThreads = bulkheadPolicy.getMaxThreads();
            queueSize = bulkheadPolicy.getQueueSize();
        }
        PolicyExecutor policyExecutor = policyExecutorProvider.create("FaultTolerancePolicyExecutor_" + UUID.randomUUID().toString());
        this.executorService = policyExecutor.maxConcurrency(maxThreads).maxQueueSize(queueSize).queueFullAction(QueueFullAction.Abort);
        this.contextService = contextService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public QueuedFuture<R> runTask(Callable<Future<R>> callable, Timeout timeout) {
        ThreadContextDescriptor threadContext = null;
        if (this.contextService != null) {
            threadContext = this.contextService.captureThreadContext(new HashMap<String, String>());
        }
        QueuedFuture<R> queuedFuture = new QueuedFuture<>(callable, timeout, threadContext);
        queuedFuture.start(this.executorService);

        return queuedFuture;
    }
}
