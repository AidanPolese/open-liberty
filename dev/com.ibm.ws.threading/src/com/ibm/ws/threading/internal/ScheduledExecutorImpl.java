/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.threading.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * This implementation is essentially a wrapper around an scheduled executor but defers execution to the default 
 * ExecutorService
 */
public final class ScheduledExecutorImpl extends ScheduledThreadPoolExecutor {
    private final static String threadGroupName = "Scheduled Executor Thread Group";

    private ExecutorService executor;

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void unsetExecutor(ExecutorService executor) {
        this.executor = null;
    }

    public ScheduledExecutorImpl() {
        super(1, new ThreadFactoryImpl("Scheduled Executor", threadGroupName));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        SchedulingRunnableFixedHelper<?> schedulingHelper = new SchedulingRunnableFixedHelper<Object>(false, command, this, initialDelay, period, unit);
        ScheduledFuture<?> schedFuture = schedule(schedulingHelper, initialDelay, unit);
        schedulingHelper.setScheduledFuture(schedFuture);
        return schedulingHelper;
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        SchedulingRunnableFixedHelper<?> schedulingHelper = new SchedulingRunnableFixedHelper<Object>(true, command, this, initialDelay, delay, unit);
        ScheduledFuture<?> schedFuture = schedule(schedulingHelper, initialDelay, unit);
        schedulingHelper.setScheduledFuture(schedFuture);
        return schedulingHelper;
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable r, RunnableScheduledFuture<V> task) {
        ExecutorService executorService = this.executor;
        // executor will be null after unsetExecutor is called on shutdown. Just return the task in this case. 
        if (this.executor == null)
            return task;

        return new SchedulingHelper<V>(Executors.callable(r, (V) null), task, executorService, getQueue());
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> c, RunnableScheduledFuture<V> task) {
        ExecutorService executorService = this.executor;
        // executor will be null after unsetExecutor is called on shutdown. Just return the task in this case.
        if (this.executor == null)
            return task;

        return new SchedulingHelper<V>(c, task, executorService, getQueue());
    }

}
