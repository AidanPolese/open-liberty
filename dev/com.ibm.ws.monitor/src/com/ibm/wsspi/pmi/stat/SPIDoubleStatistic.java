// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 */

package com.ibm.wsspi.pmi.stat;

import com.ibm.websphere.pmi.stat.WSDoubleStatistic;

/**
 * WebSphere interface to instrument a Double statistic.
 * 
 * @ibm-spi
 */

public interface SPIDoubleStatistic extends SPIStatistic, WSDoubleStatistic {
    /** Set the Double statistic with following values */
    public void set(double count, long startTime, long lastSampleTime);

    /** Set the double value */
    public void setDouble(double value);

    /** Increment the statistic by 1 */
    public void increment();

    /** Increment the statistic by the input value */
    public void increment(double val);

    /** Decrement the statistic by 1 */
    public void decrement();

    /** Decrement the statistic by the input value */
    public void decrement(double val);
}
