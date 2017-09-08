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
package com.ibm.ws.microprofile.faulttolerance.impl;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.ibm.ws.microprofile.faulttolerance.spi.RetryPolicy;

/**
 * @param <R>
 *
 */
public class RetryImpl extends net.jodah.failsafe.RetryPolicy {

    public RetryImpl(RetryPolicy policy) {
        super();
        if (policy == null) {
            withMaxRetries(0);
        } else {
            Class<? extends Throwable>[] abortOn = policy.getAbortOn();
            Duration delay = policy.getDelay();
            Duration jitter = policy.getJitter();
            Duration maxDuration = policy.getMaxDuration();
            int maxRetries = policy.getMaxRetries();
            Class<? extends Throwable>[] retryOn = policy.getRetryOn();

            System.out.println("RetryImpl created");
            if (abortOn.length > 0) {
                System.out.println("RetryImpl created with abortOn = " + Arrays.toString(abortOn));
                abortOn(abortOn);
            }
            if (delay.toMillis() > 0) {
                withDelay(delay.toMillis(), TimeUnit.MILLISECONDS);
            }
            withJitter(jitter.toMillis(), TimeUnit.MILLISECONDS);
            withMaxDuration(maxDuration.toMillis(), TimeUnit.MILLISECONDS);
            withMaxRetries(maxRetries);
            if (retryOn.length > 0) {
                retryOn(retryOn);
            }
        }
    }
}
