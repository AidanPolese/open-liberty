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
     * Describes the action to take when a task is submitted but there are no positions
     * available for it in the queue after having exceeded the maxWaitForEnqueue.
     */
    public enum QueueFullAction {
        /**
         * Reject submission of the task.
         */
        Abort,

        /**
         * The thread invoking submit (or execute or invoke*) attempts to run the task
         * before returning control.
         * Caution: this may be unwanted when maxConcurrency is specified because it allows
         * additional execution beyond what is limited by maxConcurrency.
         */
        CallerRuns,

        /**
         * If the thread on which submit (or executor or invoke*) is invoked can be identified
         * as already running a task that was submitted to the same policy executor, then
         * it attempts to run the task before returning control.
         * Otherwise, submission of the task is rejected.
         */
        CallerRunsIfSameExecutor
    }

    /**
     * Specifies the maximum number of tasks from this policy executor that can be running
     * at any given point in time. The default maxConcurrency is Integer.MAX_VALUE.
     * TODO: update with discussion of how dynamic config update is handled once supported
     *
     * @param max maximum concurrency.
     * @return the executor.
     * @throws IllegalArgumentException if value is not positive or -1 (which means Integer.MAX_VALUE).
     * @throws IllegalStateException if the executor has been shut down.
     * @throws UnsupportedOperationException if invoked on a policyExecutor instance created from server configuration.
     */
    PolicyExecutor maxConcurrency(int max);

    /**
     * Specifies the maximum number of submitted tasks that can be queued for execution.
     * As tasks are started or canceled, they are removed from the queue. When the queue is
     * at capacity and another task is submitted, the rejectAction is applied.
     * Applications that submit many tasks over a short period of time might want to use
     * a maximum queue size that is at least as large as the maximum concurrency.
     * The default maxQueueSize is Integer.MAX_VALUE.
     * TODO: update with discussion of how dynamic config update is handled once supported
     *
     * @param max capacity of the task queue.
     * @return the executor.
     * @throws IllegalArgumentException if value is not positive or -1 (which means Integer.MAX_VALUE).
     * @throws IllegalStateException if the executor has been shut down.
     * @throws UnsupportedOperationException if invoked on a policyExecutor instance created from server configuration.
     */
    PolicyExecutor maxQueueSize(int max);

    /**
     * Specifies the maximum number of milliseconds to wait for queueing a submitted task.
     * If unable to queue the task within this interval, the task submission is subject to
     * the queueFullAction. A value of 0 indicates to not wait at all, in which case, if there
     * is not a queue position available, the queueFullAction is immediately applied.
     * The default maxWaitForEnqueue is 0.
     * TODO: update with discussion of how dynamic config update is handled once supported
     *
     * @param ms maximum number of milliseconds to wait when attempting to qeueue a submitted task.
     * @return the executor.
     * @throws IllegalArgumentException if value is negative.
     * @throws IllegalStateException if the executor has been shut down.
     * @throws UnsupportedOperationException if invoked on a policyExecutor instance created from server configuration.
     */
    PolicyExecutor maxWaitForEnqueue(long ms);

    /**
     * Specifies the action to take when a task is submitted but there are no queue positions
     * available. Refer to the descriptions of the values possible in the QueueFullAction enumeration.
     * The default queueFullAction depends on the maxConcurrency.
     * If maxConcurrency is a positive integer less than Integer.MAX_VALUE, then the default is CallerRunsIfSameExecutor? or Abort? TODO
     * Otherwise, the default is CallerRuns? TODO
     * TODO: update with discussion of how dynamic config update is handled once supported
     *
     * @param action the action to take.
     * @return the executor.
     * @throws IllegalStateException if the executor has been shut down.
     * @throws UnsupportedOperationException if invoked on a policyExecutor instance created from server configuration.
     */
    PolicyExecutor queueFullAction(QueueFullAction action);
}
