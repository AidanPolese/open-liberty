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
 * WebSphere Range statistic interface.
 * 
 * @ibm-api
 */

public interface WSRangeStatistic extends WSStatistic {
    /** Returns the highest value this attribute held since the beginning of the measurement. */
    public long getHighWaterMark();

    /** Returns the lowest value this attribute held since the beginning of the measurement. */
    public long getLowWaterMark();

    /** Returns the current value of this attribute. */
    public long getCurrent();

    /** Return the integral value of this attribute. */
    public double getIntegral();

    /** Returns the time-weighted mean value of this attribute. */
    public double getMean();
}
