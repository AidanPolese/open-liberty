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
package com.ibm.ws.container.service.state.internal;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.container.service.state.ApplicationStateListener;
import com.ibm.ws.container.service.state.ModuleStateListener;
import com.ibm.ws.container.service.state.StateChangeException;
import com.ibm.ws.container.service.state.StateChangeService;

public class StateChangeServiceImpl implements StateChangeService {

    private final ApplicationStateManager applicationStateManager = new ApplicationStateManager("applicationStateListeners");
    private final ModuleStateManager moduleStateManager = new ModuleStateManager("moduleStateListeners");

    protected void activate(ComponentContext cc) {
        applicationStateManager.activate(cc);
        moduleStateManager.activate(cc);
    }

    protected void deactivate(ComponentContext cc) {
        applicationStateManager.deactivate(cc);
        moduleStateManager.deactivate(cc);
    }

    // declarative services
    public void addApplicationStateListener(ServiceReference<ApplicationStateListener> ref) {
        applicationStateManager.addListener(ref);
    }

    // declarative services
    public void removeApplicationStateListener(ServiceReference<ApplicationStateListener> ref) {
        applicationStateManager.removeListener(ref);
    }

    @Override
    public void fireApplicationStarting(ApplicationInfo info) throws StateChangeException {
        applicationStateManager.fireStarting(info);
    }

    @Override
    public void fireApplicationStarted(ApplicationInfo info) throws StateChangeException {
        applicationStateManager.fireStarted(info);
    }

    @Override
    public void fireApplicationStopping(ApplicationInfo info) {
        applicationStateManager.fireStopping(info);
    }

    @Override
    public void fireApplicationStopped(ApplicationInfo info) {
        applicationStateManager.fireStopped(info);
    }

    // declarative services
    public void addModuleStateListener(ServiceReference<ModuleStateListener> ref) {
        moduleStateManager.addListener(ref);
    }

    // declarative services
    public void removeModuleStateListener(ServiceReference<ModuleStateListener> ref) {
        moduleStateManager.removeListener(ref);
    }

    @Override
    public void fireModuleStarting(ModuleInfo info) throws StateChangeException {
        moduleStateManager.fireStarting(info);
    }

    @Override
    public void fireModuleStarted(ModuleInfo info) throws StateChangeException {
        moduleStateManager.fireStarted(info);
    }

    @Override
    public void fireModuleStopping(ModuleInfo info) {
        moduleStateManager.fireStopping(info);
    }

    @Override
    public void fireModuleStopped(ModuleInfo info) {
        moduleStateManager.fireStopped(info);
    }
}
