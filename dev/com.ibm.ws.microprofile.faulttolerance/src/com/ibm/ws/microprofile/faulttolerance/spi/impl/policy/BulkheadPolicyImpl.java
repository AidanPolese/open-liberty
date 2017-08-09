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
package com.ibm.ws.microprofile.faulttolerance.spi.impl.policy;

import java.time.Duration;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;

/**
 *
 */
public class BulkheadPolicyImpl implements BulkheadPolicy {

    private int maxThreads;
    private Duration timeout;

    /**
     *
     */
    public BulkheadPolicyImpl() {
        try {
            maxThreads = (int) Bulkhead.class.getMethod("maxThreads").getDefaultValue();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new FaultToleranceException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxThreads() {
        return maxThreads;
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /** {@inheritDoc} */
    @Override
    public Duration getTimeout() {
        return this.timeout;
    }

    /** {@inheritDoc} */
    @Override
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

}
