/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.monitors.helper;

import com.ibm.websphere.monitor.meters.Meter;
import com.ibm.websphere.monitor.meters.ThreadPoolMXBean;

/**
 * 
 */
public class ThreadPoolStats extends Meter implements ThreadPoolMXBean {

    private final ThreadPoolStatsHelper _tpHelper;

    public ThreadPoolStats(String poolName, Object tpExecImpl) {
        _tpHelper = new ThreadPoolStatsHelper(poolName, tpExecImpl);
    }

    /**
     * @return the poolName
     */
    public String getPoolName() {
        return _tpHelper.getPoolName();
    }

    /**
     * @return the poolSize
     */
    public int getPoolSize() {
        return _tpHelper.getPoolSize();
    }

    /**
     * @return the activeThreads
     */
    public int getActiveThreads() {
        return _tpHelper.getActiveThreads();
    }
}
