package com.ibm.ws.ras.instrument.internal.main;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

import java.lang.instrument.Instrumentation;

/**
 * A java agent implementation that will cause aggressive instrumentation of
 * code with tracing in a unit test environment. This is required to provide
 * trace outside of the OSGi framework used by the component tests and at
 * runtime.
 */
public class UnitTestLibertyRuntimeTransformer extends LibertyRuntimeTransformer {

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        LibertyRuntimeTransformer.setInstrumentation(inst);
        LibertyRuntimeTransformer.setInjectAtTransform(true);

        // Skip debug info for class files destroyed by emma when we're requested
        if (agentArgs != null && !agentArgs.trim().isEmpty()) {
            String[] keyValue = agentArgs.split("=");
            if (keyValue.length == 2) {
                if (keyValue[0].equals("skipDebugData") && Boolean.parseBoolean(keyValue[1])) {
                    LibertyRuntimeTransformer.setSkipDebugData(true);
                }
            }
        }
    }

}
