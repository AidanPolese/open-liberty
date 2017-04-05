// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 */

package com.ibm.wsspi.pmi.stat;

import com.ibm.websphere.pmi.stat.WSAverageStatistic;

/**
 * WebSphere interface to instrument an Average statistic.
 * 
 * @ibm-spi
 */

public interface SPIAverageStatistic extends SPIStatistic, WSAverageStatistic {
    /** Add a measurement value to the Average statistic. */
    public void add(long val);

    /** Add a measurement value to the Average statistic. */
    public void add(long lastSampleTime, long val);

    /** Set the Average statistic with the following values. */
    public void set(long count, long min, long max, long total, double sumOfSquares,
                    long startTime, long lastSampleTime);
}
