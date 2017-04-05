// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
/*
 * @(#)author    David Adcox
 * @(#)version   1.1
 * @(#)date      2005/09/22
 */
package com.ibm.wsspi.pmi.factory;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.ibm.wsspi.pmi.stat.SPIStatistic;

/**
 * This class is used to propagate action events from PMI service to the runtime
 * component.
 * 
 * @ibm-spi
 */
public class StatisticActions {
    private StatisticActionListener legacyListener = null;

    /**
     * This is the default constructor.
     */
    public StatisticActions() {}

    /**
     * This is the default constructor.
     * 
     * @param legacy This is the StatisticActionListener object that will be wrapped by this class.
     */
    public StatisticActions(StatisticActionListener legacy) {
        legacyListener = legacy;
    }

    /**
     * This method is called to indicate that a statistic is created in the Stats instance.
     * The runtime component should use this message to cache the reference to the statistic.
     * This eliminates querying the individual statistic from the StatsInstance object.
     * 
     * @param s statistic created in the StatsInstance
     */
    public void statisticCreated(SPIStatistic s) {
        if (legacyListener != null)
            legacyListener.statisticCreated(s);
    }

    /**
     * This method is called to indicate that a client or monitoring application is
     * requesting the statistic. This message is applicable only to the "updateOnRequest" statistic.
     * 
     * @param dataId data ID of the statistic
     */
    public void updateStatisticOnRequest(int dataId) {
        //System.out.println("Hi This is for UpdataStatistic");
        if (legacyListener != null)
            legacyListener.updateStatisticOnRequest(dataId);
    }

    /**
     * This method is called whenever the PMI framework has either enabled or disabled
     * statistics. The arrays provided as parameters identify which statistics are enabled
     * and which are disabled.
     * 
     * @param enabled Array of enabled statistic data IDs
     * @param disabled Array of disabled statistic data IDs
     */
    public void enableStatusChanged(int[] enabled, int[] disabled) {}

    public Bundle getCurrentBundle() {
        return FrameworkUtil.getBundle(getClass());
    }

}
