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

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;

/**
 *
 */
public class BulkheadPolicyImpl implements BulkheadPolicy {

    private int maxThreads;
    private int maxQueue;
    private Duration timeout;

    /**
     *
     */
    public BulkheadPolicyImpl() {
        try {
            maxThreads = (int) Bulkhead.class.getMethod("value").getDefaultValue();
            maxQueue = (int) Bulkhead.class.getMethod("waitingTaskQueue").getDefaultValue();
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
    public int getMaxQueue() {
        return maxQueue;
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxQueue(int maxQueue) {
        this.maxQueue = maxQueue;
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
