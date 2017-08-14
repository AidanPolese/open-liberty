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

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.test.util.AsyncTestFunction;
import com.ibm.ws.microprofile.faulttolerance.test.util.ExecutionContextImpl;

/**
 *
 */
public class AsyncTest {

    private static final int TASKS = 5;
    private static final long DURATION_UNIT = 1000;
    private static final long TASK_DURATION = 4 * DURATION_UNIT;
    private static final long FUTURE_TIMEOUT = 2 * TASK_DURATION;

    @Test
    public void testAsync() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        Executor<Future<String>> executor = builder.buildAsync();

        Future<String>[] futures = new Future[TASKS];
        for (int i = 0; i < TASKS; i++) {
            AsyncTestFunction callable = new AsyncTestFunction(Duration.ofMillis(TASK_DURATION), "testAsync" + i);
            Future<String> future = executor.execute(callable, new ExecutionContextImpl(null, "testAsync"));
            assertFalse(future.isDone());
            futures[i] = future;
        }

        for (int i = 0; i < TASKS; i++) {
            String data = futures[i].get(FUTURE_TIMEOUT, TimeUnit.MILLISECONDS);
            assertEquals("testAsync" + i, data);
        }
    }

}
