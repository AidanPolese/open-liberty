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
package com.ibm.ws.app.manager.ejb.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.app.manager.module.DeployedAppInfo;
import com.ibm.ws.app.manager.module.DeployedAppInfoFactory;
import com.ibm.ws.app.manager.module.internal.DeployedAppInfoFactoryBase;
import com.ibm.ws.app.manager.module.internal.ModuleHandler;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.application.handler.ApplicationInformation;

@Component(service = DeployedAppInfoFactory.class,
           property = { "service.vendor=IBM", "type:String=ejb" })
public class EJBDeployedAppInfoFactoryImpl extends DeployedAppInfoFactoryBase {
    protected ModuleHandler ejbModuleHandler;

    @Reference(target = "(type=ejb)")
    protected void setEjbModuleHandler(ModuleHandler handler) {
        ejbModuleHandler = handler;
    }

    protected void unsetEjbModuleHandler(ModuleHandler handler) {
        ejbModuleHandler = null;
    }

    @Override
    public DeployedAppInfo createDeployedAppInfo(ApplicationInformation<DeployedAppInfo> applicationInformation) throws UnableToAdaptException {
        EJBDeployedAppInfo deployedApp = new EJBDeployedAppInfo(applicationInformation, this);
        applicationInformation.setHandlerInfo(deployedApp);
        return deployedApp;
    }
}