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
package web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.RejectedExecutionException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;

import org.junit.Test;

import com.ibm.ws.threading.PolicyExecutor;
import com.ibm.ws.threading.PolicyExecutor.QueueFullAction;
import com.ibm.ws.threading.PolicyExecutorProvider;

import componenttest.annotation.AllowedFFDC;
import componenttest.annotation.ExpectedFFDC;

import componenttest.app.FATServlet;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/PolicyExecutorServlet")
public class PolicyExecutorServlet extends FATServlet {
    // Maximum number of nanoseconds to wait for a task to complete
    static final long TIMEOUT_NS = TimeUnit.MINUTES.toNanos(2);

    @Resource(lookup = "test/TestPolicyExecutorProvider")
    private PolicyExecutorProvider provider;

    // Executor that can be used when tests don't want to tie up threads from the Liberty global thread pool to perform concurrent test logic
    private ExecutorService testThreads;

    @Override
    public void destroy() {
        testThreads.shutdownNow();
    }

    @Override
    public void init(ServletConfig config) {
        testThreads = Executors.newFixedThreadPool(20);
    }

    // Await termination of executors that we have never used.
    // Result should be false before shutdown/shutdownNow, and true afterwards, with 0-sized list of canceled queued tasks.
    @Test
    public void testAwaitTerminationOfUnusedExecutor() throws Exception {
        ExecutorService executor1 = provider.create("testAwaitTerminationOfUnusedExecutor-1")
                .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(1)).queueFullAction(QueueFullAction.Abort);
        assertFalse(executor1.awaitTermination(0, TimeUnit.MINUTES));
        assertFalse(executor1.isTerminated());
        assertFalse(executor1.isShutdown());
        executor1.shutdown();
        assertTrue(executor1.awaitTermination(0, TimeUnit.MINUTES));
        assertTrue(executor1.isTerminated());
        assertTrue(executor1.isShutdown());

        ExecutorService executor2 = provider.create("testAwaitTerminationOfUnusedExecutor-2")
                .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(2)).queueFullAction(QueueFullAction.CallerRuns);
        assertFalse(executor2.awaitTermination(0, TimeUnit.HOURS));
        assertFalse(executor2.isTerminated());
        assertFalse(executor2.isShutdown());
        assertTrue(executor2.shutdownNow().isEmpty());
        assertTrue(executor2.awaitTermination(0, TimeUnit.HOURS));
        assertTrue(executor2.isTerminated());
        assertTrue(executor2.isShutdown());

        ExecutorService executor3 = provider.create("testAwaitTerminationOfUnusedExecutor-3")
                .maxQueueSize(3).maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(3));
        assertFalse(executor3.isTerminated());
        assertFalse(executor3.isShutdown());
        executor3.shutdown();
        assertTrue(executor3.isTerminated()); // checking isTerminated after shutdown should transition the state if no tasks remain
        assertTrue(executor3.isShutdown());
        assertTrue(executor3.awaitTermination(3, TimeUnit.NANOSECONDS));

        ExecutorService executor4 = provider.create("testAwaitTerminationOfUnusedExecutor-4")
                .maxQueueSize(4).maxWaitForEnqueue(TimeUnit.DAYS.toMillis(4));
        assertFalse(executor4.isTerminated());
        assertFalse(executor4.isShutdown());
        assertTrue(executor4.shutdownNow().isEmpty());
        assertTrue(executor4.isTerminated()); // checking isTerminated after shutdownNow should transition the state if no tasks remain
        assertTrue(executor4.isShutdown());
        assertTrue(executor4.awaitTermination(4, TimeUnit.MICROSECONDS));
    }

    // Await termination of a policy executor before asking it to shut down.
    // Submit 6 tasks such that, at the time of shutdown, 2 are running, 2 are queued, and 2 are awaiting queue positions.
    // Verify that it reports successful termination after (but not before) shutdown is requested
    // and that the running and queued tasks are allowed to complete, whereas the 2 tasks awaiting queue positions are rejected.
    @Test
    public void testAwaitTerminationWhileActiveThenShutdown() throws Exception {
        ExecutorService executor = provider.create("testAwaitTerminationWhileActiveThenShutdown")
                .maxConcurrency(2).maxQueueSize(2).maxWaitForEnqueue(TimeUnit.SECONDS.toMillis(1)).queueFullAction(QueueFullAction.Abort);

        Future<Boolean> terminationFuture = testThreads.submit(new TerminationAwaitTask(executor, TimeUnit.MINUTES.toNanos(5)));
        assertFalse(terminationFuture.isDone());

        CountDownLatch beginLatch = new CountDownLatch(2);
        CountDownLatch continueLatch = new CountDownLatch(1);
        CountDownTask task = new CountDownTask(beginLatch, continueLatch, TimeUnit.HOURS.toNanos(1));

        Future<Boolean> future1 = executor.submit(task); // should start, decrement the beginLatch, and block on continueLatch
        Future<Boolean> future2 = executor.submit(task); // should start, decrement the beginLatch, and block on continueLatch
        assertTrue(beginLatch.await(TIMEOUT_NS, TimeUnit.MILLISECONDS));

        Future<Boolean> future3 = executor.submit(task); // should be queued
        Future<Boolean> future4 = executor.submit(task); // should be queued

        Future<Future<Boolean>> future5 = testThreads.submit(new SubmitterTask<Boolean>(executor, task)); // should wait for queue position
        Future<Future<Boolean>> future6 = testThreads.submit(new SubmitterTask<Boolean>(executor, task)); // should wait for queue position

        assertFalse(executor.isShutdown());
        assertFalse(executor.isTerminated());
        assertFalse(terminationFuture.isDone());

        executor.shutdown();

        try {
            fail("Should not be able submit new task after shutdown: " + executor.submit(new SharedIncrementTask(), "Should not be able to submit this"));
        } catch (RejectedExecutionException x) {} // pass

        try {
            fail("Should not be able to complete submission of task [5] after shutdown: " + future5.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (ExecutionException x) {
            if (!(x.getCause() instanceof RejectedExecutionException))
                throw x;
        }

        try {
            fail("Should not be able to complete submission of task [6] after shutdown: " + future6.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (ExecutionException x) {
            if (!(x.getCause() instanceof RejectedExecutionException))
                throw x;
        }

        assertTrue(executor.isShutdown());
        assertFalse(executor.isTerminated());
        assertFalse(terminationFuture.isDone()); // still blocked on the continueLatch

        continueLatch.countDown();

        assertTrue(terminationFuture.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(executor.isShutdown());
        assertTrue(executor.isTerminated());
        assertTrue(terminationFuture.isDone());

        assertTrue(future1.isDone());
        assertTrue(future2.isDone());
        assertTrue(future3.isDone());
        assertTrue(future4.isDone());

        assertFalse(future1.isCancelled());
        assertFalse(future2.isCancelled());
        assertFalse(future3.isCancelled());
        assertFalse(future4.isCancelled());

        assertTrue(future1.get());
        assertTrue(future2.get());
        assertTrue(future3.get());
        assertTrue(future4.get());
    }

    // Await termination of a policy executor before asking it to shut down now.
    // Submit 6 tasks such that, at the time of shutdownNow, 2 are running, 2 are queued, and 2 are awaiting queue positions.
    // Verify that it reports successful termination after (but not before) shutdownNow is requested
    // and that the running tasks are canceled and interrupted, the 2 queued tasks are canceled, and the 2 tasks awaiting queue positions are rejected.
    @Test
    public void testAwaitTerminationWhileActiveThenShutdownNow() throws Exception {
        ExecutorService executor = provider.create("testAwaitTerminationWhileActiveThenShutdownNow")
                .maxConcurrency(2).maxQueueSize(2).maxWaitForEnqueue(TimeUnit.SECONDS.toMillis(1)).queueFullAction(QueueFullAction.Abort);

        Future<Boolean> terminationFuture = testThreads.submit(new TerminationAwaitTask(executor, TimeUnit.MINUTES.toNanos(6)));
        assertFalse(terminationFuture.isDone());

        CountDownLatch beginLatch = new CountDownLatch(2);
        CountDownLatch continueLatch = new CountDownLatch(1000); // this latch will never reach 0, awaits on it will be blocked until interrupted
        CountDownTask task = new CountDownTask(beginLatch, continueLatch, TimeUnit.HOURS.toNanos(2));

        Future<Boolean> future1 = executor.submit(task); // should start, decrement the beginLatch, and block on continueLatch
        Future<Boolean> future2 = executor.submit(task); // should start, decrement the beginLatch, and block on continueLatch
        assertTrue(beginLatch.await(TIMEOUT_NS, TimeUnit.MILLISECONDS));

        Future<Boolean> future3 = executor.submit(task); // should be queued
        Future<Boolean> future4 = executor.submit(task); // should be queued

        Future<Future<Boolean>> future5 = testThreads.submit(new SubmitterTask<Boolean>(executor, task)); // should wait for queue position
        Future<Future<Boolean>> future6 = testThreads.submit(new SubmitterTask<Boolean>(executor, task)); // should wait for queue position

        assertFalse(executor.isShutdown());
        assertFalse(executor.isTerminated());
        assertFalse(terminationFuture.isDone());

        List<Runnable> canceledFromQueue = executor.shutdownNow();

        try {
            fail("Task [3] should not complete successfully after shutdownNow: " + future3.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail("Task [4] should not complete successfully after shutdownNow: " + future4.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail("Should not be able to complete submission of task [5] after shutdownNow: " + future5.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (ExecutionException x) {
            if (!(x.getCause() instanceof RejectedExecutionException))
                throw x;
        }

        try {
            fail("Should not be able to complete submission of task [6] after shutdownNow: " + future6.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (ExecutionException x) {
            if (!(x.getCause() instanceof RejectedExecutionException))
                throw x;
        }

        try {
            fail("Should not be able submit new task after shutdownNow: " + executor.submit(new SharedIncrementTask(), "Should not be able to submit this"));
        } catch (RejectedExecutionException x) {} // pass

        assertTrue(executor.isShutdown());

        // await termination
        assertTrue(terminationFuture.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        assertTrue(executor.isShutdown());
        assertTrue(executor.isTerminated());
        assertTrue(terminationFuture.isDone());

        assertTrue(future1.isDone());
        assertTrue(future2.isDone());
        assertTrue(future3.isDone());
        assertTrue(future4.isDone());

        assertTrue(future1.isCancelled());
        assertTrue(future2.isCancelled());
        assertTrue(future3.isCancelled());
        assertTrue(future4.isCancelled());

        try {
            fail("Task [1] should not complete successfully after shutdownNow: " + future1.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail("Task [2] should not complete successfully after shutdownNow: " + future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        assertEquals("List of queued tasks that were canceled upon shutdownNow: " + canceledFromQueue, 2, canceledFromQueue.size());
        assertNotNull(canceledFromQueue.get(0)); // cannot directly compare with task because we submitted as Callable and it had to be converted to new Runnable instance
        assertNotNull(canceledFromQueue.get(1));
    }

    // Await termination of a policy executor before asking it to shut down and shut down now.
    // Submit 3 tasks such that, at the time of shutdown, 1 is running, 1 is queued, and 1 is awaiting a queue position. Immediately thereafter, request shutdownNow.
    // Verify that it reports successful termination after (but not before) shutdownNow is requested
    // and that the running task is canceled and interrupted, the queued task is canceled, and the task awaiting a queue positions is rejected.
    // Verify that the list of queued tasks that were cancelled by shutdownNow includes the single queued task,
    // and verify that the caller can choose to run it after the executor has shut down and teriminated.
    @Test
    public void testAwaitTerminationWhileActiveThenShutdownThenShutdownNow() throws Exception {
        ExecutorService executor = provider.create("testAwaitTerminationWhileActiveThenShutdownThenShutdownNow")
                .maxConcurrency(1).maxQueueSize(1).maxWaitForEnqueue(TimeUnit.SECONDS.toMillis(1)).queueFullAction(QueueFullAction.Abort);

        Future<Boolean> terminationFuture = testThreads.submit(new TerminationAwaitTask(executor, TimeUnit.MINUTES.toNanos(7)));
        assertFalse(terminationFuture.isDone());

        CountDownLatch beginLatch = new CountDownLatch(2);
        CountDownLatch continueLatch = new CountDownLatch(1);
        CountDownTask task = new CountDownTask(beginLatch, continueLatch, TimeUnit.HOURS.toNanos(3));

        Future<Boolean> future1 = executor.submit(task); // should start, decrement the beginLatch, and block on continueLatch
        Future<Boolean> future2 = null;
        while (future2 == null)
            try {
                future2 = executor.submit(task); // waits for a queue position and is either queued or rejected. In the latter case, try again.
            } catch (RejectedExecutionException x) {
                System.out.println("Rejected submit is expected depending on how fast the previous queued item can start. Just try again. Exception was: " + x);
            }

        Future<Future<Boolean>> future3 = testThreads.submit(new SubmitterTask<Boolean>(executor, task)); // should wait for queue position

        assertFalse(executor.isShutdown());
        assertFalse(executor.isTerminated());
        assertFalse(terminationFuture.isDone());

        executor.shutdown();

        List<Runnable> canceledFromQueue = executor.shutdownNow();

        assertEquals("List of queued tasks that were canceled upon shutdownNow: " + canceledFromQueue, 1, canceledFromQueue.size());

        assertTrue(executor.isShutdown());

        try {
            fail("Should not be able submit new task after shutdownNow: " + executor.submit(new SharedIncrementTask(), "Should not be able to submit this"));
        } catch (RejectedExecutionException x) {} // pass

        assertTrue(executor.isShutdown());

        // await termination
        assertTrue(terminationFuture.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        assertTrue(executor.isShutdown());
        assertTrue(executor.isTerminated());
        assertTrue(terminationFuture.isDone());

        assertTrue(future1.isDone());
        assertTrue(future2.isDone());

        assertTrue(future1.isCancelled());
        assertTrue(future2.isCancelled());

        try {
            fail("Task [1] should not complete successfully after shutdownNow: " + future1.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail("Task [2] should not complete successfully after shutdownNow: " + future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail("Should not be able to complete submission of task [3] after shutdownNow: " + future3.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (ExecutionException x) {
            if (!(x.getCause() instanceof RejectedExecutionException))
                throw x;
        }

        // Originally submitted Callable was converted to Runnable, so we cannot directly compare.
        // However, we can run it and verify if it invokes the callable.
        continueLatch.countDown(); // let the Callable that we are about to run do so without blocking
        // We cannot assume the beginLatch count is 1. It could be 2 if we hit a timing window where the polling task removes the first task from the queue but shutdownNow cancels it before it can start.
        long before = beginLatch.getCount(); 
        canceledFromQueue.iterator().next().run();
        long after = beginLatch.getCount();
        assertEquals(before - 1, after);
    }

    // Cover basic life cycle of a policy executor service: use it to run a task, shut it down, and await termination.
    @Test
    public void testBasicLifeCycle() throws Exception {
        ExecutorService executor = provider.create("testBasicLifeCycle");

        assertFalse(executor.isShutdown());
        assertFalse(executor.isTerminated());

        SharedIncrementTask task = new SharedIncrementTask();
        Future<String> future = executor.submit(task, "Successful");
        assertEquals("Successful", future.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertEquals(1, task.count());

        assertFalse(executor.isShutdown());
        assertFalse(executor.isTerminated());

        executor.shutdown();

        assertTrue(executor.isShutdown());

        List<Runnable> canceledTasks = executor.shutdownNow();
        assertEquals(canceledTasks.toString(), 0, canceledTasks.size());

        assertTrue(executor.isShutdown());

        assertTrue(executor.awaitTermination(5, TimeUnit.MINUTES));

        assertTrue(executor.isShutdown());
        assertTrue(executor.isTerminated());
    }

    // When queued tasks are canceled, it should immediately free up capacity to allow tasks waiting for enqueue to be enqueued.
    @Test
    public void testCancelQueuedTasks() throws Exception {
        ExecutorService executor = provider.create("testCancelQueuedTasks")
                .maxConcurrency(1)
                .maxQueueSize(3)
                .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(10));

        // Use up maxConcurrency
        Future<Boolean> blockerFuture = executor.submit(new CountDownTask(new CountDownLatch(1), new CountDownLatch(1), TimeUnit.MINUTES.toNanos(30)));

        // Fill the queue
        AtomicInteger counter = new AtomicInteger();
        Future<Integer> future1 = executor.submit((Callable<Integer>) new SharedIncrementTask(counter));
        Future<Integer> future2 = executor.submit((Callable<Integer>) new SharedIncrementTask(counter));
        Future<Integer> future3 = executor.submit((Callable<Integer>) new SharedIncrementTask(counter));

        // From a separate thread, submit a task that must wait for a queue position
        Future<Future<Integer>> ff4 = testThreads.submit(new SubmitterTask<Integer>(executor, new SharedIncrementTask(counter)));

        try {
            fail("Task[4] submit should remain blocked: " + ff4.get(400, TimeUnit.MILLISECONDS));
        } catch (TimeoutException x) {} // pass

        // Cancel a queued task
        assertTrue(future2.cancel(false));
        assertTrue(future2.isCancelled());
        assertTrue(future2.isDone());

        // Task should be queued now
        Future<Integer> future4 = ff4.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);

        // From separate threads, submit more tasks that must wait for queue positions
        Future<Future<Integer>> ff5 = testThreads.submit(new SubmitterTask<Integer>(executor, new SharedIncrementTask(counter)));
        Future<Future<Integer>> ff6 = testThreads.submit(new SubmitterTask<Integer>(executor, new SharedIncrementTask(counter)));

        try {
            fail("Task[5] submit should remain blocked: " + ff5.get(400, TimeUnit.MILLISECONDS));
        } catch (TimeoutException x) {} // pass

        try {
            fail("Task[6] submit should remain blocked: " + ff6.get(60, TimeUnit.MILLISECONDS));
        } catch (TimeoutException x) {} // pass

        // Cancel 2 queued tasks
        assertTrue(future3.cancel(false));
        assertTrue(future3.isCancelled());
        assertTrue(future3.isDone());

        assertTrue(future4.cancel(false));
        assertTrue(future4.isDone());
        assertTrue(future4.isCancelled());

        // Both tasks should be queued now
        Future<Integer> future5 = ff5.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        Future<Integer> future6 = ff6.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);

        // Cancel one of them
        assertTrue(future5.cancel(false));
        assertTrue(future5.isDone());
        assertTrue(future5.isCancelled());

        // Cancel the blocker task and let the two tasks remaining in the queue start and run to completion
        assertTrue(blockerFuture.cancel(true));

        int result1 = future1.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        int result6 = future6.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);

        assertEquals(3, result1 + result6); // task increment logic could run in either order, with values of: 1,2
        assertEquals(2, counter.get());

        assertTrue(future1.isDone());
        assertTrue(future6.isDone());
        assertFalse(future1.isCancelled());
        assertFalse(future6.isCancelled());

        // Should be possible to get the result multiple times
        assertEquals(Integer.valueOf(result1), future1.get());
        assertEquals(Integer.valueOf(result6), future6.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        try {
            fail("get of canceled future [2] must fail: " + future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail("get of canceled future [3] must fail: " + future3.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail("get of canceled future [4] must fail: " + future4.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail("get of canceled future [5] must fail: " + future5.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass
    }

    // Attempt to await termination from multiple threads at once after a shutdown.
    @Test
    public void testConcurrentAwaitTerminationAfterShutdown() throws Exception {
        final int totalAwaits = 10;
        ExecutorService executor = provider.create("testConcurrentAwaitTerminationAfterShutdown")
                .maxConcurrency(1)
                .maxQueueSize(10);

        // Submit one task to use up all of the threads of the executor that we will await termination of
        CountDownLatch continueLatch = new CountDownLatch(1);
        CountDownTask blockingTask = new CountDownTask(new CountDownLatch(1), continueLatch, TimeUnit.MINUTES.toNanos(20));
        Future<Boolean> blockerFuture = executor.submit(blockingTask);

        // Submit many additional tasks to be queued
        int numToQueue = 5;
        List<Future<Integer>> queuedFutures = new ArrayList<Future<Integer>>(numToQueue);
        AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < numToQueue; i++) {
            System.out.println("Queuing task #" + i);
            queuedFutures.add(executor.submit((Callable<Integer>) new SharedIncrementTask(count)));
        }

        List<Future<Boolean>> awaitTermFutures = new ArrayList<Future<Boolean>>(); 
        for (int i = 0; i < totalAwaits; i++) {
            System.out.println("Submitting awaitTermination task #" + i);
            awaitTermFutures.add(testThreads.submit(new TerminationAwaitTask(executor, TimeUnit.MINUTES.toNanos(10))));
        }

        executor.shutdown();

        // Allow the single blocking task to complete, which means that it will become possible to run queued tasks.
        continueLatch.countDown();

        long start = System.nanoTime();
        long maxWait = TimeUnit.MINUTES.toNanos(5);
        for (int i = 0; i < totalAwaits; i++) {
            long remaining = maxWait - (System.nanoTime() - start);
            assertTrue("awaitTermination Future #" + i, awaitTermFutures.get(i).get(remaining, TimeUnit.NANOSECONDS));
        }

        assertTrue(blockerFuture.get()); // Initial task completed

        assertEquals(numToQueue, count.get());

        for (int i = 0; i < numToQueue; i++)
            assertTrue("previously queued Future #" + i, queuedFutures.get(i).get(0, TimeUnit.MILLISECONDS) > 0);

        try {
            executor.execute(new SharedIncrementTask(null));
            fail("Submits should not be allowed after shutdown");
        } catch (RejectedExecutionException x) {} // pass
    }

    // Attempt to await termination from multiple threads at once after a shutdownNow.
    @Test
    public void testConcurrentAwaitTerminationAfterShutdownNow() throws Exception {
        final int totalAwaitTermination = 6;
        final int totalAwaitEnqueue = 4;
        final int numToQueue = 2;
        ExecutorService executor = provider.create("testConcurrentAwaitTerminationAfterShutdownNow")
                .maxConcurrency(1)
                .maxQueueSize(numToQueue)
                .maxWaitForEnqueue(100);

        // Submit one task to use up all of the threads of the executor that we will await termination of
        CountDownLatch continueLatch = new CountDownLatch(1);
        CountDownTask blockingTask = new CountDownTask(new CountDownLatch(1), continueLatch, TimeUnit.MINUTES.toNanos(30));
        Future<Boolean> blockerFuture = executor.submit(blockingTask);

        AtomicInteger count = new AtomicInteger(0);

        // Submit a couple of additional tasks to be queued
        List<Future<Integer>> queuedFutures = new ArrayList<Future<Integer>>(numToQueue);
        for (int i = 0; i < numToQueue; i++) {
            System.out.println("Queuing task #" + i);
            queuedFutures.add(executor.submit((Callable<Integer>) new SharedIncrementTask(count)));
        }

        // Submit tasks to wait for termination
        List<Future<Boolean>> awaitTermFutures = new ArrayList<Future<Boolean>>(); 
        for (int i = 0; i < totalAwaitTermination; i++) {
            System.out.println("Submitting awaitTermination task #" + i);
            awaitTermFutures.add(testThreads.submit(new TerminationAwaitTask(executor, TimeUnit.MINUTES.toNanos(10))));
        }

        // Submit several tasks to await queue positions
        List<Future<Future<Integer>>> awaitingEnqueueFutures = new ArrayList<Future<Future<Integer>>>(totalAwaitEnqueue);
        for (int i = 0; i < totalAwaitEnqueue; i++) {
            System.out.println("Submitting task #" + i + " that will wait for a queue position");
            awaitingEnqueueFutures.add(testThreads.submit(new SubmitterTask<Integer>(executor, new SharedIncrementTask(count))));
        }

        List<Runnable> tasksCanceledFromQueue = executor.shutdownNow();

        long start = System.nanoTime();
        long maxWait = TimeUnit.MINUTES.toNanos(5);
        for (int i = 0; i < totalAwaitTermination; i++) {
            long remaining = maxWait - (System.nanoTime() - start);
            assertTrue("awaitTermination Future #" + i, awaitTermFutures.get(i).get(remaining, TimeUnit.NANOSECONDS));
        }

        // Initial task should be canceled
        try {
            fail("Running task should have been canceled due to shutdownNow. Instead: " + blockerFuture.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        for (int i = 0; i < numToQueue; i++)
            try {
                fail("shutdownNow should have canceled previously queued Future #" + i + ": " + queuedFutures.get(i).get(0, TimeUnit.MILLISECONDS));
            } catch (CancellationException x) {} // pass

        // shutdownNow should cancel at least as many tasks as were in the queue when it was invoked.
        // There is a possibility of a task that was waiting to enqueue briefly entering the queue during this window and also being canceled,
        // which is why we check for at least as many instead of an exact match.
        assertTrue("Tasks canceled from queue by shutdownNow: " + tasksCanceledFromQueue, tasksCanceledFromQueue.size() >= numToQueue);

        // Tasks for blocked enqueue
        for (int i = 0; i < totalAwaitEnqueue; i++) {
            Future<Future<Integer>> ff = awaitingEnqueueFutures.get(i);
            try {
                System.out.println("Future for blocked enqueue #" + i);
                fail("Should not be able to submit task with full queue, even after shutdownNow: " + ff.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
            } catch (ExecutionException x) {
                if (!(x.getCause() instanceof RejectedExecutionException))
                    throw x;
            }
        }

        // None of the queued or waiting-to-enqueue tasks should even attempt to start running
        assertEquals(0, count.get());

        try {
            executor.execute(new SharedIncrementTask(null));
            fail("Submits should not be allowed after shutdownNow");
        } catch (RejectedExecutionException x) {} // pass
    }

    // Cancel the same queued tasks from multiple threads at the same time. Each task should only successfully cancel once,
    // exactly one task waiting for enqueue should be allowed to enqueue for each successful cancel.
    @Test
    public void testConcurrentCancelQueuedTasks() throws Exception {
        ExecutorService executor = provider.create("testConcurrentCancelQueuedTasks")
                .maxConcurrency(1)
                .maxQueueSize(4)
                .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(12));

        // Use up maxConcurrency
        Future<Boolean> blockerFuture = executor.submit(new CountDownTask(new CountDownLatch(1), new CountDownLatch(1), TimeUnit.MINUTES.toNanos(24)));

        // Fill the queue
        AtomicInteger counter = new AtomicInteger();
        Future<Integer> future1 = executor.submit((Callable<Integer>) new SharedIncrementTask(counter));
        Future<Integer> future2 = executor.submit((Callable<Integer>) new SharedIncrementTask(counter));
        Future<Integer> future3 = executor.submit((Callable<Integer>) new SharedIncrementTask(counter));
        Future<Integer> future4 = executor.submit((Callable<Integer>) new SharedIncrementTask(counter));

        // From separate threads, submit tasks that must wait for queue positions
        CompletionService<Future<Integer>> completionSvc = new ExecutorCompletionService<Future<Integer>>(testThreads);
        Future<Future<Integer>> ff5 = completionSvc.submit(new SubmitterTask<Integer>(executor, new SharedIncrementTask(counter)));
        Future<Future<Integer>> ff6 = completionSvc.submit(new SubmitterTask<Integer>(executor, new SharedIncrementTask(counter)));
        Future<Future<Integer>> ff7 = completionSvc.submit(new SubmitterTask<Integer>(executor, new SharedIncrementTask(counter)));

        // Have 8 threads attempt to cancel 2 of the tasks
        int numCancels = 8;
        CountDownLatch beginLatch = new CountDownLatch(numCancels);
        CountDownLatch continueLatch = new CountDownLatch(1);
        Callable<Boolean> cancellationTask1 = new CancellationTask(future1, false, beginLatch, continueLatch, TimeUnit.MINUTES.toNanos(10));
        Callable<Boolean> cancellationTask3 = new CancellationTask(future3, false, beginLatch, continueLatch, TimeUnit.MINUTES.toNanos(13));
        List<Future<Boolean>> cancellationFutures = new ArrayList<Future<Boolean>>(numCancels);
        for (int i = 0; i < 8; i++)
            cancellationFutures.add(testThreads.submit(i % 2 == 1 ? cancellationTask1 : cancellationTask3));

        // Position all of the threads so that they are about to attempt cancel
        assertTrue(beginLatch.await(TIMEOUT_NS * numCancels, TimeUnit.NANOSECONDS));

        // Let them start canceling,
        continueLatch.countDown();

        // Should be able to enqueue exactly 2 more tasks
        Future<Future<Integer>> ffA = completionSvc.poll(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        assertNotNull(ffA);
        assertTrue(ffA.isDone());
        Future<Integer> futureA = ffA.get();

        Future<Future<Integer>> ffB = completionSvc.poll(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        assertNotNull(ffB);
        assertTrue(ffB.isDone());
        Future<Integer> futureB = ffB.get();

        // At this point:
        // futures 1,3 should be canceled
        assertTrue(future1.isCancelled());
        assertTrue(future3.isCancelled());

        // future 2,4,A,B should be queued
        assertFalse(future2.isDone());
        assertFalse(future4.isDone());
        assertFalse(futureA.isDone());
        assertFalse(futureB.isDone());

        // future C (one of 5,6,7) should still be waiting for a slot in the queue.
        assertNull(completionSvc.poll());

        // Set subtraction is an inefficient way to compute the remaining future, but this is only a test case
        Set<Future<Future<Integer>>> remaining = new HashSet<Future<Future<Integer>>>(Arrays.asList(ff5, ff6, ff7));
        assertTrue(remaining.removeAll(Arrays.asList(ffA, ffB)));
        Future<Future<Integer>> ffC = remaining.iterator().next();

        try {
            fail("get of canceled future [1] must fail: " + future1.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail("get of canceled future [3] must fail: " + future3.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        try {
            fail(ffC + " submit should remain blocked: " + ffC.get(500, TimeUnit.MILLISECONDS));
        } catch (TimeoutException x) {} // pass

        // Having already verified that task C wasn't queued, cancel the queue attempt
        assertTrue(ffC.cancel(true));

        try {
            fail("get of canceled future [C] must fail: " + ffC.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass 

        // Cancel the blocker task and let the four tasks remaining in the queue start and run to completion
        assertTrue(blockerFuture.cancel(true));

        int result2 = future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        int result4 = future4.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        int resultA = futureA.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        int resultB = futureB.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);

        assertEquals(10, result2 + result4 + resultA + resultB); // task increment logic could run in either order, with values of: 1,2,3,4
        assertEquals(4, counter.get());

        assertTrue(future2.isDone());
        assertTrue(future4.isDone());
        assertTrue(futureA.isDone());
        assertTrue(futureB.isDone());
        assertFalse(future2.isCancelled());
        assertFalse(future4.isCancelled());
        assertFalse(futureA.isCancelled());
        assertFalse(futureB.isCancelled());
    }

    // Attempt shutdown and shutdownNow from multiple threads at once.
    @AllowedFFDC("java.lang.InterruptedException") // when shutdownNow cancels tasks that are attempting shutdown/shutdownNow 
    @Test
    public void testConcurrentShutdownAndShutdownNow() throws Exception {
        final int total = 10;
        ExecutorService executor = provider.create("testConcurrentShutdownAndShutdownNow").maxConcurrency(total);
        CountDownLatch beginLatch = new CountDownLatch(total);
        CountDownLatch continueLatch = new CountDownLatch(1);

        ShutdownTask shutdownTask = new ShutdownTask(executor, false, beginLatch, continueLatch, TimeUnit.HOURS.toNanos(1));
        ShutdownTask shutdownNowTask = new ShutdownTask(executor, true, beginLatch, continueLatch, TimeUnit.HOURS.toNanos(1));
        ArrayList<Future<List<Runnable>>> futures = new ArrayList<Future<List<Runnable>>>();
        for (int i = 0; i < total; i++)
            if (i % 2 == 0) {
                System.out.println("Submitting shutdown task #" + i);
                futures.add(executor.submit(shutdownTask));
            } else {
                System.out.println("Submitting shutdownNow task #" + i);
                futures.add(executor.submit(shutdownNowTask));                
            }

        Thread[] threads = new Thread[total]; // might not be in the same order as tasks were submitted

        // Position all tasks to the point where they are about to attempt a shutdown.
        beginLatch.await(TIMEOUT_NS, TimeUnit.NANOSECONDS);

        for (int i = 0; i < total; i++)
             threads[i] = shutdownTask.executionThreads.poll();

        System.out.println("Execution threads for shutdown tasks: " + Arrays.toString(threads));

        // Let all of the tasks attempt the shutdown
        continueLatch.countDown();

        for (int i = 0; i < total; i++)
            try {
                System.out.println("Attemping get for shutdown future #" + i);
                List<Runnable> canceledQueuedTasks = futures.get(i).get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                if (i % 2 == 0)
                    assertNull(canceledQueuedTasks);
                else
                    assertEquals(0, canceledQueuedTasks.size());
                System.out.println("Successful");
            } catch (CancellationException x) { // pass because shutdownNow will cancel running tasks
                System.out.println("Task was canceled due to shutdownNow");
            }

        try {
            executor.execute(new SharedIncrementTask(null));
            fail("Submits should not be allowed after shutdown or shutdownNow");
        } catch (RejectedExecutionException x) {} // pass

        assertTrue(executor.isShutdown());

        // poll for termination
        for (long start = System.nanoTime(); !executor.isTerminated() && System.nanoTime() - start < TIMEOUT_NS; TimeUnit.MILLISECONDS.sleep(200)) ;

        assertTrue(executor.isTerminated());
    }

    // Attempts submits and cancels concurrently. 3 threads submit 10 tasks each, canceling every other task submitted.
    @Test
    public void testConcurrentSubmitAndCancel() throws Exception {
        final int numThreads = 3;
        final int numIterations = 5;

        final ExecutorService executor = provider.create("testConcurrentSubmitAndCancel")
                .maxConcurrency(4)
                .maxQueueSize(30);

        final AtomicInteger counter = new AtomicInteger();
        final BlockingQueue<Future<Integer>> futuresToCancel = new LinkedBlockingQueue<Future<Integer>>();
        final AtomicInteger numSuccessfulCancels = new AtomicInteger();

        Callable<List<Future<Integer>>> multipleSubmitAndCancelTask = new Callable<List<Future<Integer>>>() {
            @Override
            public List<Future<Integer>> call() throws Exception {
                List<Future<Integer>> futures = new ArrayList<Future<Integer>>(numIterations * 2);
                for (int i = 0; i < numIterations; i++) {
                    Future<Integer> futureA = executor.submit((Callable<Integer>) new SharedIncrementTask(counter));
                    futuresToCancel.add(futureA);
                    futures.add(futureA);
                    Future<Integer> futureB = executor.submit((Callable<Integer>) new SharedIncrementTask(counter));
                    futures.add(futureB);

                    Future<?> futureC = futuresToCancel.poll(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                    boolean interruptIfRunning = i % 2 == 0;
                    if (futureC.cancel(interruptIfRunning))
                        numSuccessfulCancels.incrementAndGet();
                }
                return futures;
            }
        };

        List<Callable<List<Future<Integer>>>> testTasks = new ArrayList<Callable<List<Future<Integer>>>>(numThreads);
        for (int i = 0; i < numThreads; i++)
            testTasks.add(multipleSubmitAndCancelTask);

        List<Future<Integer>> allFutures = new ArrayList<Future<Integer>>();
        for (Future<List<Future<Integer>>> result : testThreads.invokeAll(testTasks, TIMEOUT_NS * numThreads * numIterations, TimeUnit.NANOSECONDS))
            allFutures.addAll(result.get());

        int numCanceled = 0;
        int numCompleted = 0;
        HashSet<Integer> resultsOfSuccessfulTasks = new HashSet<Integer>();
        for (Future<Integer> future : allFutures) {
            if (future.isCancelled()) {
                numCanceled++;
                numCompleted++;
            } else if (future.isDone()) {
                assertTrue(resultsOfSuccessfulTasks.add(future.get()));
                numCompleted++;
            } else
                try {
                    assertTrue(resultsOfSuccessfulTasks.add(future.get(TIMEOUT_NS, TimeUnit.NANOSECONDS)));
                    numCompleted++;
                } catch (CancellationException x) {
                    numCanceled++;
                }
        }

        int totalTasksSubmitted = numThreads * numIterations * 2;
        int numTasksThatStartedRunning = counter.get();

        System.out.println(totalTasksSubmitted + " tasks were submitted, of which " + numCompleted + " completed, of which " + numCanceled + " have canceled futures and " + numSuccessfulCancels + " reported successful cancel.");
        System.out.println(numTasksThatStartedRunning + " tasks started running.");

        assertEquals(totalTasksSubmitted, numCompleted);
        assertEquals(numSuccessfulCancels.get(), numCanceled);

        // We requested cancellation of half of the tasks, however, some might have already completed. We can only test that no extras were canceled.
        assertTrue(numCanceled <= totalTasksSubmitted / 2);

        // Some tasks might start running and later be canceled. The number that starts running must be at least half.
        assertTrue(numTasksThatStartedRunning >= totalTasksSubmitted / 2);

        // Every task that was not successfully canceled must return a unique result (per the shared counter).
        assertEquals(totalTasksSubmitted - numSuccessfulCancels.get(), resultsOfSuccessfulTasks.size());

        // Should be nothing left in the queue for the policy executor
        List<Runnable> tasksCanceledFromQueue = executor.shutdownNow();
        assertEquals(0, tasksCanceledFromQueue.size());
    }

    // Attempt submits while concurrently shutting down. Submits should either be accepted
    // or rejected with the error that indicates the executor has been shut down.
    @Test
    public void testConcurrentSubmitAndShutdown() throws Exception {
        final int numSubmits = 10;

        ExecutorService executor = provider.create("testConcurrentSubmitAndShutdown").maxConcurrency(numSubmits).maxQueueSize(numSubmits);

        CountDownLatch beginLatch = new CountDownLatch(numSubmits);
        CountDownLatch continueLatch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger();

        List<Future<Future<Integer>>> ffs = new ArrayList<Future<Future<Integer>>>(numSubmits);
        for (int i = 0; i < numSubmits; i++)
            ffs.add(testThreads.submit(new SubmitterTask<Integer>(executor, new SharedIncrementTask(counter), beginLatch, continueLatch, TimeUnit.MINUTES.toNanos(50))));

        // Wait for all of the test threads to position themselves to start submitting to the policy executor
        beginLatch.await(TIMEOUT_NS * 5, TimeUnit.NANOSECONDS);

        // Let them start submitting tasks to the policy executor
        continueLatch.countDown();
        TimeUnit.NANOSECONDS.sleep(100);

        // Shut down the policy executor
        executor.shutdown();

        assertTrue(executor.awaitTermination(TIMEOUT_NS * 5, TimeUnit.NANOSECONDS));
        assertTrue(executor.isTerminated());

        int numAccepted = 0;
        int numRejected = 0;
        int sum = 0;

        for (Future<Future<Integer>> ff : ffs)
            try {
                Future<Integer> future = ff.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                numAccepted++;
                assertFalse(future.isCancelled());
                assertTrue(future.isDone());
                sum += future.get();
            } catch (ExecutionException x) {
                if (x.getCause() instanceof RejectedExecutionException) {
                    numRejected++;
                    if (!x.getCause().getMessage().contains("CWWKE1202E")) // rejected-due-to-shutdown message
                        throw x;
                } else
                    throw x;
            }
 
        System.out.println(numAccepted + " accepted, " + numRejected + " rejected");
        assertEquals(numSubmits, numAccepted + numRejected);

        // tasks can run in any order, so it's more convenient to validate the sum of results rather than individual results
        int expectedSum = numAccepted * (numAccepted + 1) / 2;
        assertEquals(expectedSum, sum);
    }

    // Attempt submits while concurrently shutting down via shutdownNow. Submits should either be accepted
    // or rejected with the error that indicates the executor has been shut down. Tasks which are submitted
    // will either be canceled from the queue, canceled while running, or will have completed successfully.
    @Test
    public void testConcurrentSubmitAndShutdownNow() throws Exception {
        final int numSubmits = 10;

        ExecutorService executor = provider.create("testConcurrentSubmitAndShutdownNow").maxConcurrency(numSubmits).maxQueueSize(numSubmits);

        CountDownLatch beginLatch = new CountDownLatch(numSubmits);
        CountDownLatch continueLatch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger();

        List<Future<Future<Integer>>> ffs = new ArrayList<Future<Future<Integer>>>(numSubmits);
        for (int i = 0; i < numSubmits; i++)
            ffs.add(testThreads.submit(new SubmitterTask<Integer>(executor, new SharedIncrementTask(counter), beginLatch, continueLatch, TimeUnit.MINUTES.toNanos(40))));

        // Wait for all of the test threads to position themselves to start submitting to the policy executor
        beginLatch.await(TIMEOUT_NS * 5, TimeUnit.NANOSECONDS);

        // Let them start submitting tasks to the policy executor
        continueLatch.countDown();
        TimeUnit.NANOSECONDS.sleep(100);

        // Shut down the policy executor
        List<Runnable> canceledQueuedTasks = executor.shutdownNow();

        assertTrue(executor.awaitTermination(TIMEOUT_NS * 5, TimeUnit.NANOSECONDS));
        assertTrue(executor.isTerminated());

        int numAccepted = 0;
        int numAcceptedThenCanceled = 0;
        int numRejected = 0;
        int sum = 0;

        for (Future<Future<Integer>> ff : ffs)
            try {
                Future<Integer> future = ff.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                assertTrue(future.isDone());
                numAccepted++;
                if (future.isCancelled())
                    numAcceptedThenCanceled++;
                else
                    sum += future.get();
            } catch (ExecutionException x) {
                if (x.getCause() instanceof RejectedExecutionException) {
                    numRejected++;
                    if (!x.getCause().getMessage().contains("CWWKE1202E")) // rejected-due-to-shutdown message
                        throw x;
                } else
                    throw x;
            }

        int numCanceledFromQueue = canceledQueuedTasks.size();

        System.out.println(numAccepted + " accepted, of which " + numAcceptedThenCanceled + " were canceled due to shutdownNow, with "
                         + numCanceledFromQueue + " canceled from the queue; " + numRejected + " rejected");

        assertEquals(numSubmits, numAccepted + numRejected);

        assertTrue(numCanceledFromQueue <= numAcceptedThenCanceled);

        int numSuccessful = numAccepted - numAcceptedThenCanceled;

        // tasks can run in any order and some might partially run, so we have little guarantee of the results
        int expectedSumMax = numAccepted * (numAccepted + 1) / 2;
        int expectedSumMin = numSuccessful * (numSuccessful + 1) / 2;
        assertTrue(sum >= expectedSumMin);
        assertTrue(sum <= expectedSumMax);
    }

    /**
     * Submit multiple tasks at once, waiting for some to complete before scheduling another group of tasks, and so forth.
     */
    @Test
    public void testGroupedSubmits() throws Exception {
        final int groupSize = 8;
        final int nextGroupOn = 6;
        final int numGroups = 5;

        ExecutorService executor = provider.create("testGroupedSubmits")
                .maxConcurrency(4)
                .maxQueueSize(nextGroupOn)
                .queueFullAction(QueueFullAction.Abort);

        final CompletionService<Integer> completionSvc = new ExecutorCompletionService<Integer>(executor);

        List<Future<Future<Integer>>> submitFutures = new ArrayList<Future<Future<Integer>>>(groupSize * numGroups);

        AtomicInteger counter = new AtomicInteger();
        Phaser allTasksReady = new Phaser(groupSize);

        for (int g = 0; g < numGroups; g++) {
            // launch separate threads to submit these tasks all at once (via the allTaskReady phaser)
            for (int i = 0; i < groupSize; i++) {
                CompletionServiceTask<Integer> completionSvcTask = new CompletionServiceTask<Integer>(completionSvc, new SharedIncrementTask(counter), allTasksReady);
                submitFutures.add(testThreads.submit(completionSvcTask));
            }

            // Await successful completion of 'nextGroupOn' number of tasks before submitting more
            for (int i = 0; i < nextGroupOn; i++) {
                Future<Integer> future = completionSvc.poll(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                assertNotNull(future);
            }
        }

        List<Future<Integer>> completedFutures = new ArrayList<Future<Integer>>(groupSize * numGroups);
        int numRejected = 0;
        List<Integer> results = new ArrayList<Integer>(groupSize * numGroups);
        int sum = 0;

        // Wait for all remaining tasks to finish and compute totals
        for (Future<Future<Integer>> ff : submitFutures)
            try {
                Future<Integer> future = ff.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                assertNotNull(completedFutures.add(future));
                int result = future.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                assertTrue(results.add(result));
                sum += result;
            } catch (ExecutionException x) {
                if (x.getCause() instanceof RejectedExecutionException)
                    numRejected++;
                else
                    throw x;
            }

        System.out.println(completedFutures.size() + " completed successfully: " + completedFutures);
        System.out.println(numRejected + " were rejected");

        int count = counter.get();
        assertEquals(count, results.size());
        assertEquals(count * (count + 1) / 2, sum);

        int maxRejected = (groupSize - nextGroupOn) * numGroups;
        assertTrue("maximum of " + maxRejected + " tasks submits should be rejected", numRejected <= maxRejected);

        executor.shutdown();

        // with no tasks remaining, should be immediately considered terminated
        assertTrue(executor.isTerminated());
    }

    // Attempt shutdown from multiple threads at once. Interrupt most of them and verify that the interrupted
    // shutdown operations fail rather than prematurely returning as successful prior to a successful shutdown.
    @AllowedFFDC("java.lang.InterruptedException") // when shutdown is interrupted
    @Test
    public void testInterruptShutdown() throws Exception {
        final int total = 10;
        ExecutorService executor = provider.create("testInterruptShutdown").maxConcurrency(total);
        CountDownLatch beginLatch = new CountDownLatch(total);
        CountDownLatch continueLatch = new CountDownLatch(1);

        ShutdownTask shutdownTask = new ShutdownTask(executor, false, beginLatch, continueLatch, TimeUnit.HOURS.toNanos(1));
        ArrayList<Future<List<Runnable>>> futures = new ArrayList<Future<List<Runnable>>>();
        for (int i = 0; i < total; i++) {
            System.out.println("Submitting shutdown task #" + i);
            futures.add(executor.submit(shutdownTask));
        }

        Thread[] threads = new Thread[total]; // might not be in the same order as tasks were submitted

        // Position all tasks to the point where they are about to attempt a shutdown.
        beginLatch.await(TIMEOUT_NS, TimeUnit.NANOSECONDS);

        for (int i = 0; i < total; i++)
             threads[i] = shutdownTask.executionThreads.poll();

        System.out.println("Execution threads for shutdown tasks: " + Arrays.toString(threads));

        // Let all of the tasks attempt the shutdown
        continueLatch.countDown();

        // Interrupt all but the first 2 shutdown operations
        for (int i = 2; i < total; i++)
            threads[i].interrupt();

        // Verify that all shutdown attempts either succeeded or failed as expected with a RuntimeException with cause of InterruptedException
        int interruptCount = 0;
        for (int i = 0; i < total; i++)
            try {
                System.out.println("Attemping get for shutdown future #" + i);
                List<Runnable> result = futures.get(i).get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                assertNull(result); 
                System.out.println("Successful");
            } catch (ExecutionException x) {
                System.out.println(x);
                if (x.getCause() instanceof RuntimeException  && x.getCause().getCause() instanceof InterruptedException)
                    interruptCount++;
                else
                    throw x;
            }

        assertTrue("Too many tasks interrupted: " + interruptCount, interruptCount <= 8);

        try {
            executor.execute(new SharedIncrementTask(null));
            fail("Submits should not be allowed after shutdown");
        } catch (RejectedExecutionException x) {} // pass

        assertTrue(executor.isShutdown());

        // poll for termination
        for (long start = System.nanoTime(); !executor.isTerminated() && System.nanoTime() - start < TIMEOUT_NS; TimeUnit.MILLISECONDS.sleep(200)) ;

        assertTrue(executor.isTerminated());
    }

    // Attempt shutdownNow from multiple threads at once. Interrupt most of them and verify that the interrupted
    // shutdownNow operations fail rather than prematurely returning as successful prior to a successful shutdown.
    @AllowedFFDC("java.lang.InterruptedException") // when shutdownNow is interrupted
    @Test
    public void testInterruptShutdownNow() throws Exception {
        final int total = 10;
        ExecutorService executor = provider.create("testInterruptShutdownNow").maxConcurrency(total);
        CountDownLatch beginLatch = new CountDownLatch(total);
        CountDownLatch continueLatch = new CountDownLatch(1);

        ShutdownTask shutdownNowTask = new ShutdownTask(executor, true, beginLatch, continueLatch, TimeUnit.HOURS.toNanos(1));
        ArrayList<Future<List<Runnable>>> futures = new ArrayList<Future<List<Runnable>>>();
        for (int i = 0; i < total; i++) {
            System.out.println("Submitting shutdownNow task #" + i);
            futures.add(executor.submit(shutdownNowTask));
        }

        Thread[] threads = new Thread[total]; // might not be in the same order as tasks were submitted

        // Position all tasks to the point where they are about to attempt a shutdown.
        beginLatch.await(TIMEOUT_NS, TimeUnit.NANOSECONDS);

        for (int i = 0; i < total; i++)
             threads[i] = shutdownNowTask.executionThreads.poll();

        System.out.println("Execution threads for shutdownNow tasks: " + Arrays.toString(threads));

        // Let all of the tasks attempt the shutdown
        continueLatch.countDown();

        // Interrupt all but the first 2 shutdown operations
        for (int i = 2; i < total; i++)
            threads[i].interrupt();

        // Verify that all shutdown attempts either succeeded or failed as expected with a RuntimeException with cause of InterruptedException
        int interruptCount = 0;
        for (int i = 0; i < total; i++)
            try {
                System.out.println("Attemping get for shutdownNow future #" + i);
                List<Runnable> canceledQueuedTasks = futures.get(i).get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                assertEquals(0, canceledQueuedTasks.size()); 
                System.out.println("Successful");
            } catch (ExecutionException x) {
                System.out.println(x);
                if (x.getCause() instanceof RuntimeException  && x.getCause().getCause() instanceof InterruptedException)
                    interruptCount++;
                else
                    throw x;
            } catch (CancellationException x) { // pass because shutdownNow will cancel running tasks
                System.out.println("Task was canceled due to shutdownNow");
            }

        assertTrue("Too many tasks interrupted: " + interruptCount, interruptCount <= 8);

        try {
            executor.execute(new SharedIncrementTask(null));
            fail("Submits should not be allowed after shutdownNow");
        } catch (RejectedExecutionException x) {} // pass

        assertTrue(executor.isShutdown());

        // poll for termination
        for (long start = System.nanoTime(); !executor.isTerminated() && System.nanoTime() - start < TIMEOUT_NS; TimeUnit.MILLISECONDS.sleep(200)) ;

        assertTrue(executor.isTerminated());
    }

    // Interrupt an attempt to submit a task. Verify that RejectedExecutionException with chained InterruptedException is raised.
    // Also interrupt a running task which rethrows the InterruptedException and verify that the exception is raised when attempting Future.get,
    // and that a queued task that was blocked waiting for a thread is able to subsequently run.
    @Test
    public void testInterruptSubmitAndRun() throws Exception {
        ExecutorService executor = provider.create("testInterruptSubmitAndRun-submitter")
                .maxConcurrency(1).maxQueueSize(1).maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(4));
        CountDownLatch beginLatch = new CountDownLatch(1);
        CountDownLatch continueLatch = new CountDownLatch(1);
        CountDownTask task1 = new CountDownTask(beginLatch, continueLatch, TimeUnit.HOURS.toNanos(4));

        // Submit a task and wait for it to start
        Future<Boolean> future1 = executor.submit(task1);
        assertTrue(beginLatch.await(TIMEOUT_NS, TimeUnit.MILLISECONDS));

        // Submit a task which will be stuck in the queue waiting for the first task before it can get a thread to run on
        AtomicInteger count = new AtomicInteger();
        Future<?> future2 = executor.submit((Runnable) new SharedIncrementTask(count));

        // From a thread owned by the test servlet, interrupt the current thread
        Future<?> interrupterFuture = testThreads.submit(new InterrupterTask(Thread.currentThread(), 1, TimeUnit.SECONDS));

        try {
            Future<Integer> future3 = executor.submit((Callable<Integer>) new SharedIncrementTask(count));
            fail("Task submit should be interrupted while awaiting a queue position. " + future3);
        } catch (RejectedExecutionException x) {
            if (!(x.getCause() instanceof InterruptedException))
                throw x;
        }

        assertFalse(Thread.currentThread().isInterrupted()); // should have been reset when InterruptedException was raised

        assertFalse(future1.isCancelled());
        assertFalse(future2.isCancelled());
        assertFalse(future1.isDone());
        assertFalse(future2.isDone());

        // Also interrupt the executing task
        Thread executionThread = task1.executionThreads.poll(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        assertNotNull(executionThread);
        executionThread.interrupt();

        // Interruption of task1 should allow the queued task to run
        assertNull(future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertEquals(1, count.get());
        assertFalse(future2.isCancelled());
        assertTrue(future2.isDone());

        // Task1 should be completed with exception, but not canceled
        assertFalse(future1.isCancelled());
        assertTrue(future1.isDone());
        try {
            fail("Interrupted task that rethrows exception should not return result: " + future1.get(1, TimeUnit.NANOSECONDS));
        } catch (ExecutionException x) {
            if (!(x.getCause() instanceof InterruptedException))
                throw x;
        }

        // Wait for the task that was submitted to the test's fixed thread pool to complete, if it hasn't done so already
        assertNull(interrupterFuture.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
    }

    // Poll isTerminated until the executor terminates while concurrently awaiting termination from another thread.
    // The awaitTermination operation should recognize the state transition that is triggered by invocation of the isTerminated method.
    @Test
    public void testIsTerminatedWhileAwaitingTermination() throws Exception {
        ExecutorService executor = provider.create("testIsTerminatedWhileAwaitingTermination").maxConcurrency(3);

        // start a thread to await termination
        Future<Boolean> terminationFuture = testThreads.submit(new TerminationAwaitTask(executor, TIMEOUT_NS * 5));

        AtomicInteger counter = new AtomicInteger();
        final int numSubmitted = 15;
        List<Runnable> tasks = new ArrayList<Runnable>();
        Future<?>[] futures = new Future<?>[numSubmitted];

        for (int i = 0; i < numSubmitted; i++)
            tasks.add(new SharedIncrementTask(counter));

        for (int i = 0; i < numSubmitted; i++)
            futures[i] = executor.submit(tasks.get(i));

        List<Runnable> canceledFromQueue = executor.shutdownNow();

        // poll for termination
        for (long start = System.nanoTime(); !executor.isTerminated() && System.nanoTime() - start < TIMEOUT_NS * 4; TimeUnit.MILLISECONDS.sleep(100)) ;

        assertTrue(executor.isTerminated());

        // awaitTermination should complete within a reasonable amount of time after isTerminated transitions the state
        assertTrue(terminationFuture.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        // verify that tasks canceled from the queue report that they are canceled
        for (Runnable task : canceledFromQueue) {
            int i = tasks.indexOf(task);
            assertNotSame("unknown task reported canceled from queue: " + task, -1, i);
            System.out.println("Task #" + i + " canceled from queue");

            assertTrue("task" + i, futures[i].isCancelled());
            assertTrue("task" + i, futures[i].isDone());
        }

        int numCanceled = 0;
        for (int i = 0; i < numSubmitted; i++) {
            System.out.println("Future #" + i);
            if (futures[i].isCancelled()) {
                numCanceled++;
                assertTrue(futures[i].isDone());
            }
        }

        int numCanceledFromQueue = canceledFromQueue.size();

        int count = counter.get();
        System.out.println(count + " tasks either completed successfully or were canceled during execution.");
        System.out.println(numCanceled + " tasks were canceled, either during execution or from the queue.");
        System.out.println(numCanceledFromQueue + " tasks were canceled from the queue.");

        assertTrue(count + numCanceledFromQueue <= numSubmitted);
        assertTrue(numCanceledFromQueue <= numCanceled);
    }

    // Poll isTerminated until the executor (which was previously unused) terminates while concurrently awaiting termination from another thread.
    // The awaitTermination operation should recognize the state transition that is triggered by invocation of the isTerminated method.
    @Test
    public void testIsTerminatedWhileAwaitingTerminationOfUnusedExecutor() throws Exception {
        ExecutorService executor = provider.create("testIsTerminatedWhileAwaitingTerminationOfUnusedExecutor");

        // start a thread to await termination
        Future<Boolean> terminationFuture = testThreads.submit(new TerminationAwaitTask(executor, TIMEOUT_NS * 2));

        executor.shutdown();

        // poll for termination
        for (long start = System.nanoTime(); !executor.isTerminated() && System.nanoTime() - start < TIMEOUT_NS; TimeUnit.MILLISECONDS.sleep(100)) ;

        assertTrue(executor.isTerminated());

        // awaitTermination should complete within a reasonable amount of time after isTerminated transitions the state
        assertTrue(terminationFuture.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
    }

    // Submit a task to the policy executor that breaks itself into multiple tasks that also submit to the policy executor,
    // which in turn break themselves down and submit multiple tasks to the policy executor, and so forth.
    @Test
    public void testMultipleLayersOfSubmits() throws Exception {
        // TODO add min/coreConcurrency when we have it
        ExecutorService executor = provider.create("testMultipleLayersOfSubmits")
                .maxConcurrency(8) // just enough to ensure we can cover a 16 element array
                .maxQueueSize(8) // also just enough to ensure we can cover a 16 element array
                .queueFullAction(QueueFullAction.Abort); // TODO in the future, this sort of test is a good candidate for CallerRuns

        int[] array1 = new int[] { 2, 9, 3, 5, 1, 3, 6, 3, 8, 0, 4, 4, 10, 2, 1, 8 };
        System.out.println("Searching of minimum of " + Arrays.toString(array1));
        assertEquals(Integer.valueOf(0), executor.submit(new MinFinderTask(array1, executor)).get(TIMEOUT_NS * 5, TimeUnit.NANOSECONDS));

        int[] array2 = new int[] { 5, 20, 18, 73, 64, 102, 6, 62, 12, 31 };
        System.out.println("Searching of minimum of " + Arrays.toString(array2));
        assertEquals(Integer.valueOf(5), executor.submit(new MinFinderTask(array2, executor)).get(TIMEOUT_NS * 5, TimeUnit.NANOSECONDS));

        int[] array3 = new int[] { 80, 20, 40, 70, 30, 90, 90, 50, 10 };
        System.out.println("Searching of minimum of " + Arrays.toString(array3));
        assertEquals(Integer.valueOf(10), executor.submit(new MinFinderTask(array3, executor)).get(TIMEOUT_NS * 5, TimeUnit.NANOSECONDS));

        // expect immediate termination after shutdown with no tasks remaining
        executor.shutdown();
        assertTrue(executor.awaitTermination(0, TimeUnit.NANOSECONDS));
    }

    // Use a policy executor to submit tasks that await termination of itself.
    // Also uses the executor to submit tasks that shut itself down.
    @Test
    public void testSelfAwaitTermination() throws Exception {
        final ExecutorService executor = provider.create("testSelfAwaitTermination");

        // Submit a task to await termination of the executor
        Future<Boolean> future1 = executor.submit(new TerminationAwaitTask(executor, TimeUnit.MILLISECONDS.toNanos(50)));
        assertFalse(future1.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(future1.isDone());
        assertFalse(executor.isShutdown());
        assertFalse(executor.isTerminated());

        // Submit another task to await termination of same executor.
        CountDownLatch beginLatch = new CountDownLatch(1);
        TerminationAwaitTask awaitTerminationTask = new TerminationAwaitTask(executor, TimeUnit.MINUTES.toNanos(20), beginLatch, null, 0);
        Future<Boolean> future2 = executor.submit(awaitTerminationTask);

        // Wait for the above task to start
        beginLatch.await(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        // and then encourage it to start awaiting termination
        TimeUnit.MILLISECONDS.sleep(100);

        // Run shutdown and shutdownNow from tasks submitted by the executor.
        // We must submit both tasks before either issues the shutdown because shutdown prevents subsequent submits.
        CountDownLatch shutdownLatch = new CountDownLatch(1);
        CountDownLatch shutdownNowLatch = new CountDownLatch(1);
        Future<List<Runnable>> shutdownFuture = executor.submit(new ShutdownTask(executor, false, beginLatch/*no-op*/, shutdownLatch, TimeUnit.MINUTES.toNanos(10)));
        Future<List<Runnable>> shutdownNowFuture = executor.submit(new ShutdownTask(executor, true, beginLatch/*no-op*/, shutdownNowLatch, TimeUnit.MINUTES.toNanos(10)));

        // let the shutdown task run
        shutdownLatch.countDown();
        assertNull(shutdownFuture.get());
        assertTrue(executor.isShutdown());

        try {
            fail("Task awaiting termination shouldn't stop when executor shuts down via shutdown: " + future2.get(100, TimeUnit.NANOSECONDS));
        } catch (TimeoutException x) {} // pass

        assertFalse(executor.isTerminated());

        // let the shutdownNow task run
        shutdownNowLatch.countDown();
        try {
            List<Runnable> tasksCanceledFromQueue = shutdownNowFuture.get();
            assertEquals(0, tasksCanceledFromQueue.size());
        } catch (CancellationException x) {} // pass if cancelled due to shutdownNow

        try {
            fail("Task awaiting termination shouldn't succeed when executor shuts down via shutdownNow: " + future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        Throwable x = awaitTerminationTask.errorOnAwait.poll(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        assertNotNull(x);
        if (!(x instanceof InterruptedException))
            throw new RuntimeException("Unexpected error from awaitTermination task after shutdownNow. See cause.", x);

        assertTrue(executor.awaitTermination(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(executor.isTerminated());
    }

    // Submit a task that cancels itself.
    @Test
    public void testSelfCancellation() throws Exception {
        ExecutorService executor = provider.create("testSelfCancellation");
        final LinkedBlockingQueue<Future<Void>> futures = new LinkedBlockingQueue<Future<Void>>();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws InterruptedException {
                Future<Void> future = futures.poll(TIMEOUT_NS, TimeUnit.NANOSECONDS);
                assertNotNull(future);
                assertTrue(future.cancel(true));

                // Perform some operation that should be rejected due to interrupting this thread
                TimeUnit.NANOSECONDS.sleep(TIMEOUT_NS * 2);
                return null;
            }
        });

        futures.add(future);

        try {
            fail("Future for self cancelling task returned " + future.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        } catch (CancellationException x) {} // pass

        assertTrue(future.isCancelled());
        assertTrue(future.isDone());
    }

    //Ensure that a policy executor can be obtained from the injected provider
    @Test
    public void testGetPolicyExecutor() throws Exception {
        provider.create("testGetPolicyExecutor").maxConcurrency(2);
    }
    
    //Ensure that two tasks are run and the third is queued when three tasks are submitted and max concurrency is 2
    @Test
    public void testMaxConcurrencyBasic() throws Exception {
        PolicyExecutor executor = provider.create("testMaxConcurrencyBasic")
                        .maxConcurrency(2)
                        .maxQueueSize(1)
                        .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(1))
                        .queueFullAction(QueueFullAction.Abort);

        CountDownLatch beginLatch = new CountDownLatch(3);
        CountDownLatch continueLatch = new CountDownLatch(1);
        CountDownTask task = new CountDownTask(beginLatch, continueLatch, TimeUnit.HOURS.toNanos(1));

        //This task should start and block on continueLatch
        Future<Boolean> future1 = executor.submit(task);
        //This task should start and block on continueLatch
        Future<Boolean> future2 = executor.submit(task);
        //This task should be queued since we should be at max concurrency
        Future<Boolean> future3 = executor.submit(task);
        Future<Boolean> future4 = null;
        
        //Shorten maxWaitForEnqueue so we the test doesn't have to wait long for the timeout
        executor.maxWaitForEnqueue(200);

        try {
            //This task should be aborted since the queue should be full, triggering a RejectedExecutionException
            future4 = executor.submit(task);

            fail("The fourth task should have thrown a RejectedExecutionException when attempting to queue");

        } catch (RejectedExecutionException x) {
        } //expected

        //Let the three tasks complete
        continueLatch.countDown();

        assertTrue(future1.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(future3.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        executor.shutdownNow();
    }

    //Test updating maxConcurrency:
    //The test begins with maxConcurrency of 1 and one task is submitted and runs
    //The maxConcurrency is increased to two and another task is submitted and should run
    //Submit two more tasks, one should queue and one should abort
    //Increase the maxConcurrency to 3 and queue size to 2
    //Submit another task which should cause the queued task to run and then this task will queue
    //Then decrease MaxConcurrency to 2 and queue size to 1
    //Allow the third submitted task to complete and submit another, which should abort since there are
    //two tasks running and one in the queue
    @Test
    public void testUpdateMaxConcurrency() throws Exception {
        PolicyExecutor executor = provider.create("testUpdateMaxConcurrency")
                        .maxConcurrency(1)
                        .maxQueueSize(1)
                        .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(1))
                        .queueFullAction(QueueFullAction.Abort);

        CountDownLatch beginLatch1 = new CountDownLatch(2);
        CountDownLatch continueLatch1 = new CountDownLatch(1);
        CountDownTask task1 = new CountDownTask(beginLatch1, continueLatch1, TimeUnit.HOURS.toNanos(1));
        CountDownLatch beginLatch2 = new CountDownLatch(1);
        CountDownLatch continueLatch2 = new CountDownLatch(1);
        CountDownTask task2 = new CountDownTask(beginLatch2, continueLatch2, TimeUnit.HOURS.toNanos(1));

        //This task should start and block on continueLatch
        Future<Boolean> future1 = executor.submit(task1);
        executor.maxConcurrency(2);
        //This task should start and block on continueLatch since maxConcurrency was just increased
        Future<Boolean> future2 = executor.submit(task1);

        //Ensure both tasks are running
        assertTrue(beginLatch1.await(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        //This task should be queued since we should be at max concurrency
        Future<Boolean> future3 = executor.submit(task2);
        Future<Boolean> future4 = null;
        
        //Shorten maxWaitForEnqueue so we the test doesn't have to wait long for the timeout
        executor.maxWaitForEnqueue(200);

        try {
            //This task should be aborted since the queue should be full, triggering a RejectedExecutionException
            future4 = executor.submit(task1);

            fail("The fourth task should have thrown a RejectedExecutionException when attempting to queue");

        } catch (RejectedExecutionException x) {
        } //expected
        
        //Return maxWaitForEnqueue to a one minute timeout so it doesn't timeout on a slow machine
        executor.maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(1));

        //Changing maxConcurrency to 3
        executor.maxConcurrency(3).maxQueueSize(2);
        
        //TODO: Update test once polling has been added to run tasks from the queue after
        //maxConcurrency has been increased
        Future<Boolean> future5 = executor.submit(task2);
        assertTrue(beginLatch2.await(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        //Setting the maxConcurrency lower than the current number of tasks running should be allowed
        //Also set the queue size back to 1 so that the queue is full again
        executor.maxConcurrency(2).maxQueueSize(1);

        //Allow the third task to complete
        continueLatch2.countDown();
        
        assertTrue(future3.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
 
        //Shorten maxWaitForEnqueue so the test doesn't have to wait long for the timeout
        executor.maxWaitForEnqueue(200);

        try {
            //This task should be aborted since the queue should be full and
            //there are two tasks running, triggering a RejectedExecutionException
            future4 = executor.submit(task1);

            fail("The task future4 should have thrown a RejectedExecutionException when attempting to queue");

        } catch (RejectedExecutionException x) {
        } //expected

        //Let the three tasks complete
        continueLatch1.countDown();

        assertTrue(future1.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(future5.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        executor.shutdownNow();
    }

    //Test that changing the maxConcurrency of one executor does not affect a different executor
    @Test
    public void testMaxConcurrencyMultipleExecutors() throws Exception {
        PolicyExecutor executor1 = provider.create("testMaxConcurrencyMultipleExecutors-1")
                        .maxConcurrency(1)
                        .maxQueueSize(1)
                        .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(1))
                        .queueFullAction(QueueFullAction.Abort);
        PolicyExecutor executor2 = provider.create("testMaxConcurrencyMultipleExecutors-2")
                        .maxConcurrency(1)
                        .maxQueueSize(1)
                        .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(1))
                        .queueFullAction(QueueFullAction.Abort);

        CountDownLatch beginLatch = new CountDownLatch(3);
        CountDownLatch continueLatch = new CountDownLatch(1);
        CountDownTask task = new CountDownTask(beginLatch, continueLatch, TimeUnit.HOURS.toNanos(1));

        //Should run and block on continue latch
        Future<Boolean> future1 = executor1.submit(task);

        //Should run and block on continue latch
        Future<Boolean> future2 = executor2.submit(task);

        executor1.maxConcurrency(2);

        //This task should be queued since we should be at max concurrency in executor 1
        Future<Boolean> future3 = executor2.submit(task);
        Future<Boolean> future4 = null;
        
        //Shorten maxWaitForEnqueue so the test doesn't have to wait long for the timeout
        executor2.maxWaitForEnqueue(200);

        try {
            //This task should be aborted since the queue should be full, triggering a RejectedExecutionException
            future4 = executor2.submit(task);

            fail("The third task on executor2 should have thrown a RejectedExecutionException when attempting to queue");

        } catch (RejectedExecutionException x) {
        } //expected

        //Let the three tasks complete
        continueLatch.countDown();

        assertTrue(future1.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(future3.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        executor1.shutdownNow();

        executor2.shutdownNow();
    }
    
    //Concurrently submit two tasks to change the maxConcurrency.  Ensure that afterward the maxConcurrency
    //is one of the two submitted values
    @Test
    public void testConcurrentUpdateMaxConcurrency() throws Exception {
        PolicyExecutor executor = provider.create("testConcurrentUpdateMaxConcurrency")
                .maxConcurrency(2)
                .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(1))
                .queueFullAction(QueueFullAction.Abort)
        		.maxQueueSize(1);
        
        CountDownLatch beginLatch = new CountDownLatch(2);
        CountDownLatch continueLatch1 = new CountDownLatch(1);
        
        CountDownLatch continueLatch2 = new CountDownLatch(1);
        
        ConfigChangeTask configTask1 = new ConfigChangeTask(executor, beginLatch, continueLatch1, TIMEOUT_NS, "maxConcurrency", "1");
        ConfigChangeTask configTask2 = new ConfigChangeTask(executor, beginLatch, continueLatch1, TIMEOUT_NS, "maxConcurrency", "3");
        CountDownTask countDownTask = new CountDownTask(new CountDownLatch(0), continueLatch2, TIMEOUT_NS);
        
        //Submit the two tasks that will change the maxConcurrency
        Future<Boolean> future1 = testThreads.submit(configTask1);
        Future<Boolean> future2 = testThreads.submit(configTask2);
        
        //Wait for the two tasks to begin running
        assertTrue(beginLatch.await(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        
        //Allow the two tasks to change the maxConcurrency and complete
        continueLatch1.countDown();
        assertTrue(future1.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(future2.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        
        //Now we need to check that the maxConcurrency is either 1 or 3
        
        //This should be the first task run
        Future<Boolean> future3 = executor.submit(countDownTask);
        
        //This task should queue if maxConcurrency=1, otherwise should run
        Future<Boolean> future4 = executor.submit(countDownTask);
        Future<Boolean> future5 = null;
        boolean caughtException = false;
        //Decrease to 1s so that we aren't waiting too long if maxConcurrency is 1
        //However this can't be too short that it is hit as tasks queue and run in the case that
        //maxConcurrency is 3 on a slow machine
        executor.maxWaitForEnqueue(1000);
        
        try {
            //This task will be aborted if maxConcurrency = 1, otherwise should run
            future5 = executor.submit(countDownTask);
        } catch (RejectedExecutionException x) {
        	caughtException = true; //expected if maxConcurrency = 1
        }
        
        executor.maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(1));
        Future<Boolean> future6 = null;
        Future<Boolean> future7 = null;
        if(caughtException == false) {
        	//We should be at maxConcurrency of 3 here, so this should queue
        	future6 = executor.submit(countDownTask);
            //Decrease to 200 ms so that we aren't waiting too long for timeout
        	executor.maxWaitForEnqueue(200);
            try {
                //This task will be aborted if maxConcurrency = 3
                future7 = executor.submit(countDownTask);
            } catch (RejectedExecutionException x) {
            	caughtException = true; //expected if maxConcurrency = 3
            }
        }
        
        assertTrue("maxConcurrency should be either 1 or 3",caughtException);

        //Let the submitted tasks complete
        continueLatch2.countDown();

        assertTrue(future3.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(future4.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        if(future5 != null)
        	assertTrue(future5.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        if(future6 != null)
        	assertTrue(future6.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));

        executor.shutdownNow();   
    }
    
    //Test that maxConcurrency cannot be called after shutdown
    @Test
    public void testUpdateMaxConcurrencyAfterShutdown() {
        PolicyExecutor executor = provider.create("updateMaxConcurrencyAfterShutdown")
                .maxConcurrency(2);
        
        executor.shutdown();
        try {
        	executor.maxConcurrency(5);
        	fail("Should not be allowed to change maxConcurrency after calling shutdown");
        } catch(IllegalStateException e) { //expected
        }
    }

}
