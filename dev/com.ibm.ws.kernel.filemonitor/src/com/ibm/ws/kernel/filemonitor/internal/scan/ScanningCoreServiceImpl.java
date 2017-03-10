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

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.ws.kernel.filemonitor.FileNotification;
import com.ibm.ws.kernel.filemonitor.internal.CoreServiceImpl;
import com.ibm.ws.kernel.filemonitor.internal.MonitorHolder;
import com.ibm.wsspi.kernel.filemonitor.FileMonitor;

/**
 * The traditional core service, used when we're below Java 1.7
 */
@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL, configurationPid = "com.ibm.ws.kernel.filemonitor", service = { FileNotification.class },
           property = { "service.vendor=IBM" })
public class ScanningCoreServiceImpl extends CoreServiceImpl {

    @Override
    protected MonitorHolder createMonitorHolder(ServiceReference<FileMonitor> monitorRef) {
        return new ScanningMonitorHolder(this, monitorRef);
    }

    /**
     * We don't do anything with the process, but having it set allows us to only be activated by DS if criteria we set
     * about the Java version are met.
     */
    @Reference(policy = ReferencePolicy.STATIC/* , target = "(&(java.specification.version<=1.7)(! (java.specification.version=1.7)))" */)
    protected void setProcess(LibertyProcess process) {

    }

}
