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

import java.time.Duration;
import java.util.concurrent.Callable;

import com.ibm.ws.microprofile.faulttolerance.spi.Executor;

/**
 *
 */
public class TestTask implements Callable<String> {

    private final Executor<String, String> executor;
    private final TestFunction testFunction;
    private final String context;

    /**
     * @param executor
     */
    public TestTask(Executor<String, String> executor, Duration callLength, String context) {
        this.executor = executor;
        this.testFunction = new TestFunction(callLength, context);
        this.context = context;
    }

    /** {@inheritDoc} */
    @Override
    public String call() {
        String execution = this.executor.execute(testFunction, context);
        return execution;
    }

}
