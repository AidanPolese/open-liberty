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
package com.ibm.ws.threading.internal;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.ibm.ws.threading.PolicyExecutor;
import com.ibm.ws.threading.PolicyExecutorProvider;

/**
 * Unit tests for policy executor.
 */
public class PolicyExecutorTest {

    PolicyExecutorProvider provider = new PolicyExecutorProvider();

    // Verify that core concurrency can be -1 (unlimited) but otherwise not negative or greater than maximum concurrency,
    // except where maximum concurrency is -1 (unlimited).
    @Test
    public void testCoreConcurrencyConfiguration() {
        PolicyExecutor executor = provider.create("testCoreConcurrencyConfiguration");

        try {
            executor.coreConcurrency(-2);
            fail("Should reject negative core concurrency.");
        } catch (IllegalArgumentException x) {
            if (!x.getMessage().contains("-2"))
                throw x;
        }

        executor.maxConcurrency(10);
        try {
            executor.coreConcurrency(12);
            fail("Should reject core concurrency set greater than max concurrency.");
        } catch (IllegalArgumentException x) {
            if (!x.getMessage().contains("12"))
                throw x;
        }

        executor.coreConcurrency(10);
        try {
            executor.maxConcurrency(5);
            fail("Should reject max concurrency set less than core concurrency.");
        } catch (IllegalArgumentException x) {
            if (!x.getMessage().contains("5"))
                throw x;
        }

        executor.maxConcurrency(Integer.MAX_VALUE);
        executor.coreConcurrency(Integer.MAX_VALUE);

        executor.coreConcurrency(-1);
        executor.maxConcurrency(-1);

        executor.coreConcurrency(0);

        executor.shutdownNow();
    }

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
