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
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFallback;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFunction;

/**
 *
 */
public class FallbackTest {

    @Test
    public void testFallback() {
        FallbackPolicy<String, String> fallback = FaultToleranceProvider.newFallbackPolicy();
        TestFallback fallbackCallable = new TestFallback();
        fallback.setFallback(fallbackCallable);

        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setFallbackPolicy(fallback);
        Executor<String, String> executor = builder.build();

        TestFunction callable = new TestFunction(-1, "testFallback");

        //callable is set to always throw an exception but the fallback should be run instead
        String executions = executor.execute(callable, "testFallback");
        assertEquals("Fallback: testFallback", executions);
    }

}
