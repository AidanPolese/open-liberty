/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */
package test.server.config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import test.server.BaseTest;

public abstract class Test extends BaseTest {

    protected CountDownLatch latch;
    protected Throwable exception;

    public Test(String name) {
        this(name, 1);
    }

    public Test(String name, int count) {
        super(name);
        this.latch = new CountDownLatch(count);
    }

    public Throwable getException() {
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                return new RuntimeException("Timed out");
            }
        } catch (InterruptedException e) {
            return new RuntimeException("Interrupted");
        }
        return exception;
    }

}
