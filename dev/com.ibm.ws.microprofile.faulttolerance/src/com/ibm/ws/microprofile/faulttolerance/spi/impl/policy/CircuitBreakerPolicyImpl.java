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
package com.ibm.ws.microprofile.faulttolerance.spi.impl.policy;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.ws.microprofile.faulttolerance.spi.CircuitBreakerPolicy;

/**
 *
 */
public class CircuitBreakerPolicyImpl implements CircuitBreakerPolicy {

    private Class<? extends Throwable>[] failOn;
    private Duration delay;
    private int requestVolumeThreshold;
    private double failureRatio;
    private int successThreshold;

    /**
     *
     */
    @SuppressWarnings("unchecked")
    public CircuitBreakerPolicyImpl() {
        try {
            failOn = (Class<? extends Throwable>[]) CircuitBreaker.class.getMethod("failOn").getDefaultValue();
            long longDelay = (long) CircuitBreaker.class.getMethod("delay").getDefaultValue();
            ChronoUnit delayUnit = (ChronoUnit) CircuitBreaker.class.getMethod("delayUnit").getDefaultValue();
            delay = Duration.of(longDelay, delayUnit);
            requestVolumeThreshold = (int) CircuitBreaker.class.getMethod("requestVolumeThreshold").getDefaultValue();
            failureRatio = (double) CircuitBreaker.class.getMethod("failureRatio").getDefaultValue();
            successThreshold = (int) CircuitBreaker.class.getMethod("successThreshold").getDefaultValue();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new FaultToleranceException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Throwable>[] getFailOn() {
        return failOn;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void setFailOn(Class<? extends Throwable>... failOn) {
        this.failOn = failOn;
    }

    /** {@inheritDoc} */
    @Override
    public Duration getDelay() {
        return delay;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    /** {@inheritDoc} */
    @Override
    public int getRequestVolumeThreshold() {
        return requestVolumeThreshold;
    }

    /** {@inheritDoc} */
    @Override
    public void setRequestVolumeThreshold(int threshold) {
        this.requestVolumeThreshold = threshold;
    }

    /** {@inheritDoc} */
    @Override
    public double getFailureRatio() {
        return failureRatio;
    }

    /** {@inheritDoc} */
    @Override
    public void setFailureRatio(double ratio) {
        this.failureRatio = ratio;
    }

    /** {@inheritDoc} */
    @Override
    public int getSuccessThreshold() {
        return successThreshold;
    }

    /** {@inheritDoc} */
    @Override
    public void setSuccessThreshold(int threshold) {
        this.successThreshold = threshold;
    }

}
