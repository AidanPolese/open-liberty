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
 * WebSphere Double statistic interface
 * 
 * @ibm-api
 */

public interface WSDoubleStatistic extends WSStatistic {
    /** Returns the double value since the measurement started. */
    public double getDouble();
}
