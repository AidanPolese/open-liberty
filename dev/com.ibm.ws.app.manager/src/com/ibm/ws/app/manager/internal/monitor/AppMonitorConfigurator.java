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
package com.ibm.ws.app.manager.internal.monitor;

import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.app.manager.internal.AppManagerConstants;

/**
 *
 */
@Component(service = AppMonitorConfigurator.class, immediate = true,
           configurationPid = AppManagerConstants.MONITOR_PID,
           configurationPolicy = ConfigurationPolicy.REQUIRE,
           property = "service.vendor=IBM")
public class AppMonitorConfigurator {

    private ApplicationMonitor appMonitor;
    private DropinMonitor dropinMonitor;
    private volatile ApplicationMonitorConfig appMonitorConfig;

    public ApplicationMonitor getMonitor() {
        return appMonitor;
    }

    @Activate
    protected void activate(ComponentContext ctx, Map<String, Object> config) {
        modified(ctx, config);
    }

    @Modified
    protected void modified(ComponentContext ctx, Map<String, Object> config) {
        ApplicationMonitorConfig prevConfig = appMonitorConfig;
        ApplicationMonitorConfig newConfig = new ApplicationMonitorConfig(prevConfig, config);

        appMonitorConfig = newConfig;
        appMonitor.refresh(appMonitorConfig);
        dropinMonitor.refresh(appMonitorConfig);
    }

    @Deactivate
    protected void deactivate(ComponentContext ctx, int reason) {}

    @Reference
    protected void setApplicationMonitor(ApplicationMonitor appMonitor) {
        this.appMonitor = appMonitor;
    }

    protected void unsetApplicationMonitor(ApplicationMonitor appMonitor) {}

    @Reference
    protected void setDropinMonitor(DropinMonitor dropinMonitor) {
        this.dropinMonitor = dropinMonitor;
    }

    protected void unsetDropinMonitor(DropinMonitor dropinMonitor) {}
}
