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
package com.ibm.ws.microprofile.faulttolerance.test.util;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.Callable;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;

import com.ibm.ws.microprofile.faulttolerance.spi.Execution;

/**
 *
 */
public class TestTask implements Callable<String> {

    private final Execution<String> executor;
    private final TestFunction testFunction;
    private final ExecutionContext context;

    /**
     * @param executor
     */
    public TestTask(Execution<String> executor, Duration callLength, String contextData) {
        this.executor = executor;
        this.testFunction = new TestFunction(callLength, contextData);
        this.context = new ExecutionContext() {

            @Override
            public Object[] getParameters() {
                return new String[] { contextData };
            }

            @Override
            public Method getMethod() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    public String call() {
        String execution = this.executor.execute(testFunction, context);
        return execution;
    }

}
