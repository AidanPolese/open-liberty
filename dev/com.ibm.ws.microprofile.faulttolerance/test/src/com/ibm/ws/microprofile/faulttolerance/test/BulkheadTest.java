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
package com.ibm.ws.microprofile.faulttolerance.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;
import org.junit.Test;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutorBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.test.util.AsyncTestFunction;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestTask;

/**
 *
 */
public class BulkheadTest {

    @Test
    public void testBulkhead() throws InterruptedException, ExecutionException, TimeoutException {
        BulkheadPolicy bulkhead = FaultToleranceProvider.newBulkheadPolicy();
        bulkhead.setMaxThreads(2);

        ExecutorBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setBulkheadPolicy(bulkhead);
        Executor<String> executor = builder.build();

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        TestTask testTask1 = new TestTask(executor, Duration.ofMillis(2000), "testBulkhead1");
        TestTask testTask2 = new TestTask(executor, Duration.ofMillis(2000), "testBulkhead2");
        TestTask testTask3 = new TestTask(executor, Duration.ofMillis(2000), "testBulkhead3");
        TestTask testTask4 = new TestTask(executor, Duration.ofMillis(2000), "testBulkhead4");

        long start = System.nanoTime();
        Future<String> task1 = executorService.submit(testTask1);
        Future<String> task2 = executorService.submit(testTask2);
        System.out.println(timeDiff(start) + " - First two tasks submitted");
        Thread.sleep(100); //allow the first two to be picked up from the queue and begin execution ... (test) queue should then be clear
        System.out.println(timeDiff(start) + " - Submitting next two");
        Future<String> task3 = executorService.submit(testTask3);
        Future<String> task4 = executorService.submit(testTask4);
        System.out.println(timeDiff(start) + " - All four submitted");

        String executions1 = task1.get(2300, TimeUnit.MILLISECONDS); //if we allow just over 2000ms then the first two should complete
        System.out.println(timeDiff(start) + " - task1 got");
        assertEquals("testBulkhead1", executions1);
        String executions2 = task2.get(100, TimeUnit.MILLISECONDS);
        System.out.println(timeDiff(start) + " - task2 got");
        assertEquals("testBulkhead2", executions2);

        try {
            String executions3 = task3.get(100, TimeUnit.MILLISECONDS);
            fail("Task3 should have failed");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            assertTrue(cause + " was not a ExecutionException", cause instanceof org.eclipse.microprofile.faulttolerance.exceptions.ExecutionException);
            cause = cause.getCause();
            assertTrue(cause + " was not a BulkheadException", cause instanceof BulkheadException);
        }
        try {
            String executions4 = task4.get(100, TimeUnit.MILLISECONDS);
            fail("Task4 should have failed");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            assertTrue(cause + " was not a ExecutionException", cause instanceof org.eclipse.microprofile.faulttolerance.exceptions.ExecutionException);
            cause = cause.getCause();
            assertTrue(cause + " was not a BulkheadException", cause instanceof BulkheadException);
        }

    }

    private static String timeDiff(long relativePoint) {
        long now = System.nanoTime();
        long diff = now - relativePoint;
        double seconds = ((double) diff / (double) 1000000000);
        return "" + seconds + "s";
    }

    @Test
    public void testAsyncBulkhead() throws InterruptedException, ExecutionException, TimeoutException {
        BulkheadPolicy bulkhead = FaultToleranceProvider.newBulkheadPolicy();
        bulkhead.setMaxThreads(20);
        bulkhead.setQueueSize(20);

        ExecutorBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setBulkheadPolicy(bulkhead);

        Executor<Future<String>> executor = builder.buildAsync();

        Future<String>[] futures = new Future[10];
        for (int i = 0; i < 10; i++) {
            AsyncTestFunction callable = new AsyncTestFunction(Duration.ofMillis(2000), "testAsyncBulkhead" + i);
            ExecutionContext context = executor.newExecutionContext((Method) null, "testAsyncBulkhead");
            Future<String> future = executor.execute(callable, context);
            assertFalse(future.isDone());
            futures[i] = future;
        }

        for (int i = 0; i < 10; i++) {
            String data = futures[i].get(2300, TimeUnit.MILLISECONDS);
            assertEquals("testAsyncBulkhead" + i, data);
        }
    }

    @Test
    public void testAsyncBulkheadQueueFull() throws InterruptedException, ExecutionException, TimeoutException {
        BulkheadPolicy bulkhead = FaultToleranceProvider.newBulkheadPolicy();
        bulkhead.setMaxThreads(2);
        bulkhead.setQueueSize(2);

        ExecutorBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setBulkheadPolicy(bulkhead);

        Executor<Future<String>> executor = builder.buildAsync();

        for (int i = 0; i < 4; i++) {
            String contextData = "testAsyncBulkheadQueueFull" + i;
            ExecutionContext context = executor.newExecutionContext((Method) null, contextData);
            AsyncTestFunction callable = new AsyncTestFunction(Duration.ofMillis(2000), contextData);
            Future<String> future = executor.execute(callable, context);
            System.out.println(System.currentTimeMillis() + " Test " + context + " - submitted");
            assertFalse(future.isDone());
            Thread.sleep(100);
        }

        String contextData = "testAsyncBulkheadQueueFull4";
        ExecutionContext context = executor.newExecutionContext((Method) null, contextData);
        AsyncTestFunction callable = new AsyncTestFunction(Duration.ofMillis(2000), contextData);
        try {
            executor.execute(callable, context);
            System.out.println(System.currentTimeMillis() + " Test " + contextData + " - submitted");
            fail("Exception not thrown");
        } catch (BulkheadException e) {
            //expected
        }

    }

}
