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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
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

    private final ReduceableSemaphore maxQueueSizeConstraint = new ReduceableSemaphore();

    private final AtomicLong maxWaitForEnqueue = new AtomicLong();

    private final ConcurrentLinkedQueue<PolicyTaskFuture<?>> queue = new ConcurrentLinkedQueue<PolicyTaskFuture<?>>();

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
     * Tasks that are running on policy executor threads.
     * This is only populated & used for policy executors that are programmatically created,
     * because it is needed only for the life cycle methods which are unavailable to
     * server-configured policy executors.
     */
    private final Set<PolicyTaskFuture<?>> running = Collections.newSetFromMap(new ConcurrentHashMap<PolicyTaskFuture<?>, Boolean>());

    /**
     * Policy executor state, which transitions in one direction only. See constants for possible states.
     */
    private final AtomicReference<State> state = new AtomicReference<State>(State.ACTIVE);

    @Trivial
    private static enum State {
        ACTIVE, // task submit/start/run all possible
        ENQUEUE_STOPPING, // enqueue is being disabled, submit might be possible, start/run still possible
        ENQUEUE_STOPPED, // task submit disallowed, start/run still possible
        TASKS_CANCELING, // task submit disallowed, start/run might be possible, queued and running tasks are being canceled
        TASKS_CANCELED, // task submit/start disallowed, waiting for all tasks to end
        TERMINATED // task submit/start/run all disallowed
    }

    /**
     * A wrapper for FutureTask that allows us to immediately free up a queue position upon cancel
     * and ensures that we only provide implementation of the Future methods rather than all methods
     * of FutureTask to the invoker.
     *
     * @param <T> return type of underlying task.
     */
    private class PolicyTaskFuture<T> implements Future<T> {
        private final FutureTask<T> futureTask;
        private final Object task;
        private final int hash;

        public PolicyTaskFuture(Callable<T> task) {
            this.futureTask = new FutureTask(task);
            this.task = task;
            this.hash = task.hashCode();
        }

        public PolicyTaskFuture(Runnable task, T result) {
            this.futureTask = new FutureTask(task, result);
            this.task = task;
            this.hash = task.hashCode();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean canceled = futureTask.cancel(mayInterruptIfRunning);
            if (canceled && queue.remove(this))
                maxQueueSizeConstraint.release();
            return canceled;
        }

        @Override
        public T get() throws ExecutionException, InterruptedException {
            return futureTask.get();
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
            return futureTask.get(timeout, unit);
        }

        @Trivial
        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean isCancelled() {
            return futureTask.isCancelled();
        }

        @Override // to auto-add trace
        public boolean isDone() {
            return futureTask.isDone();
        }

        @Trivial
        @Override
        public String toString() {
            return new StringBuilder("PolicyTaskFuture@").append(Integer.toHexString(hash)).append(' ').append(task).append(" on ").append(identifier).toString();
        }
    }

    /**
     * Polling tasks run on the global thread pool.
     * Their role is to run tasks that are queued up on the policy executor.
     */
    private class PollingTask implements Runnable {
        @Override
        public void run() {
            PolicyTaskFuture<?> next;
            do {
                next = queue.poll();
                if (next == null)
                    break;
                else
                    maxQueueSizeConstraint.release();
            } while (next.isCancelled());

            if (next != null)
                runTask(next);

            // Release a maxConcurrency permit and do not reschedule if there are no tasks on the queue
            if (next == null || queue.isEmpty()) {
                maxConcurrencyConstraint.release();
                int available = maxConcurrencyConstraint.availablePermits();

                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(this, tc, "maxConcurrency permits available: " + available);

                // If this was the only polling task left, check once again to ensure there are still no items left in the queue.
                // Otherwise a race condition could leave a task unexecuted.
                if (maxConcurrency == available && !queue.isEmpty() && maxConcurrencyConstraint.tryAcquire())
                    enqueueGlobal(PollingTask.this);
            } else // There are still tasks to run, so reschedule the polling task to the global executor
                enqueueGlobal(PollingTask.this);
        }
    }

    /**
     * Utility class to convert a Callable to a Runnable, which is necessary for an implementation of
     * ExecutorService.shutdownNow to validly return as Runnables, which is required by the method signature,
     * a list of tasks that didn't start, where some of the tasks are Callable, not Runnable.
     */
    private static class RunnableFromCallable implements Runnable {
        private final Callable<?> callable;

        private RunnableFromCallable(Callable<?> callable) {
            this.callable = callable;
        }

        @Override
        public void run() {
            try {
                callable.call();
            } catch (RuntimeException x) {
                throw x;
            } catch (Exception x) {
                throw new RuntimeException(x);
            }
        }
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
        // or ENQUEUE_STOPPED state to be reached with an empty queue, after which we can attempt
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
                case ENQUEUE_STOPPED:
                case TASKS_CANCELING:
                case TASKS_CANCELED:
                    // Transition to TERMINATED state if no tasks in the queue and no polling tasks on global executor.
                    if (queue.isEmpty()) {
                        if (remaining > 0 ? maxConcurrencyConstraint.tryAcquire(maxConcurrency, remaining < pollInterval ? remaining : pollInterval, TimeUnit.NANOSECONDS) //
                                        : maxConcurrencyConstraint.tryAcquire(maxConcurrency)) {
                            State previous = state.getAndSet(State.TERMINATED);
                            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                                Tr.event(this, tc, "state: " + previous + " --> TERMINATED");
                            return true;
                        }
                    } else
                        TimeUnit.NANOSECONDS.sleep(remaining < pollInterval ? remaining : pollInterval);
                    continue;
                default:
                    TimeUnit.NANOSECONDS.sleep(remaining < pollInterval ? remaining : pollInterval);
            }
        }

        return state.get() == State.TERMINATED; // one final chance, in case another thread has transitioned the state while we waited the last time
    }

    /**
     * Attempt to add a task to the policy executor's queue, following the configured
     * behavior for waiting and rejecting vs running locally if the queue is at capacity.
     * As needed, ensure that polling tasks are submitted to the global executor to process
     * the queued up tasks.
     *
     * @param policyTaskFuture submitted task and its Future.
     * @throws RejectedExecutionException if the task is rejected rather than being queued.
     */
    @Trivial // because invoker is traced
    private void enqueue(PolicyTaskFuture<?> policyTaskFuture) {
        try {
            long wait = maxWaitForEnqueue.get();
            if (wait <= 0 ? maxQueueSizeConstraint.tryAcquire() : maxQueueSizeConstraint.tryAcquire(wait, TimeUnit.MILLISECONDS)) {
                queue.offer(policyTaskFuture);
                if (maxConcurrencyConstraint.tryAcquire())
                    enqueueGlobal(new PollingTask());
            } else if (state.get() == State.ACTIVE) {
                // TODO Reject, CallerRuns, or CallerRunsIfSameExecutor
                throw new RejectedExecutionException();
            } else
                throw new RejectedExecutionException(getStateMessage());

            // Check if shutdown occurred since acquiring the permit to enqueue, and if so, try to remove the queued task
            if (state.get() != State.ACTIVE && queue.remove(policyTaskFuture))
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
     * Queue a polling task to the global executor.
     * Prereq: numTasksOnGlobal must already reflect the task being queued to global.
     * If unsuccessful in queueing to global, this method decrements numTasksOnGlobal.
     *
     * @param pollingTask task that can execute tasks that are queued to the policy executor.
     * @return Future for the tasks queued to global. Null if not queued to global.
     */
    Future<?> enqueueGlobal(PollingTask pollingTask) {
        Future<?> future = null;
        try {
            future = globalExecutor.submit(pollingTask);
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
        enqueue(new PolicyTaskFuture<Void>(command, null));
    }

    @Trivial
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
            case ENQUEUE_STOPPED:
            case TASKS_CANCELING:
            case TASKS_CANCELED:
                // Transition to TERMINATED state if no tasks in the queue and no polling tasks on global executor
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
     * Invoked by the policy executor thread to run a task.
     *
     * @param task the task.
     */
    @Trivial // do the tracing ourselves to ensure exception is included
    void runTask(PolicyTaskFuture<?> future) {
        final boolean trace = TraceComponent.isAnyTracingEnabled();
        if (trace && tc.isEntryEnabled())
            Tr.entry(this, tc, "runTask", future, future.task);

        Throwable failure = null;
        try {
            if (!isServerConfigured)
                running.add(future); // intentionally done before checking state to avoid missing cancels on shutdownNow

            State currentState = state.get();
            if (currentState == State.ACTIVE || currentState == State.ENQUEUE_STOPPING || currentState == State.ENQUEUE_STOPPED)
                future.futureTask.run();
            else {
                if (trace && tc.isDebugEnabled())
                    Tr.debug(this, tc, "Cancel task due to policy executor state " + currentState);
                future.cancel(false);
            }
        } catch (Throwable x) {
            failure = x;
        } finally {
            if (!isServerConfigured)
                running.remove(future);
        }

        if (trace && tc.isEntryEnabled())
            Tr.exit(this, tc, "runTask", failure);
    }

    @Override
    public void shutdown() {
        if (isServerConfigured)
            throw new UnsupportedOperationException();

        // Permanently update our configuration such that no more task submits are accepted
        if (state.compareAndSet(State.ACTIVE, State.ENQUEUE_STOPPING)) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                Tr.event(this, tc, "state: ACTIVE --> ENQUEUE_STOPPING");

            maxWaitForEnqueue.set(-1); // make attempted task submissions fail immediately

            synchronized (configLock) {
                maxQueueSize = 0;
                maxQueueSizeConstraint.drainPermits();
                maxQueueSizeConstraint.reducePermits(Integer.MAX_VALUE);
            }

            if (state.compareAndSet(State.ENQUEUE_STOPPING, State.ENQUEUE_STOPPED))
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled())
                    Tr.event(this, tc, "state: ENQUEUE_STOPPING --> ENQUEUE_STOPPED");
        } else
            while (state.get() == State.ENQUEUE_STOPPING) {
                // Await completion of other thread that concurrently invokes shutdown.
                Thread.yield();
            }
    }

    @Override
    public List<Runnable> shutdownNow() {
        final boolean trace = TraceComponent.isAnyTracingEnabled();

        shutdown();

        LinkedList<Runnable> queuedTasks = new LinkedList<Runnable>();

        if (state.compareAndSet(State.ENQUEUE_STOPPED, State.TASKS_CANCELING)) {
            if (trace && tc.isEventEnabled())
                Tr.event(this, tc, "state: ENQUEUE_STOPPED --> TASKS_CANCELING");

            // Remove and cancel all queued tasks. The maxQueueSizeConstraint should prevent queueing more,
            // apart from a timing window where a task is being scheduled during shutdown. TODO
            for (PolicyTaskFuture<?> f = queue.poll(); f != null; f = queue.poll()) {
                // It would be wrong to return FutureTask as the Runnable.
                // Presumably the list of tasks that didn't run is being returned so that the invoker can decide what to do
                // with them, which includes having the option to run them, which is not an option for a canceled FutureTask.
                if (f.task instanceof Runnable)
                    queuedTasks.add((Runnable) f.task);
                else
                    queuedTasks.add(new RunnableFromCallable((Callable<?>) f.task));
            }

            // Cancel tasks that are running
            for (Iterator<PolicyTaskFuture<?>> it = running.iterator(); it.hasNext();) {
                PolicyTaskFuture<?> f = it.next();
                boolean canceled = f.cancel(true);
                if (trace && tc.isDebugEnabled())
                    Tr.debug(this, tc, "canceled?", f, f.task, canceled);
            }

            if (state.compareAndSet(State.TASKS_CANCELING, State.TASKS_CANCELED))
                if (trace && tc.isEventEnabled())
                    Tr.event(this, tc, "state: TASKS_CANCELING --> TASKS_CANCELED");
        } else {
            // Await completion of other thread that concurrently invokes shutdownNow.
            // TODO removing all queued tasks and canceling all running tasks is not trivial, should a better mechanism be used to wait?
            while (state.get() == State.TASKS_CANCELING)
                Thread.yield();
        }

        return queuedTasks;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        PolicyTaskFuture<T> policyTaskFuture = new PolicyTaskFuture<T>(task);
        enqueue(policyTaskFuture);
        return policyTaskFuture;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        PolicyTaskFuture<T> policyTaskFuture = new PolicyTaskFuture<T>(task, result);
        enqueue(policyTaskFuture);
        return policyTaskFuture;
    }

    @Override
    public Future<?> submit(Runnable task) {
        PolicyTaskFuture<?> policyTaskFuture = new PolicyTaskFuture<Void>(task, null);
        enqueue(policyTaskFuture);
        return policyTaskFuture;
    }
}
