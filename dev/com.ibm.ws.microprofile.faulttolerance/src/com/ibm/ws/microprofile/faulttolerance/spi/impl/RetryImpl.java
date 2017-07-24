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
package com.ibm.ws.microprofile.faulttolerance.spi.impl;

import java.time.Duration;
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

            if (abortOn.length > 0) {
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
