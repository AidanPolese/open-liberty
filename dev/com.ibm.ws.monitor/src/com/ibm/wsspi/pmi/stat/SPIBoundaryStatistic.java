// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 */

package com.ibm.wsspi.pmi.stat;

import com.ibm.websphere.pmi.stat.WSBoundaryStatistic;

/**
 * WebSphere interface to instrument a Boundary statistic.
 * 
 * @ibm-spi
 */
public interface SPIBoundaryStatistic extends SPIStatistic, WSBoundaryStatistic {
    /** Set the Boundary statistic with the following values */
    public void set(long lowerBound, long upperBound, long startTime, long lastSampleTime);

    /** Sets the low bound */
    public void setLowerBound(long lowerBound);

    /** Sets the upper bound */
    public void setUpperBound(long upperBound);
}
