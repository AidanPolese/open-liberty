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
package com.ibm.ws.threading.internal;

import java.util.concurrent.FutureTask;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * Policy executor tasks run on the global thread pool.
 * Their role is to run tasks that are queued up on the policy executor.
 */
public class PolicyTask implements Runnable {
    private static final TraceComponent tc = Tr.register(PolicyTask.class);

    private final PolicyExecutorImpl policyExecutor;

    /**
     * Constructor for declarative services.
     * The majority of initialization logic should be performed in the activate method, not here.
     */
    public PolicyTask(PolicyExecutorImpl policyExecutor) {
        this.policyExecutor = policyExecutor;
    }

    @Override
    public void run() {
        boolean trace = TraceComponent.isAnyTracingEnabled();

        FutureTask<?> nextTask;
        do {
            nextTask = policyExecutor.queue.poll();
            if (nextTask == null)
                break;
            else // TODO do this earlier for cancel-from-queue and only do here if we will actually run the task
                 // TODO It is only safe to release a permit after poll (remove from queue) so cancel would need to remove
                policyExecutor.maxQueueSizeConstraint.release();
        } while (nextTask.isCancelled());

        if (nextTask != null)
            try {
                if (trace && tc.isDebugEnabled())
                    Tr.debug(this, tc, "starting " + nextTask);

                nextTask.run();

                if (trace && tc.isDebugEnabled())
                    Tr.debug(this, tc, "completed " + nextTask);
            } catch (Throwable x) {
                if (trace && tc.isDebugEnabled())
                    Tr.debug(this, tc, "completed " + nextTask, x);
            }
        // TODO If we run multiple tasks in sequence on this thread,
        // 1) for tracking purposes, notify global executor that a task has completed
        // 2) additional processing to reset thread state

        policyExecutor.resubmit(this, nextTask != null);
    }
}
