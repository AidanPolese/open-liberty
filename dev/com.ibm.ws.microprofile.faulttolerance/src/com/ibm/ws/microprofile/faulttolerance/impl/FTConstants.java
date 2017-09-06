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
package com.ibm.ws.microprofile.faulttolerance.impl;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;

/**
 *
 */
public class FTConstants {

    public static final String SCHEDULED_EXECUTOR_SERVICE_JNDI = "java:comp/DefaultManagedScheduledExecutorService";

    public static final String JSE_FLAG = "com.ibm.ws.microprofile.faulttolerance.jse";

    public static final long MIN_TIMEOUT_NANO = 1000000; //1ms

    @Trivial
    public static void debugRelativeTime(TraceComponent tc, String id, String message, long relativePointA) {
        debugRelativeTime(tc, id, message, relativePointA, System.nanoTime());
    }

    @Trivial
    public static void debugRelativeTime(TraceComponent tc, String id, String message, long relativePointA, long relativePointB) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "{0}: {1}: {2}", id, message, relativeSeconds(relativePointA, relativePointB));
        }
    }

    @Trivial
    public static void debugTime(TraceComponent tc, String id, String message, long time) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "{0}: {1}: {2}", id, message, toSeconds(time));
        }
    }

    @Trivial
    public static void debugTime(TraceComponent tc, String message, long time) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "{0}: {1}", message, toSeconds(time));
        }
    }

    //in seconds, how long between two a relative points (nanoTime)
    @Trivial
    public static double relativeSeconds(long relativePointA, long relativePointB) {
        long diff = relativePointB - relativePointA;
        double seconds = toSeconds(diff);
        return seconds;
    }

    //convert from nanos (long) to seconds (double)
    @Trivial
    public static double toSeconds(long nanos) {
        return ((double) nanos / (double) 1000000000);
    }

}
