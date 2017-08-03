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

import org.junit.Test;

import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;
import com.ibm.ws.microprofile.faulttolerance.test.util.ExecutionContextImpl;
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
        Executor<String> executor = builder.build();

        TestFunction callable = new TestFunction(2, "testRetry");//first two executions will throw an exception

        executor.execute(callable, new ExecutionContextImpl("testRetry"));
        assertEquals(3, callable.getExecutions());
    }

}
