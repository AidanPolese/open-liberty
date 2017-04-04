package com.ibm.ws.objectManager.utils;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 *
 * Change activity:
 *
 *  Reason         Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 * 607710          20/08/09 gareth   Add isAnyTracingEnabled() check around trace
 * ============================================================================
 */

/**
 * This class holds a reference to the trace object we use to check
 * if any tracing is enabled at all. Hopefully through the use of
 * static methods and a final reference to the Trace object used
 * it will allow the JIT to inline the checks (and remove any corresponding
 * tracing checks if tracing is disabled). Also the use of a single trace
 * object for all checks should stop excessive paging of Trace objects
 * in and out of memory purely for the purposes of checking to see if
 * trace is enabled.
 */
public final class Tracing
{
    // Statically initialise the final instance of trace that we will
    // use to check whether any tracing is enabled.
    public static final Trace trace = createTraceObject();

    private static Trace createTraceObject()
    {
        // Create an NLS object
        NLS anyTraceNLS = (NLS) Utils.getImpl("com.ibm.ws.objectManager.utils.NLSImpl",
                                              new Class[] { String.class },
                                              new Object[] { UtilsConstants.MSG_BUNDLE });

        // Create a TraceFactory
        TraceFactory factory = (TraceFactory) Utils.getImpl("com.ibm.ws.objectManager.utils.TraceFactoryImpl",
                                                            new Class[] { NLS.class },
                                                            new Object[] { anyTraceNLS });

        // Create a Trace object
        return factory.getTrace(TraceFactory.class, "");
    }

    public static boolean isAnyTracingEnabled()
    {
        return trace.isAnyTracingEnabled();
    }
}
