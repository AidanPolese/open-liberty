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
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class TestFunction implements Callable<String> {

    private final AtomicInteger executions = new AtomicInteger(0);
    private long callLength = 100;
    private int exception = 0;
    private final String context;

    public TestFunction(Duration callLength, int exception, String context) {
        this.callLength = callLength.toMillis();
        this.exception = exception;
        this.context = context;
    }

    public TestFunction(int exception, String context) {
        this(Duration.ofMillis(100), exception, context);
    }

    public TestFunction(Duration callLength, String context) {
        this(callLength, 0, context);
    }

    /** {@inheritDoc} */
    @Override
    public String call() throws Exception {
        int execution = executions.incrementAndGet();
        if (exception == -1 || execution <= exception) {
            System.out.println(System.currentTimeMillis() + " Test " + context + ": " + execution + "/" + exception + " - exception");
            throw new TestException();
        } else {
            System.out.println(System.currentTimeMillis() + " Test " + context + ": " + execution + "/" + exception + " - execute");
        }
        Thread.sleep(callLength);
        System.out.println(System.currentTimeMillis() + " Test " + context + ": " + execution + "/" + exception + " - complete");
        return context;
    }

    public int getExecutions() {
        return executions.get();
    }

}
