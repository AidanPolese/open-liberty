/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
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
        Executor<String, Future<String>> executor = builder.buildAsync();

        Future<String>[] futures = new Future[TASKS];
        for (int i = 0; i < TASKS; i++) {
            AsyncTestFunction callable = new AsyncTestFunction(Duration.ofMillis(TASK_DURATION), "testAsync" + i);
            Future<String> future = executor.execute(callable, "testAsync");
            assertFalse(future.isDone());
            futures[i] = future;
        }

        for (int i = 0; i < TASKS; i++) {
            String data = futures[i].get(FUTURE_TIMEOUT, TimeUnit.MILLISECONDS);
            assertEquals("testAsync" + i, data);
        }
    }

}
