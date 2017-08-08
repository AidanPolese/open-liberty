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
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;

import org.junit.Test;


import com.ibm.ws.threading.PolicyExecutor.QueueFullAction;
import com.ibm.ws.threading.PolicyExecutorProvider;

import componenttest.annotation.ExpectedFFDC;

import componenttest.app.FATServlet;

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
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/PolicyExecutorServlet")
public class PolicyExecutorServlet extends FATServlet {
    // Maximum number of nanoseconds to wait for a task to complete
    private static final long TIMEOUT_NS = TimeUnit.MINUTES.toNanos(2);

    @Resource(lookup = "test/TestPolicyExecutorProvider")
    private PolicyExecutorProvider provider;


    // Await termination of a policy executor before asking it to shut down.
    // Submit 6 tasks such that, at the time of shutdown, 2 are running, 2 are queued, and 2 are awaiting queue positions.
    // Verify that it reports successful termination after (but not before) shutdown is requested
    // and that the running and queued tasks are allowed to complete, whereas the 2 tasks awaiting queue positions are rejected.
    @ExpectedFFDC("java.util.concurrent.RejectedExecutionException")
    @Test
    public void testAwaitTerminationWhileActiveThenShutdown() throws Exception {
        // A thread from executor1 is used to await executor2 
        ExecutorService executor1 = provider.create("testAwaitTerminationWhileActiveThenShutdown-1")
                .maxConcurrency(3).maxQueueSize(3).queueFullAction(QueueFullAction.Abort);

        ExecutorService executor2 = provider.create("testAwaitTerminationWhileActiveThenShutdown-2")
                .maxConcurrency(2).maxQueueSize(2).maxWaitForEnqueue(TimeUnit.SECONDS.toMillis(1)).queueFullAction(QueueFullAction.Abort);

        Future<Boolean> terminationFuture = executor1.submit(new TerminationAwaitTask(executor2, TimeUnit.MINUTES.toNanos(5)));
        assertFalse(terminationFuture.isDone());

        CountDownLatch beginLatch = new CountDownLatch(2);
        CountDownLatch continueLatch = new CountDownLatch(1);
        CountDownTask task = new CountDownTask(beginLatch, continueLatch, TimeUnit.HOURS.toNanos(1));

        Future<Boolean> future1 = executor2.submit(task); // should start, decrement the beginLatch, and block on continueLatch
        Future<Boolean> future2 = executor2.submit(task); // should start, decrement the beginLatch, and block on continueLatch
        assertTrue(beginLatch.await(TIMEOUT_NS, TimeUnit.MILLISECONDS));

        Future<Boolean> future3 = executor2.submit(task); // should be queued
        Future<Boolean> future4 = executor2.submit(task); // should be queued

        Future<Future<Boolean>> future5 = executor1.submit(new SubmitterTask<Boolean>(executor2, task)); // should wait for queue position
        Future<Future<Boolean>> future6 = executor1.submit(new SubmitterTask<Boolean>(executor2, task)); // should wait for queue position

        assertFalse(executor2.isShutdown());
        assertFalse(executor2.isTerminated());
        assertFalse(terminationFuture.isDone());

        executor2.shutdown();

        try {
            fail("Should not be able submit new task after shutdown: " + executor2.submit(new SharedIncrementTask(), "Should not be able to submit this"));
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

        assertTrue(executor2.isShutdown());
        assertFalse(executor2.isTerminated());
        assertFalse(terminationFuture.isDone()); // still blocked on the continueLatch

        continueLatch.countDown();

        assertTrue(terminationFuture.get(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        assertTrue(executor2.isShutdown());
        assertTrue(executor2.isTerminated());
        assertTrue(terminationFuture.isDone()); // still blocked on the continueLatch

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

        executor1.shutdown();
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

    //Ensure that a policy executor can be obtained from the injected provider
    @Test
    public void testGetPolicyExecutor() throws Exception {
        provider.create("testGetPolicyExecutor").maxConcurrency(2);
    }
}
