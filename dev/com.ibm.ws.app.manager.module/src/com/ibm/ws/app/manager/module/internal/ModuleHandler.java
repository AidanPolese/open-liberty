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
package com.ibm.ws.app.manager.module.internal;

import java.util.concurrent.Future;

import com.ibm.ws.app.manager.module.DeployedAppInfo;
import com.ibm.ws.app.manager.module.DeployedModuleInfo;
import com.ibm.ws.container.service.app.deploy.extended.ExtendedModuleInfo;
import com.ibm.ws.container.service.metadata.MetaDataException;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

public interface ModuleHandler {

    ModuleMetaData createModuleMetaData(ExtendedModuleInfo moduleInfo, DeployedAppInfo deployedApp) throws MetaDataException;

    Future<Boolean> deployModule(DeployedModuleInfo deployedModule, DeployedAppInfo deployedApp);

    boolean undeployModule(DeployedModuleInfo deployedModule);
}
