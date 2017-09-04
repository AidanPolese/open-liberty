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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 *
 */
public class AsyncTestFunction implements Callable<Future<String>> {

    private final TestFunction function;

    public AsyncTestFunction(Duration callLength, int exception, String context) {
        this.function = new TestFunction(callLength, exception, context);
    }

    public AsyncTestFunction(int exception, String context) {
        this.function = new TestFunction(exception, context);
    }

    public AsyncTestFunction(Duration callLength, String context) {
        this.function = new TestFunction(callLength, context);
    }

    /** {@inheritDoc} */
    @Override
    public Future<String> call() throws Exception {
        return CompletableFuture.completedFuture(function.call());
    }

    /**
     * @return
     */
    public int getExecutions() {
        return function.getExecutions();
    }

}
