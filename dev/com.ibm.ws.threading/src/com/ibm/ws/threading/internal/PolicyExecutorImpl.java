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
import java.util.LinkedList;
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

    /**
     * Use this lock to make a consistent update to both maxConcurrency and maxConcurrencyConstraint,
     * and to maxQueueSize and maxQueueSizeConstraint.
     */
    private final Integer configLock = new Integer(0); // new instance required to avoid sharing

    private ExecutorService globalExecutor;

    private String identifier;

    private final boolean isServerConfigured;

    private int maxConcurrency;

    private final ReduceableSemaphore maxConcurrencyConstraint = new ReduceableSemaphore();

    private int maxQueueSize;

    final ReduceableSemaphore maxQueueSizeConstraint = new ReduceableSemaphore();

    private final AtomicLong maxWaitForEnqueue = new AtomicLong();

    final ConcurrentLinkedQueue<FutureTask<?>> queue = new ConcurrentLinkedQueue<FutureTask<?>>();

    private final AtomicReference<QueueFullAction> queueFullAction = new AtomicReference<QueueFullAction>();

    @SuppressWarnings("serial") // never serialized
    static class ReduceableSemaphore extends Semaphore {
        @Trivial
        private ReduceableSemaphore() {
            super(0);
        }

        @Override // to make visible
        public void reducePermits(int reduction) {
            super.reducePermits(reduction);
        }
    }

    /**
     * Policy executor state, which transitions in one direction only. See constants for possible states.
     */
    private final AtomicReference<State> state = new AtomicReference<State>(State.ACTIVE);

    @Trivial
    private static enum State {
        ACTIVE, // task submit/start/run all possible
        QUEUE_STOPPING, // queue is being disabled, submit might be possible, start/run still possible
        QUEUE_STOPPED, // task submit disallowed, start/run still possible
        TASKS_CANCELING, // task submit disallowed, start/run might be possible, queued and running tasks are being canceled
        TASKS_CANCELED, // task submit/start disallowed, waiting for all tasks to end
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
        maxConcurrencyConstraint.release(maxConcurrency = Integer.MAX_VALUE);
        maxQueueSizeConstraint.release(maxQueueSize = Integer.MAX_VALUE);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (isServerConfigured)
            throw new UnsupportedOperationException();

        // This method is optimized for the scenario where the user first invokes shutdownNow.
        // Absent that, polling will be used to wait for TASKS_CANCELED state to be reached
        // or QUEUE_STOPPED state to be reached with an empty queue, after which we can attempt
        // to obtain all of the maxConcurrency permits and transition to TERMINATED state.
        final long pollInterval = TimeUnit.MILLISECONDS.toNanos(500);

        timeout = unit.toNanos(timeout);

        for (long start = System.nanoTime(), waitTime = 0, remaining = timeout; //
                        waitTime == 0 || (remaining = timeout - waitTime) > 0; //
                        waitTime = System.nanoTime() - start) {
            State currentState = state.get();
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "awaitTermination", remaining, currentState);
            switch (currentState) {
                case TERMINATED:
                    return true;
                case QUEUE_STOPPED:
                case TASKS_CANCELING:
                case TASKS_CANCELED:
                    // Transition to TERMINATED state if no tasks in the queue and no policy tasks on global executor.
                    // TODO How do we avoid waiting for the entire timeout if the state transitions to TERMINATED right before we start waiting?
                    if (queue.isEmpty()) {
                        if (remaining > 0 ? maxConcurrencyConstraint.tryAcquire(maxConcurrency, remaining, unit) //
                                        : maxConcurrencyConstraint.tryAcquire(maxConcurrency)) {
                            State previous = state.getAndSet(State.TERMINATED);
                            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                                Tr.event(this, tc, "state: " + previous + " --> TERMINATED");
                            return true;
                        } else
                            return state.get() == State.TERMINATED; // one final chance
                    } else
                        TimeUnit.NANOSECONDS.sleep(remaining < pollInterval ? remaining : pollInterval);
                    continue;
                default:
                    TimeUnit.NANOSECONDS.sleep(remaining < pollInterval ? remaining : pollInterval);
            }
        }

        return false;
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
                if (maxConcurrencyConstraint.tryAcquire())
                    enqueueGlobal(new PolicyTask(this));
            } else if (state.get() == State.ACTIVE) {
                // TODO Reject, CallerRuns, or CallerRunsIfSameExecutor
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
                maxConcurrencyConstraint.release();

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(this, tc, "maxConcurrency permits available: " + maxConcurrencyConstraint.availablePermits());
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

        State currentState = state.get();
        switch (currentState) {
            case TERMINATED:
                return true;
            case QUEUE_STOPPED:
            case TASKS_CANCELING:
            case TASKS_CANCELED:
                // Transition to TERMINATED state if no tasks in the queue and no policy tasks on global executor
                if (queue.isEmpty() && maxConcurrencyConstraint.tryAcquire(maxConcurrency)) {
                    State previous = state.getAndSet(State.TERMINATED);
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                        Tr.event(this, tc, "state: " + previous + " --> TERMINATED");
                    return true;
                } else
                    return false;
            default:
                return false;
        }
    }

    @Override
    public PolicyExecutor maxConcurrency(int max) {
        if (isServerConfigured)
            throw new UnsupportedOperationException();

        if (max == -1)
            max = Integer.MAX_VALUE;
        else if (max < 1)
            throw new IllegalArgumentException(Integer.toString(max));

        synchronized (configLock) {
            int increase = max - maxConcurrency;
            if (increase > 0)
                maxConcurrencyConstraint.release(increase);
            else if (increase < 0)
                maxConcurrencyConstraint.reducePermits(-increase);
            maxConcurrency = max;
        }

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

        synchronized (configLock) {
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

    /**
     * Resubmit a policy task if any queued tasks remain.
     * Otherwise decrement the count against maxConcurrency.
     * TODO Should write a more efficient/optimal/accurate mechanism for rescheduling.
     *
     * @param policyTask policy executor task to resubmit.
     * @param executeTaskOnPolicyThread indicates if the policy thread ran a queued task,
     *            as opposed to finding nothing in the queue.
     */
    void resubmit(PolicyTask policyTask, boolean executedTaskOnPolicyThread) {
        if (executedTaskOnPolicyThread && !queue.isEmpty())
            enqueueGlobal(policyTask);
        else {
            maxConcurrencyConstraint.release();
            int available = maxConcurrencyConstraint.availablePermits();

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "maxConcurrency permits available: " + available);

            // If this was the only policy task left, check once again to ensure there are still no items left in the queue.
            // Otherwise a race condition could leave a task unexecuted.
            if (maxConcurrency == available && !queue.isEmpty() && maxConcurrencyConstraint.tryAcquire())
                enqueueGlobal(policyTask);
        }
    }

    @Override
    public void shutdown() {
        if (isServerConfigured)
            throw new UnsupportedOperationException();

        // Permanently update our configuration such that no more task submits are accepted
        if (state.compareAndSet(State.ACTIVE, State.QUEUE_STOPPING)) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                Tr.event(this, tc, "state: ACTIVE --> QUEUE_STOPPING");

            maxWaitForEnqueue.set(-1); // make attempted task submissions fail immediately

            synchronized (configLock) {
                maxQueueSize = 0;
                maxQueueSizeConstraint.drainPermits();
                maxQueueSizeConstraint.reducePermits(Integer.MAX_VALUE);
            }

            if (state.compareAndSet(State.QUEUE_STOPPING, State.QUEUE_STOPPED))
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                    Tr.event(this, tc, "state: QUEUE_STOPPING --> QUEUE_STOPPED");
        } else
            while (state.get() == State.QUEUE_STOPPING) {
                // Await completion of other thread that concurrently invokes shutdown.
                Thread.yield();
            }
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();

        LinkedList<Runnable> queuedTasks = new LinkedList<Runnable>();

        if (state.compareAndSet(State.QUEUE_STOPPED, State.TASKS_CANCELING)) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                Tr.event(this, tc, "state: QUEUE_STOPPED --> TASKS_CANCELING");

            // Remove all queued tasks. The maxQueueSizeConstraint should prevent queueing more,
            // apart from a timing window where a task is being scheduled during shutdown. TODO
            for (FutureTask<?> t = queue.poll(); t != null; t = queue.poll())
                queuedTasks.add(t); // TODO get the actual Runnable/Callable, not FutureTask

            // Cancel in-progress tasks
            // TODO track in-progress tasks so that we can cancel

            if (state.compareAndSet(State.TASKS_CANCELING, State.TASKS_CANCELED))
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                    Tr.event(this, tc, "state: TASKS_CANCELING --> TASKS_CANCELED");
        } else {
            // Await completion of other thread that concurrently invokes shutdownNow.
            // TODO removing all queued tasks and canceling all policy tasks is not trivial, should a better mechanism be used to wait?
            while (state.get() == State.TASKS_CANCELING)
                Thread.yield();
        }

        return queuedTasks;
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
