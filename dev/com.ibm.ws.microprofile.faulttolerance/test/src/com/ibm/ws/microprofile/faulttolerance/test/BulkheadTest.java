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
import static org.junit.Assert.fail;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;
import org.junit.Test;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
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

        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setBulkheadPolicy(bulkhead);
        Executor<String, String> executor = builder.build();

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Future<String> task1 = executorService.submit(new TestTask(executor, Duration.ofMillis(2000), "testBulkhead1"));
        Future<String> task2 = executorService.submit(new TestTask(executor, Duration.ofMillis(2000), "testBulkhead2"));
        Thread.sleep(100);
        Future<String> task3 = executorService.submit(new TestTask(executor, Duration.ofMillis(2000), "testBulkhead3"));
        Future<String> task4 = executorService.submit(new TestTask(executor, Duration.ofMillis(2000), "testBulkhead4"));

        String executions1 = task1.get(2300, TimeUnit.MILLISECONDS); //if we allow just over 2000ms then the first two should complete
        assertEquals("testBulkhead1", executions1);
        String executions2 = task2.get(100, TimeUnit.MILLISECONDS);
        assertEquals("testBulkhead2", executions2);

        assertFalse(task3.isDone());
        assertFalse(task4.isDone());

        String executions3 = task3.get(2300, TimeUnit.MILLISECONDS); //if we allow just over 2000ms then the next two should complete
        assertEquals("testBulkhead3", executions3);
        String executions4 = task4.get(100, TimeUnit.MILLISECONDS);
        assertEquals("testBulkhead4", executions4);

    }

    @Test
    public void testAsyncBulkhead() throws InterruptedException, ExecutionException, TimeoutException {
        BulkheadPolicy bulkhead = FaultToleranceProvider.newBulkheadPolicy();
        bulkhead.setMaxThreads(20);

        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setBulkheadPolicy(bulkhead);

        Executor<String, Future<String>> executor = builder.buildAsync();

        Future<String>[] futures = new Future[10];
        for (int i = 0; i < 10; i++) {
            AsyncTestFunction callable = new AsyncTestFunction(Duration.ofMillis(2000), "testAsyncBulkhead" + i);
            Future<String> future = executor.execute(callable, "testAsyncBulkhead");
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

        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setBulkheadPolicy(bulkhead);

        Executor<String, Future<String>> executor = builder.buildAsync();

        for (int i = 0; i < 4; i++) {
            String context = "testAsyncBulkheadQueueFull" + i;
            AsyncTestFunction callable = new AsyncTestFunction(Duration.ofMillis(2000), context);
            Future<String> future = executor.execute(callable, context);
            System.out.println(System.currentTimeMillis() + " Test " + context + " - submitted");
            assertFalse(future.isDone());
            Thread.sleep(100);
        }

        String context = "testAsyncBulkheadQueueFull4";
        AsyncTestFunction callable = new AsyncTestFunction(Duration.ofMillis(2000), context);
        try {
            executor.execute(callable, context);
            System.out.println(System.currentTimeMillis() + " Test " + context + " - submitted");
            fail("Exception not thrown");
        } catch (BulkheadException e) {
            //expected
        }

    }

}
