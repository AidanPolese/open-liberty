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

import com.ibm.ws.microprofile.faulttolerance.spi.Execution;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutionBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.test.util.ExecutionContextImpl;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFallback;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFallbackFactory;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFunction;

/**
 *
 */
public class FallbackTest {

    @Test
    public void testFallbackFunction() {
        FallbackPolicy<String> fallback = FaultToleranceProvider.newFallbackPolicy();
        TestFallback fallbackCallable = new TestFallback();
        fallback.setFallbackFunction(fallbackCallable);

        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setFallbackPolicy(fallback);
        Execution<String> executor = builder.build();

        TestFunction callable = new TestFunction(-1, "testFallback");

        //callable is set to always throw an exception but the fallback should be run instead
        String executions = executor.execute(callable, new ExecutionContextImpl("testFallback"));
        assertEquals("Fallback: testFallback", executions);
    }

    @Test
    public void testFallbackFactory() {
        FallbackPolicy<String> fallback = FaultToleranceProvider.newFallbackPolicy();
        TestFallbackFactory fallbackFactory = new TestFallbackFactory();
        fallback.setFallbackHandler(TestFallback.class, fallbackFactory);

        ExecutionBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setFallbackPolicy(fallback);

        Execution<String> executor = builder.build();

        TestFunction callable = new TestFunction(-1, "testFallback");

        //callable is set to always throw an exception but the fallback should be run instead
        String executions = executor.execute(callable, new ExecutionContextImpl("testFallback"));
        assertEquals("Fallback: testFallback", executions);
    }

}
