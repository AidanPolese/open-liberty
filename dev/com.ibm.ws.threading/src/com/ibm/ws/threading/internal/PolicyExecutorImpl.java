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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Policy executors are backed by the Liberty global thread pool,
 * but allow concurrency constraints and various queue attributes
 * to be controlled independently of the global thread pool.
 */
public class PolicyExecutorImpl implements ExecutorService {
    private static final TraceComponent tc = Tr.register(PolicyExecutorImpl.class);

    private final boolean allowLifeCycleOperations;

    private static final PrivilegedAction<ExecutorService> getGlobalExecutor = new PrivilegedAction<ExecutorService>() {
        @Override
        @Trivial
        public ExecutorService run() {
            BundleContext bc = FrameworkUtil.getBundle(getClass()).getBundleContext();
            return bc.getService(bc.getServiceReference(ExecutorService.class));
        }
    };

    private ExecutorService globalExecutor;

    private int maxConcurrency;

    final AtomicInteger numTasksOnGlobal = new AtomicInteger();

    BlockingQueue<FutureTask<?>> queue;

    /**
     * Constructor for declarative services.
     * The majority of initialization logic should be performed in the activate method, not here.
     */
    public PolicyExecutorImpl() {
        allowLifeCycleOperations = false;
    }

    /**
     * Constructor for PolicyExecutorBuilder.
     *
     * @param maxConcurrency maximum number of tasks that can be running
     * @param maxQueueSize maximum size of task queue
     */
    public PolicyExecutorImpl(int maxConcurrency, int maxQueueSize) {
        allowLifeCycleOperations = true;
        globalExecutor = System.getSecurityManager() == null ? getGlobalExecutor.run() : AccessController.doPrivileged(getGlobalExecutor);
        this.maxConcurrency = maxConcurrency;
        queue = new LinkedBlockingQueue<FutureTask<?>>(maxQueueSize);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempt to add a task to the policy executor's queue, following the configured
     * behavior for waiting and rejecting vs running locally if the queue is at capacity.
     * As needed, ensure that policy tasks are submitted to the global executor to process
     * the queued up tasks.
     *
     * @param futureTask submitted task and its Future.
     * @throws RejectedExecutionException if the task is rejected rather than being queued.
     */
    @Trivial // because invoker is traced
    private void enqueue(FutureTask<?> futureTask) {
        try {
            if (queue.offer(futureTask)) { // TODO apply maxWaitForEnqueue if configured
                if (incrementNumTasksOnGlobal()) {
                    Future<?> policyTaskFuture = null;
                    try {
                        policyTaskFuture = globalExecutor.submit(new PolicyTask(this));
                    } finally {
                        if (policyTaskFuture == null)
                            numTasksOnGlobal.decrementAndGet();
                    }
                }
            } else // TODO reject or callerRuns
                throw new RejectedExecutionException();
        } catch (RuntimeException x) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "enqueue", x);
            throw x;
        } catch (Error x) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "enqueue", x);
            throw x;
        }
    }

    @Override
    public void execute(Runnable command) {
        enqueue(new FutureTask<Void>(command, null));
    }

    /**
     * Increment our counter of tasks submitted to the global executor if doing so
     * does not exceed maximum concurrency.
     *
     * @return true if incremented, otherwise false.
     */
    private boolean incrementNumTasksOnGlobal() {
        for (int n = numTasksOnGlobal.get(); n < maxConcurrency; n = numTasksOnGlobal.get())
            if (numTasksOnGlobal.compareAndSet(n, n + 1))
                return true;
        return false;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTerminated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> futureTask = new FutureTask<T>(task);
        enqueue(futureTask);
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        FutureTask<T> futureTask = new FutureTask<T>(task, result);
        enqueue(futureTask);
        return futureTask;
    }

    @Override
    public Future<?> submit(Runnable task) {
        FutureTask<?> futureTask = new FutureTask<Void>(task, null);
        enqueue(futureTask);
        return futureTask;
    }
}
