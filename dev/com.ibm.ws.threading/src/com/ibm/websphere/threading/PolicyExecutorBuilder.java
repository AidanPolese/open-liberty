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
package com.ibm.websphere.threading;

import java.util.concurrent.ExecutorService;

import com.ibm.ws.threading.internal.PolicyExecutorImpl;

/**
 * <p>Builder for policy executors. Policy executors are backed by the Liberty
 * global thread pool, but allow concurrency constraints and various queue
 * attributes to be controlled independently of the global thread pool.
 * For example, to build a policy executor that allows at most 3 tasks to
 * be active at any given point and can queue up to 20 tasks,</p>
 * <code>
 * executor = new PolicyExecutorBuilder().maxConcurrency(3).maxQueueSize(20).build();
 * </code>
 */
public class PolicyExecutorBuilder {
    private int maxConcurrency = 10; // TODO what should the defaults be? And then include in JavaDoc.
    private int maxQueueSize = 30;

    public ExecutorService build() {
        return new PolicyExecutorImpl(maxConcurrency, maxQueueSize);
    }

    /**
     * Specifies the maximum number of threads allowed to run executor tasks
     * at any given point in time.
     *
     * @param max maximum concurrency.
     * @return the builder.
     */
    public PolicyExecutorBuilder maxConcurrency(int max) {
        if (max < 1)
            throw new IllegalArgumentException(Integer.toString(max));
        maxConcurrency = max;
        return this;
    }

    /**
     * Specifies the maximum number of tasks that can queue up at any given
     * point in time. Tasks queue up before starting, so ensure the maximum
     * queue size is at least as large as the maximum concurrency.
     *
     * @param max maximum number of queued tasks.
     * @return the builder.
     */
    public PolicyExecutorBuilder maxQueueSize(int max) {
        if (max < 1)
            throw new IllegalArgumentException(Integer.toString(max));
        maxQueueSize = max;
        return this;
    }
}
