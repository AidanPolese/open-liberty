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
package com.ibm.ws.app.manager.rar.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.app.manager.module.DeployedAppInfo;
import com.ibm.ws.app.manager.module.DeployedAppInfoFactory;
import com.ibm.ws.app.manager.module.internal.DeployedAppInfoFactoryBase;
import com.ibm.ws.app.manager.module.internal.ModuleHandler;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.application.handler.ApplicationInformation;

@Component(service = DeployedAppInfoFactory.class,
           property = { "service.vendor=IBM", "type:String=rar" })
public class RARDeployedAppInfoFactoryImpl extends DeployedAppInfoFactoryBase {

    protected ModuleHandler rarModuleHandler;

    @Reference(target = "(type=connector)")
    protected void setRarModuleHandler(ModuleHandler handler) {
        rarModuleHandler = handler;
    }

    protected void unsetRarModuleHandler(ModuleHandler handler) {
        rarModuleHandler = null;
    }

    @Override
    public RARDeployedAppInfo createDeployedAppInfo(ApplicationInformation<DeployedAppInfo> applicationInformation) throws UnableToAdaptException {
    	RARDeployedAppInfo deployedApp = new RARDeployedAppInfo(applicationInformation, this);
        applicationInformation.setHandlerInfo(deployedApp);
        return deployedApp;
    }
}
