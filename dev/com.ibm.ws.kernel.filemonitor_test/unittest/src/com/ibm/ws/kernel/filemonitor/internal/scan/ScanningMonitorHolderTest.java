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
package com.ibm.ws.kernel.filemonitor.internal.scan;

import org.osgi.framework.ServiceReference;

import com.ibm.ws.kernel.filemonitor.internal.CoreService;
import com.ibm.ws.kernel.filemonitor.internal.MonitorHolderTestParent;
import com.ibm.wsspi.kernel.filemonitor.FileMonitor;

public class ScanningMonitorHolderTest extends MonitorHolderTestParent {

    /**
     * @param mockCoreService
     * @param mockServiceReference
     * @return
     */
    @Override
    protected ScanningMonitorHolder instantiateMonitor(CoreService mockCoreService, ServiceReference<FileMonitor> mockServiceReference) {
        return new ScanningMonitorHolder(mockCoreService, mockServiceReference);
    }

}
