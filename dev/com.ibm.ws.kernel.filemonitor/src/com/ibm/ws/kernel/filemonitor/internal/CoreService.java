/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.filemonitor.internal;

import java.util.concurrent.ScheduledExecutorService;

import org.osgi.framework.ServiceReference;

import com.ibm.wsspi.kernel.filemonitor.FileMonitor;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;

/**
 *
 */
public interface CoreService {

    /**
     * @return the active/bound instance of the ScheduledExecutorService; will not return null
     * @throws IllegalStateException if service can not be found
     */
    ScheduledExecutorService getScheduler();

    /**
     * @return the active/bound instance of the WsLocationAdmin service; will not return null
     * @throws IllegalStateException if service can not be found
     */
    WsLocationAdmin getLocationService();

    /**
     * @param monitorRef ServiceReference to a bound FileMonitor
     * @return the active/bound instance of the FileMonitor associated with the provided ServiceReference; will not return null
     * @throws IllegalStateException if service can not be found
     */
    FileMonitor getReferencedMonitor(ServiceReference<FileMonitor> monitorRef);

    /**
     * @return true if detailed scan trace is enabled (trace for scan start/stop, etc. regardless of
     *         whether or not changes are discovered: very noisy!)
     */
    boolean isDetailedScanTraceEnabled();
}
