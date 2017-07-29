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
package com.ibm.ws.microprofile.faulttolerance.cdi;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceFunction;

/**
 * @param <T>
 *
 */
public class FallbackFunction<R> implements FaultToleranceFunction<ExecutionContext, R> {

    private final FallbackHandler<R> fallbackHandler;

    public FallbackFunction(FallbackHandler<R> fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
    }

    /** {@inheritDoc} */
    @Override
    public R execute(ExecutionContext executionContext) throws Exception {
        return this.fallbackHandler.handle(executionContext);
    }

}
