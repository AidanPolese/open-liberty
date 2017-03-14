/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.timer;

/**
 * Approximate timer class that updates the equivalent of
 * System.currentTimeMillis() but at set intervals. This is
 * useful for callers that do not need to be exact.
 */
public interface ApproximateTime {
    /**
     * Get the time which is set according to the time interval.
     * 
     * @return time
     */
    long getApproxTime();
}
