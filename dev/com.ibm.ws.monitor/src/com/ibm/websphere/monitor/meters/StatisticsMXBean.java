/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.websphere.monitor.meters;

public interface StatisticsMXBean {

    public String getDescription();

    public String getUnit();

    public long getCount();

    public long getMinimumValue();

    public long getMaximumValue();

    public double getTotal();

    public double getMean();

    public double getVariance();

    public double getStandardDeviation();

    public StatisticsReading getReading();

}
