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

import com.ibm.ws.microprofile.faulttolerance.impl.ExecutionContextImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.threading.PolicyExecutor;
import com.ibm.ws.threading.PolicyExecutor.QueueFullAction;
import com.ibm.ws.threading.PolicyExecutorProvider;
import com.ibm.wsspi.threadcontext.ThreadContextDescriptor;
import com.ibm.wsspi.threadcontext.WSContextService;

/**
 *
 */
public class PolicyExecutorTaskRunner<R> implements TaskRunner<Future<R>> {

    private ExecutorService executorService;
    private final WSContextService contextService;
    private PolicyExecutor policyExecutor;
    private final int maxThreads;
    private final int queueSize;

    protected PolicyExecutorTaskRunner(BulkheadPolicy bulkheadPolicy, WSContextService contextService) {
        if (bulkheadPolicy != null) {
            this.maxThreads = bulkheadPolicy.getMaxThreads();
            this.queueSize = bulkheadPolicy.getQueueSize();
        } else {
            this.maxThreads = Integer.MAX_VALUE;
            this.queueSize = 1000;
        }
        this.contextService = contextService;
    }

    public PolicyExecutorTaskRunner(BulkheadPolicy bulkheadPolicy, WSContextService contextService, PolicyExecutorProvider policyExecutorProvider) {
        this(bulkheadPolicy, contextService);
        //TODO make the ID more human readable
        this.policyExecutor = policyExecutorProvider.create("FaultTolerance_" + UUID.randomUUID().toString());
    }

    protected ExecutorService getExecutorService() {
        if (this.executorService == null) {
            this.executorService = this.policyExecutor.maxConcurrency(getMaxThreads()).maxQueueSize(getQueueSize()).queueFullAction(QueueFullAction.Abort);
        }
        return this.executorService;
    }

    protected int getQueueSize() {
        return queueSize;
    }

    protected int getMaxThreads() {
        return maxThreads;
    }

    @SuppressWarnings("unchecked")
    @Override
    public QueuedFuture<R> runTask(Callable<Future<R>> task, ExecutionContextImpl executionContext) {
        ThreadContextDescriptor threadContext = null;
        if (this.contextService != null) {
            threadContext = this.contextService.captureThreadContext(new HashMap<String, String>());
        }
        QueuedFuture<R> queuedFuture = new QueuedFuture<>(task, executionContext, threadContext);
        queuedFuture.start(getExecutorService());

        return queuedFuture;
    }
}
