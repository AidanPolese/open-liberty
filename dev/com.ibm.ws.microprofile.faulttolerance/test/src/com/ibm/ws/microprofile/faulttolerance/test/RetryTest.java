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

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.junit.Test;

import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutorBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.test.util.AsyncTestFunction;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFunction;

/**
 *
 */
public class RetryTest {

    @Test
    public void testRetry() {
        RetryPolicy retry = FaultToleranceProvider.newRetryPolicy();
        retry.setMaxRetries(3);

        ExecutorBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setRetryPolicy(retry);

        Executor<String> executor = builder.build();

        TestFunction callable = new TestFunction(2, "testRetry");//first two executions will throw an exception

        ExecutionContext context = executor.newExecutionContext((Method) null, "testRetry");
        executor.execute(callable, context);
        assertEquals(3, callable.getExecutions());
    }

    @Test
    public void testAsyncRetry() throws InterruptedException, ExecutionException, TimeoutException {
        RetryPolicy retry = FaultToleranceProvider.newRetryPolicy();
        retry.setMaxRetries(3);

        ExecutorBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setRetryPolicy(retry);

        Executor<Future<String>> executor = builder.buildAsync();

        AsyncTestFunction callable = new AsyncTestFunction(2, "testRetry");//first two executions will throw an exception

        ExecutionContext context = executor.newExecutionContext((Method) null, "testRetry");
        Future<String> future = executor.execute(callable, context);
        String result = future.get(1000, TimeUnit.MILLISECONDS);
        assertEquals("testRetry", result);
        assertEquals(3, callable.getExecutions());
    }

}
