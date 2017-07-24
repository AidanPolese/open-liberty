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

import com.ibm.ws.microprofile.faulttolerance.spi.FallbackPolicy;
import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceFunction;

public class FallbackPolicyImpl<T, R> implements FallbackPolicy<T, R> {

    private FaultToleranceFunction<T, R> fallback;

    /** {@inheritDoc} */
    @Override
    public FaultToleranceFunction<T, R> getFallback() {
        return fallback;
    }

    /** {@inheritDoc} */
    @Override
    public void setFallback(FaultToleranceFunction<T, R> fallback) {
        this.fallback = fallback;
    }

}
