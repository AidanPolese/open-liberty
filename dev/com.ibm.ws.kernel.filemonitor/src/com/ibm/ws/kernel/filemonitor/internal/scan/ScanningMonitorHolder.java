/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.filemonitor.internal.scan;

import java.io.File;

import org.osgi.framework.ServiceReference;

import com.ibm.ws.kernel.filemonitor.internal.CoreService;
import com.ibm.ws.kernel.filemonitor.internal.MonitorHolder;
import com.ibm.ws.kernel.filemonitor.internal.UpdateMonitor;
import com.ibm.ws.kernel.filemonitor.internal.UpdateMonitor.MonitorType;
import com.ibm.wsspi.kernel.filemonitor.FileMonitor;

/**
 *
 */
public class ScanningMonitorHolder extends MonitorHolder {
    /**
     * @param coreService
     * @param monitorRef
     */
    public ScanningMonitorHolder(CoreService coreService, ServiceReference<FileMonitor> monitorRef) {
        super(coreService, monitorRef);
    }

    @Override
    protected UpdateMonitor createUpdateMonitor(File file, MonitorType type, String monitorFilter) {
        // NOTE: File caching is disabled. if/when we want it back, we can start
        // checking against a minimum interval before setting the value.
        UpdateMonitor um = UpdateMonitor.getMonitor(file, type, monitorFilter);
        return um;
    }

}
