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
package com.ibm.ws.threading;

import java.util.concurrent.ExecutorService;

/**
 * <p>Policy executors are backed by the Liberty global thread pool, but allow
 * concurrency constraints and various queue attributes to be controlled independently
 * of the global thread pool.</p>
 */
public interface PolicyExecutor extends ExecutorService {
    /**
     * Specifies the maximum number of tasks from this policy executor that can be running
     * at any given point in time.
     * TODO: update with discussion of how dynamic config update is handled once supported
     *
     * @param max maximum concurrency.
     * @return the executor.
     * @throws IllegalArgumentException if value is not positive or -1 (which means Integer.MAX_VALUE).
     * @throws UnsupportedOperationException if invoked on a policyExecutor instance created from server configuration.
     */
    PolicyExecutor maxConcurrency(int max);

    /**
     * Specifies the maximum number of submitted tasks that can be queued for execution.
     * As tasks are started or canceled, they are removed from the queue. When the queue is
     * at capacity and another task is submitted, the rejectAction is applied.
     * Applications that submit many tasks over a short period of time might want to use
     * a maximum queue size that is at least as large as the maximum concurrency.
     * TODO: update with discussion of how dynamic config update is handled once supported
     *
     * @param max capacity of the task queue.
     * @return the executor.
     * @throws IllegalArgumentException if value is not positive or -1 (which means Integer.MAX_VALUE).
     * @throws UnsupportedOperationException if invoked on a policyExecutor instance created from server configuration.
     */
    PolicyExecutor maxQueueSize(int max);
}
