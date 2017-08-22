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
package com.ibm.ws.microprofile.faulttolerance.test.util;

import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.ws.microprofile.faulttolerance.spi.FallbackHandlerFactory;

/**
 *
 */
public class TestFallbackFactory implements FallbackHandlerFactory {

    /** {@inheritDoc} */
    @Override
    public <R extends FallbackHandler<?>> R newHandler(Class<R> handlerClass) {
        if (handlerClass == TestFallback.class) {
            return (R) new TestFallback();
        } else {
            throw new FaultToleranceException();
        }
    }

}
