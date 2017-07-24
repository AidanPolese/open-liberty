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

}
