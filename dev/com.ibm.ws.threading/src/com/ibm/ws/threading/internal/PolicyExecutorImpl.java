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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.threading.PolicyExecutor;

/**
 * Policy executors are backed by the Liberty global thread pool,
 * but allow concurrency constraints and various queue attributes
 * to be controlled independently of the global thread pool.
 */
public class PolicyExecutorImpl implements PolicyExecutor {
    private static final TraceComponent tc = Tr.register(PolicyExecutorImpl.class);

    private ExecutorService globalExecutor;

    private String identifier;

    private final boolean isServerConfigured;

    private final AtomicInteger maxConcurrency = new AtomicInteger(Integer.MAX_VALUE);

    private int maxQueueSize;

    final ReduceableSemaphore maxQueueSizeConstraint = new ReduceableSemaphore();

    /**
     * This lock is for making a consistent update to both maxQueueSize and maxQueueSizeConstraint
     */
    private final Integer maxQueueSizeLock = new Integer(0); // new instance required to avoid sharing

    private final AtomicLong maxWaitForEnqueue = new AtomicLong();

    final AtomicInteger numTasksOnGlobal = new AtomicInteger();

    final ConcurrentLinkedQueue<FutureTask<?>> queue = new ConcurrentLinkedQueue<FutureTask<?>>();

    private final AtomicReference<QueueFullAction> queueFullAction = new AtomicReference<QueueFullAction>();

    @SuppressWarnings("serial") // never serialized
    static class ReduceableSemaphore extends Semaphore {
        private ReduceableSemaphore() {
            super(0);
        }

        @Override // to make visible
        public void reducePermits(int reduction) {
            super.reducePermits(reduction);
        }
    }

    private final AtomicReference<State> state = new AtomicReference<State>(State.ACTIVE);

    private static enum State {
        ACTIVE, // task submit/start/run all possible
        STOPPING, // task submit disallowed, start/run still possible
        TERMINATING, // task submit/start disallowed, interrupts sent to running tasks, waiting for all tasks to end
        TERMINATED // task submit/start/run all disallowed
    }

    /**
     * Constructor for declarative services.
     * The majority of initialization logic should be performed in the activate method, not here.
     */
    public PolicyExecutorImpl() {
        isServerConfigured = true;
    }

    /**
     * This constructor is used by PolicyExecutorProvider.
     *
     * @param globalExecutor the Liberty global executor, which was obtained by the PolicyExecutorProvider via declarative services.
     * @param identifier unique identifier for this instance, to be used for monitoring and problem determination.
     */
    public PolicyExecutorImpl(ExecutorService globalExecutor, String identifier) {
        isServerConfigured = false;
        this.globalExecutor = globalExecutor;
        this.identifier = "PolicyExecutorProvider-" + identifier;
        maxQueueSizeConstraint.release(maxQueueSize = Integer.MAX_VALUE);
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
            long wait = maxWaitForEnqueue.get();
            if (wait <= 0 ? maxQueueSizeConstraint.tryAcquire() : maxQueueSizeConstraint.tryAcquire(wait, TimeUnit.MILLISECONDS)) {
                queue.offer(futureTask);
                if (incrementNumTasksOnGlobal())
                    enqueueGlobal(new PolicyTask(this));
            } else if (state.get() == State.ACTIVE) {
                // TODO Reject, CallerRuns, or
                throw new RejectedExecutionException();
            } else
                throw new RejectedExecutionException(getStateMessage());
        } catch (InterruptedException x) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "enqueue", x);
            throw new RejectedExecutionException(x);
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

    /**
     * Queue a policy task to the global executor.
     * Prereq: numTasksOnGlobal must already reflect the task being queued to global.
     * If unsuccessful in queueing to global, this method decrements numTasksOnGlobal.
     *
     * @param policyTask task that can execute tasks that are queued to the policy executor.
     * @return Future for the tasks queued to global. Null if not queued to global.
     */
    Future<?> enqueueGlobal(PolicyTask policyTask) {
        Future<?> future = null;
        try {
            future = globalExecutor.submit(policyTask);
        } finally {
            if (future == null) {
                int numPolicyTasks = numTasksOnGlobal.decrementAndGet();

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(this, tc, "Policy tasks for " + this + " reduced to " + numPolicyTasks);
            }
        }
        return future;
    }

    @Override
    public void execute(Runnable command) {
        enqueue(new FutureTask<Void>(command, null));
    }

    private String getStateMessage() {
        return state.toString(); // TODO NLS message
    }

    /**
     * Increment our counter of tasks submitted to the global executor if doing so
     * does not exceed maximum concurrency.
     *
     * @return true if incremented, otherwise false.
     */
    boolean incrementNumTasksOnGlobal() {
        int max = maxConcurrency.get();
        for (int n = numTasksOnGlobal.get(); n < max; n = numTasksOnGlobal.get())
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
        if (isServerConfigured)
            throw new UnsupportedOperationException();
        else
            return state.get() != State.ACTIVE;
    }

    @Override
    public boolean isTerminated() {
        if (isServerConfigured)
            throw new UnsupportedOperationException();
        else
            return state.get() == State.TERMINATED;
    }

    @Override
    public PolicyExecutor maxConcurrency(int max) {
        if (isServerConfigured)
            throw new UnsupportedOperationException();

        if (max == -1)
            max = Integer.MAX_VALUE;
        else if (max < 1)
            throw new IllegalArgumentException(Integer.toString(max));

        maxConcurrency.set(max);

        return this;
    }

    @Override
    public PolicyExecutor maxQueueSize(int max) {
        if (isServerConfigured)
            throw new UnsupportedOperationException();

        if (max == -1)
            max = Integer.MAX_VALUE;
        else if (max < 1)
            throw new IllegalArgumentException(Integer.toString(max));

        synchronized (maxQueueSizeLock) {
            int increase = max - maxQueueSize;
            if (increase > 0)
                maxQueueSizeConstraint.release(increase);
            else if (increase < 0)
                maxQueueSizeConstraint.reducePermits(-increase);
            maxQueueSize = max;
        }

        return this;
    }

    @Override
    public PolicyExecutor maxWaitForEnqueue(long ms) {
        if (isServerConfigured)
            throw new UnsupportedOperationException();

        if (ms < 0)
            throw new IllegalArgumentException(Long.toString(ms));

        for (long current = maxWaitForEnqueue.get(); current != -1; current = maxWaitForEnqueue.get())
            if (maxWaitForEnqueue.compareAndSet(current, ms))
                return this;

        throw new IllegalStateException(getStateMessage());
    }

    @Override
    public PolicyExecutor queueFullAction(QueueFullAction action) {
        if (isServerConfigured)
            throw new UnsupportedOperationException();

        queueFullAction.set(action);

        return this;
    }

    @Override
    public void shutdown() {
        if (isServerConfigured)
            throw new UnsupportedOperationException();

        if (state.compareAndSet(State.ACTIVE, State.STOPPING)) {
            stopAcceptingSubmits();
            // TODO transition past STOPPING state as tasks complete
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    /**
     * Common implementation between shutdown and shutdownNow to permanently
     * update our configuration such that no more task submits are accepted.
     */
    private void stopAcceptingSubmits() {
        maxWaitForEnqueue.set(-1); // make attempted task submissions fail immediately

        synchronized (maxQueueSizeLock) {
            maxQueueSize = 0;
            maxQueueSizeConstraint.drainPermits();
            maxQueueSizeConstraint.reducePermits(Integer.MAX_VALUE);
        }
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
