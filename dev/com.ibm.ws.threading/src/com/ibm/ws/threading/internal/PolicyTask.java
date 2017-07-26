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
        // TODO trace each execution, including
        // TODO poll with timeout if keepAlive configured
        for (FutureTask<?> nextTask = policyExecutor.queue.poll(); nextTask != null; nextTask = policyExecutor.queue.poll())
            try {
                nextTask.run();
            } catch (Throwable x) {
                // TODO can this even happen?
            } finally {
                // TODO for tracking purposes, notify global executor that a task has completed
                // TODO additional processing to reset thread state
            }

        int numPolicyTasks = policyExecutor.numTasksOnGlobal.decrementAndGet();

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(this, tc, "Policy tasks for " + policyExecutor + " reduced to " + numPolicyTasks);
    }
}
