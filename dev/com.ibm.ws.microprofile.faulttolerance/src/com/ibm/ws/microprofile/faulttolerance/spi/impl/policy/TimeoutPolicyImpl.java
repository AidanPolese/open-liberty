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

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;

/**
 *
 */
public class TimeoutPolicyImpl implements TimeoutPolicy {

    private Duration timeout;

    /**
     *
     */
    public TimeoutPolicyImpl() {
        try {
            long longTimeout = (long) Timeout.class.getMethod("value").getDefaultValue();
            ChronoUnit timeoutUnit = (ChronoUnit) Timeout.class.getMethod("unit").getDefaultValue();
            timeout = Duration.of(longTimeout, timeoutUnit);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new FaultToleranceException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Duration getTimeout() {
        return timeout;
    }

    /** {@inheritDoc} */
    @Override
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

}
