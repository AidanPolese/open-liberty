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

import org.junit.Test;

import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFunction;

/**
 *
 */
public class RetryTest {

    @Test
    public void testRetry() {
        RetryPolicy retry = FaultToleranceProvider.newRetryPolicy();
        retry.setMaxRetries(3);

        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setRetryPolicy(retry);
        Executor<String, String> executor = builder.build();

        TestFunction callable = new TestFunction(2, "testRetry");//first two executions will throw an exception

        executor.execute(callable, "testRetry");
        assertEquals(3, callable.getExecutions());
    }

}
