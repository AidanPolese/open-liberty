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
package com.ibm.ws.microprofile.faulttolerance.impl.policy;

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
