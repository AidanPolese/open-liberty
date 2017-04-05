// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 */

package com.ibm.wsspi.pmi.stat;

import com.ibm.websphere.pmi.stat.WSCountStatistic;

/**
 * WebSphere interface to instrument a Count statistic.
 * 
 * @ibm-spi
 */

public interface SPICountStatistic extends SPIStatistic, WSCountStatistic {
    /** Increment the Count statistic by 1 */
    public void increment();

    /** Increment the Count statistic by incVal */
    public void increment(long incVal);

    /** Increment the Count statistic by incVal */
    public void increment(long lastSampleTime, long incVal);

    /** Decrement the Count statistic by 1 */
    public void decrement();

    /** Decrement the Count statistic by decVal */
    public void decrement(long decVal);

    /** Decrement the Count statistic by decVal */
    public void decrement(long lastSampleTime, long incVal);

    /** Set the Count statistic with the following values */
    public void set(long count, long startTime, long lastSampleTime);

    /** Set the count to the following value */
    public void setCount(long value);
}
