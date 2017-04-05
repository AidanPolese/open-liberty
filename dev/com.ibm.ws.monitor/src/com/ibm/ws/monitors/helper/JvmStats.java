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

import com.ibm.websphere.monitor.meters.JvmMXBean;
import com.ibm.websphere.monitor.meters.Meter;

/**
 *
 */

public class JvmStats extends Meter implements JvmMXBean {

    private static JvmMonitorHelper _jHelper;

    /**
     * @param jHelper
     * 
     */
    public JvmStats(JvmMonitorHelper jHelper) {
        if (_jHelper == null) {
            //FFDC Here
        }
        _jHelper = jHelper;
    }

    /** {@inheritDoc} */
    @Override
    public long getFreeMemory() {
        return (_jHelper.getCommitedHeapMemoryUsage() - _jHelper.getUsedHeapMemoryUsage());
    }

    /** {@inheritDoc} */
    @Override
    public long getGcCount() {
        // TODO Auto-generated method stub
        return _jHelper.getGCCollectionCount();
    }

    /** {@inheritDoc} */
    @Override
    public long getGcTime() {
        // TODO Auto-generated method stub
        return _jHelper.getGCCollectionTime();
    }

    /** {@inheritDoc} */
    @Override
    public long getHeap() {
        return _jHelper.getCommitedHeapMemoryUsage();
    }

    /** {@inheritDoc} */
    @Override
    public double getProcessCPU() {
        return _jHelper.getCPU();
    }

    /** {@inheritDoc} */
    @Override
    public long getUpTime() {
        // TODO Auto-generated method stub
        return _jHelper.getUptime();
    }

    /** {@inheritDoc} */
    @Override
    public long getUsedMemory() {
        // TODO Auto-generated method stub
        return _jHelper.getUsedHeapMemoryUsage();
    }

}
