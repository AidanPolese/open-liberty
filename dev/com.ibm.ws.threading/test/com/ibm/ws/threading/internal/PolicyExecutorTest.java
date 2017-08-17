/*******************************************************************************
 * Copyright (c) 2012, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.threading.internal;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.ibm.ws.threading.PolicyExecutor;
import com.ibm.ws.threading.PolicyExecutorProvider;

/**
 *
 */
public class PolicyExecutorTest {

    PolicyExecutorProvider provider = new PolicyExecutorProvider();

    /**
     * Test max concurrency configuration boundaries.
     */
    @Test
    public void testMaxConcurrencyConfiguration() {
        PolicyExecutor policy = null;
        // Test max concurrency configuration cannot be 0.
        try {
            policy = provider.create("testConcurrency0");
            policy.maxConcurrency(0);
            fail("The zero invalid max concurency configuration should have generated an exception, but it did not.");
        } catch (IllegalArgumentException eae) {
            // Expected Exception.
        }

        // Test max concurrency configuration cannot be lower than the minimum value of = -1.
        try {
            policy = provider.create("testConcurrencyNegative2");
            policy.maxConcurrency(-2);
            fail("The negative two invalid max concurency configuration should have generated an exception, but it did not.");
        } catch (IllegalArgumentException eae) {
            // Expected Exception.
        }

        // Test max concurrency configuration can be -1.
        policy = provider.create("testConcurrencyNegative1");
        policy.maxConcurrency(-1);

        // Test max concurrency configuration cannot be greater than the maximum value of = Integer.MAX_VALUE.
        try {
            policy = provider.create("testConcurrencyOverMax");
            policy.maxConcurrency(Integer.MAX_VALUE + 1);
            fail("The max int plus one max concurrency configuration should have generated an exception, but it did not.");
        } catch (IllegalArgumentException eae) {
            // Expected Exception.
        }
    }

    /**
     * Test max queue configuration boundaries.
     */
    @Test
    public void testMaxQueueConfiguration() {
        PolicyExecutor policy = null;
        // Test max queue configuration cannot be 0.
        try {
            policy = provider.create("testQueue0");
            policy.maxQueueSize(0);
            fail("The zero invalid max queue configuration should have generated an exception, but it did not.");
        } catch (IllegalArgumentException eae) {
            // Expected Exception.
        }

        // Test max queue configuration cannot be lower than the minimum value of = -1.
        try {
            policy = provider.create("testQueueNegative2");
            policy.maxQueueSize(-2);
            fail("The negative two invalid max concurency configuration should have generated an exception, but it did not.");
        } catch (IllegalArgumentException eae) {
            // Expected Exception.
        }

        // Test max queue configuration can be -1.
        policy = provider.create("testQueueNegative1");
        policy.maxQueueSize(-1);

        // Test max queue configuration cannot be greater than the maximum value of = Integer.MAX_VALUE.
        try {
            policy = provider.create("testQueueOverMax");
            policy.maxQueueSize(Integer.MAX_VALUE + 1);
            fail("The max int plus one max queue configuration should have generated an exception, but it did not.");
        } catch (IllegalArgumentException eae) {
            // Expected Exception.
        }
    }

    /**
     * Test boundaries for maxWaitForEnqueue.
     */
    @Test
    public void testMaxWaitForEnqueueConfiguration() {
        try {
            fail("should not allow negative value " + provider.create("testMaxWaitForEnqueueConfiguration-negative").maxWaitForEnqueue(-1));
        } catch (IllegalArgumentException x) {
        } // pass

        provider.create("testMaxWaitForEnqueueConfiguration-zero").maxWaitForEnqueue(0);

        provider.create("testMaxWaitForEnqueueConfiguration-max").maxWaitForEnqueue(Long.MAX_VALUE);

        PolicyExecutor executor = provider.create("testMaxWaitForEnqueueConfiguration-positive").maxWaitForEnqueue(TimeUnit.SECONDS.toMillis(20));
        executor.shutdown();

        try {
            fail("should not allow change after shutdown " + executor.maxWaitForEnqueue(TimeUnit.SECONDS.toMillis(30)));
        } catch (IllegalStateException x) {
        } // pass
    }
}
