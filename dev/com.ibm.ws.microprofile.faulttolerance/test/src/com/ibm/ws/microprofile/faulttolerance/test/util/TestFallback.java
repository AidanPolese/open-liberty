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

import com.ibm.ws.microprofile.faulttolerance.spi.FaultToleranceFunction;

/**
 *
 */
public class TestFallback implements FaultToleranceFunction<String, String> {

    /** {@inheritDoc} */
    @Override
    public String execute(String context) throws Exception {
        return "Fallback: " + context;
    }

}
