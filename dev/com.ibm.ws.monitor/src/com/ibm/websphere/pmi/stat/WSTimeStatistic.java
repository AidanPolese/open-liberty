// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 */

package com.ibm.websphere.pmi.stat;

/**
 * WebSphere Time statistic interface.
 * 
 * @ibm-api
 */

public interface WSTimeStatistic extends WSAverageStatistic {
    /** Returns the sum total of time taken to complete all the invocations since the beginning of the measurement. */
    public long getTotalTime();

    /** Returns the minimum time taken to complete one invocation since the beginning of the measurement. */
    public long getMinTime();

    /** Returns the maximum time taken to complete one invocation since the beginning of the measurement. */
    public long getMaxTime();
}
