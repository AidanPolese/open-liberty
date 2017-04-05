// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 * @(#)version   1.2
 * @(#)date      05/05/03
 */

package com.ibm.wsspi.pmi.factory;

import com.ibm.wsspi.pmi.stat.*;

/**
 * This interface is used to propagate action events from PMI service to the runtime component.
 * 
 * @deprecated As of 6.1, replaced with {@link com.ibm.wsspi.pmi.factory.StatisticActions}
 * 
 * @ibm-api
 **/

public interface StatisticActionListener {
    /**
     * This method is called to indicate that a statistic is created in the Stats instance.
     * The runtime component should use this message to cache the reference to the statistic.
     * This eliminates querying the individual statistic from the StatsInstance object.
     * 
     * @param s statistic created in the StatsInstance
     */
    public void statisticCreated(SPIStatistic s);

    /**
     * This method is called to indicate that a client or monitoring application is
     * requesting the statistic. This message is applicable only to the "updateOnRequest" statistic.
     * 
     * @param dataId data ID of the statistic
     */
    public void updateStatisticOnRequest(int dataId);
}
