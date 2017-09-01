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

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.junit.Test;

import com.ibm.ws.microprofile.faulttolerance.spi.Executor;
import com.ibm.ws.microprofile.faulttolerance.spi.ExecutorBuilder;
import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceProvider;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFallback;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFallbackFactory;
import com.ibm.ws.microprofile.faulttolerance.test.util.TestFunction;

/**
 *
 */
public class FallbackTest {

    @Test
    public void testFallbackFunction() {
        FallbackPolicy fallback = FaultToleranceProvider.newFallbackPolicy();
        TestFallback fallbackCallable = new TestFallback();
        fallback.setFallbackFunction(fallbackCallable);

        ExecutorBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setFallbackPolicy(fallback);
        Executor<String> executor = builder.build();

        TestFunction callable = new TestFunction(-1, "testFallback");

        //callable is set to always throw an exception but the fallback should be run instead
        ExecutionContext context = executor.newExecutionContext((Method) null, "testFallback");
        String executions = executor.execute(callable, context);
        assertEquals("Fallback: testFallback", executions);
    }

    @Test
    public void testFallbackFactory() {
        FallbackPolicy fallback = FaultToleranceProvider.newFallbackPolicy();
        TestFallbackFactory fallbackFactory = new TestFallbackFactory();
        fallback.setFallbackHandler(TestFallback.class, fallbackFactory);

        ExecutorBuilder<String, String> builder = FaultToleranceProvider.newExecutionBuilder();
        builder.setFallbackPolicy(fallback);

        Executor<String> executor = builder.build();

        TestFunction callable = new TestFunction(-1, "testFallback");

        //callable is set to always throw an exception but the fallback should be run instead
        ExecutionContext context = executor.newExecutionContext((Method) null, "testFallback");
        String executions = executor.execute(callable, context);
        assertEquals("Fallback: testFallback", executions);
    }

}
