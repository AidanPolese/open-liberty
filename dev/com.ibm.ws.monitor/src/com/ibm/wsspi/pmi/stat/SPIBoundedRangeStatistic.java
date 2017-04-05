// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 */

package com.ibm.wsspi.pmi.stat;

import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;

/**
 * WebSphere interface to instrument BoundedRange statistic.
 * 
 * @ibm-spi
 */

public interface SPIBoundedRangeStatistic extends SPIBoundaryStatistic, SPIRangeStatistic,
                                                  WSBoundedRangeStatistic {
    /** Set the Bounded Range statistic with the following values. */
    public void set(long lowerBound, long upperBound,
                    long lowWaterMark, long highWaterMark,
                    long current, double integral,
                    long startTime, long lastSampleTime);
}
