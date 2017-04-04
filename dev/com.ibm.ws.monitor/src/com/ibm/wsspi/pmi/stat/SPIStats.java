// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/*
 * @(#)author    Srini Rangaswamy
 */

package com.ibm.wsspi.pmi.stat;

import java.util.*;

import com.ibm.websphere.pmi.stat.WSStats;

/**
 * WebSphere interface to instrument Stats. Typically, the methods in this interface are not called from the application.
 * 
 * @ibm-spi
 */

public interface SPIStats extends WSStats {
    /** Set the Stats name */
    public void setName(String name);

    /** Set the stats type */
    public void setStatsType(String modName);

    /**
     * Set the stats monitoring level
     * 
     * @deprecated No replacement
     * */
    public void setLevel(int level);

    /** Set statistics for this Stats */
    public void setStatistics(ArrayList dataMembers);

    /** Set child stats */
    public void setSubStats(ArrayList subCollections);
}
