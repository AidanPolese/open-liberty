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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;
import org.junit.Test;

import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestException;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFunction;

/**
 *
 */
public class CircuitBreakerTest {

    @Test
    public void testCircuitBreaker() {
        CircuitBreakerPolicy circuitBreaker = FaultToleranceProvider.newCircuitBreakerPolicy();
        circuitBreaker.setFailureRatio(1.0);
        circuitBreaker.setRequestVolumeThreshold(2);

        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setCircuitBreakerPolicy(circuitBreaker);
        Executor<String, String> executor = builder.build();

        TestFunction callable = new TestFunction(-1, "testCircuitBreaker");

        String executions = "NOT_RUN";
        try {
            executions = executor.execute(callable, "testCircuitBreaker1");
            fail("Exception not thrown");
        } catch (FaultToleranceException t) {
            //expected
            assertTrue(t.getCause() instanceof TestException);
        }
        try {
            executions = executor.execute(callable, "testCircuitBreaker2");
            fail("Exception not thrown");
        } catch (FaultToleranceException t) {
            //expected
            assertTrue(t.getCause() instanceof TestException);
        }
        try {
            executions = executor.execute(callable, "testCircuitBreaker3");
            fail("Exception not thrown");
        } catch (CircuitBreakerOpenException t) {
            //expected
        }
        assertEquals("NOT_RUN", executions);
    }

}
