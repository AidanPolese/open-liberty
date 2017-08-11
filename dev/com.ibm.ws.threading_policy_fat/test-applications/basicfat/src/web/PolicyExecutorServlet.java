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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import java.util.concurrent.TimeUnit;
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
    private static final long TIMEOUT_NS = TimeUnit.MINUTES.toNanos(2);

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
        continueLatch.countDown(); // let the Callable run without blocking
        assertEquals(1, beginLatch.getCount());
        canceledFromQueue.iterator().next().run();
        assertEquals(0, beginLatch.getCount());
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
        PolicyExecutor executor1 = provider.create("testQueueSizeMultipleExecutors-1")
                        .maxConcurrency(1)
                        .maxQueueSize(1)
                        .maxWaitForEnqueue(TimeUnit.MINUTES.toMillis(1))
                        .queueFullAction(QueueFullAction.Abort);
        PolicyExecutor executor2 = provider.create("testQueueSizeMultipleExecutors-2")
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

}
