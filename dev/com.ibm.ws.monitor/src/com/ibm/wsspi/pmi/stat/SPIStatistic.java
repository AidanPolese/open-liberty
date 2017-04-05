// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 */

package com.ibm.wsspi.pmi.stat;

import com.ibm.websphere.pmi.stat.WSStatistic;

/**
 * WebSphere interface to instrument a statistic.
 * 
 * @ibm-spi
 */

public interface SPIStatistic extends WSStatistic {
    /**
     * Resets the statistic to zero. Typically, this method is not called by the application.
     */
    public void reset();

    /**
     * Set last sample time
     */
    public void setLastSampleTime(long lastSampleTime);

    /**
     * Set start time
     */
    public void setStartTime(long startTime);

    /** Returns true if monitoring for this statitic is enabled */
    public boolean isEnabled();
}
