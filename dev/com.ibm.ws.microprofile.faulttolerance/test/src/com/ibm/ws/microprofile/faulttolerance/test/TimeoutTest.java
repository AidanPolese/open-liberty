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
import static org.junit.Assert.fail;

import java.time.Duration;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;
import org.junit.Test;

import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFunction;

/**
 *
 */
public class TimeoutTest {

    @Test
    public void testTimeout() {
        TimeoutPolicy timeout = FaultToleranceProvider.newTimeoutPolicy();
        timeout.setTimeout(Duration.ofMillis(500));

        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setTimeoutPolicy(timeout);
        Executor<String, String> executor = builder.build();

        TestFunction callable = new TestFunction(Duration.ofMillis(1000), "testTimeout");

        String executions = "NOT_RUN";
        try {
            executions = executor.execute(callable, "testTimeout");
            fail("Exception not thrown");
        } catch (TimeoutException t) {
            //expected
        }
        assertEquals("NOT_RUN", executions);
    }

}
