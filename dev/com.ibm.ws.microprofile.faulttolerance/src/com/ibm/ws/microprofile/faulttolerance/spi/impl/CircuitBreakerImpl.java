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

import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;

import net.jodah.failsafe.CircuitBreaker;

/**
 *
 */
public class CircuitBreakerImpl extends CircuitBreaker {

    public CircuitBreakerImpl(CircuitBreakerPolicy policy) {
        Duration delay = policy.getDelay();
        Class<? extends Throwable>[] failOn = policy.getFailOn();
        double failureRatio = policy.getFailureRatio();
        int requestVolumeThreshold = policy.getRequestVolumeThreshold();
        int successThreshold = policy.getSuccessThreshold();

        failOn(failOn);
        withDelay(delay.toMillis(), TimeUnit.MILLISECONDS);
        int failures = (int) (failureRatio * requestVolumeThreshold);
        //TODO should failures be rounded up or down?
        int executions = requestVolumeThreshold;

        withFailureThreshold(failures, executions);
        withSuccessThreshold(successThreshold);
    }

}
