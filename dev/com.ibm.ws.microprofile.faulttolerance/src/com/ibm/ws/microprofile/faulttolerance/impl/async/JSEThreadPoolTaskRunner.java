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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.wsspi.threadcontext.WSContextService;

public class JSEThreadPoolTaskRunner<R> extends PolicyExecutorTaskRunner<R> {

    private ThreadPoolExecutor executorService;

    public JSEThreadPoolTaskRunner(BulkheadPolicy bulkheadPolicy, WSContextService contextService) {
        super(bulkheadPolicy, contextService);
    }

    @Override
    protected ExecutorService getExecutorService() {
        if (this.executorService == null) {
            ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(getQueueSize(), true);
            this.executorService = new java.util.concurrent.ThreadPoolExecutor(getMaxThreads(), getMaxThreads(), 0l, TimeUnit.MILLISECONDS, queue, Executors.defaultThreadFactory());
        }
        return this.executorService;
    }
}
