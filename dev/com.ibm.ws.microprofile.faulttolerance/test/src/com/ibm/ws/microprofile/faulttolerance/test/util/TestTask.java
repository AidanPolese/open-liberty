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
