// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 */

package com.ibm.wsspi.pmi.stat;

import com.ibm.websphere.pmi.stat.WSRangeStatistic;

/**
 * WebSphere interface to instrument a Range statistic.
 * 
 * @ibm-spi
 */

public interface SPIRangeStatistic extends SPIStatistic, WSRangeStatistic {
    /** Updates high water mark and low water mark based on the input value */
    public void setWaterMark(long currentValue);

    /** Updates high water mark and low water mark based on the input value */
    public void setWaterMark(long lastSampleTime, long currentValue);

    /** Set the Range statistic with the following values */
    public void set(long lowWaterMark, long highWaterMark, long current, double integral,
                    long startTime, long lastSampleTime);

    /** Set the current value. The water marks will be updated automatically. */
    public void set(long currentValue);

    /** Set the current value. The water marks will be updated automatically. */
    public void set(long lastSampleTime, long val);

    /** Increment the current value by 1. The water marks will be updated automatically. */
    public void increment();

    /** Increment the current value by incVal. The water marks will be updated automatically. */
    public void increment(long incVal);

    /** Increment the current value by incVal. The water marks will be updated automatically. */
    public void increment(long lastSampleTime, long incVal);

    /*
     * public void incrementWithoutSync(long lastSampleTime, long val);
     * public void decrementWithoutSync(long lastSampleTime, long val);
     */

    /** Decrement the current value by 1. The water marks will be updated automatically. */
    public void decrement();

    /** Decrement the current value by incVal. The water marks will be updated automatically. */
    public void decrement(long decVal);

    /** Decrement the current value by incVal. The water marks will be updated automatically. */
    public void decrement(long lastSampleTime, long incVal);

    /** Set the current value. The water marks are not updated. */
    public void setLastValue(long val);

    /** Updates the intergal value. Typically, this method shouldn't be called from the application. */
    public long updateIntegral();

    /** Updates the intergal value. Typically, this method shouldn't be called from the application. */
    public long updateIntegral(long lastSampleTime);
}