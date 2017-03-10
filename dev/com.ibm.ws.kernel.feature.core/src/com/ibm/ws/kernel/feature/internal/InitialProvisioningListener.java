/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.feature.internal;

import java.util.concurrent.CountDownLatch;

import com.ibm.ws.kernel.feature.internal.ShutdownHookManager.ShutdownHookListener;

/**
 *
 */
public class InitialProvisioningListener implements ShutdownHookListener {

    /** Latch that is notified when initial feature provisioning has completed. */
    protected final CountDownLatch initialProvisioningLatch = new CountDownLatch(1);

    /**
     * Countdown the initial provisioning latch if the JVM is shutdown
     */
    @Override
    public void shutdownHookInvoked() {
        initialProvisioningLatch.countDown();
    }

    /**
     * Wait on the initial provisioning latch
     */
    public void await() throws InterruptedException {
        initialProvisioningLatch.await();
    }

    /**
     * Countdown the initial provisioning latch
     */
    public void countDown() {
        initialProvisioningLatch.countDown();
    }
}
