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
 * WebSphere Average statistic to represent a simple average.
 * 
 * @ibm-api
 */

public interface WSAverageStatistic extends WSStatistic {
    /** Returns the number of samples involved in this statistic. */
    public long getCount();

    /** Returns the sum of the values of all the samples. */
    public long getTotal();

    /** Returns the mean or average (getTotal() divided by getCount()). */
    public double getMean();

    /** Returns the minimum value of all the samples. */
    public long getMin();

    /** Returns the maximum value of all the samples. */
    public long getMax();

    /** Returns the sum-of-squares of the values of all the samples. */
    public double getSumOfSquares();
}
