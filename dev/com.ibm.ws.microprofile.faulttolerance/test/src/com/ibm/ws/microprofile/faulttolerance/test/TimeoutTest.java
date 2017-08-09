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
