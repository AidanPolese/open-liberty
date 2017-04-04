// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.wsspi.pmi.stat;

import java.util.ArrayList;

import com.ibm.ws.pmi.stat.StatsCreator;
import com.ibm.ws.pmi.stat.StatsImpl;

/**
 * This is a public api wrapper class for com.ibm.ws.pmi.stat
 */
public class SPIStatsCreator {
    public static StatsImpl create(String statsType,
                                    String name,
                                    int type,
                                    long time) {
        return StatsCreator.create(statsType, name, type, time);
    }

    public static StatsImpl create(String statsType,
                                    String name,
                                    int type,
                                    int level,
                                    long time) {
        return StatsCreator.create(statsType, name, type, level, time);
    }

    public static StatsImpl create(String statsType,
                                    String name,
                                    int type,
                                    int level,
                                    long time,
                                    ArrayList dataMembers,
                                    ArrayList subCollections) {
        return StatsCreator.create(statsType, name, type, level, time, dataMembers, subCollections);
    }

    public static void addSubStatsToParent(SPIStats parentStats, SPIStats subStats) {
        StatsCreator.addSubStatsToParent(parentStats, subStats);
    }

    public static void addStatisticsToParent(SPIStats parentStats, SPIStatistic statistic) {
        StatsCreator.addStatisticsToParent(parentStats, statistic);
    }
}
