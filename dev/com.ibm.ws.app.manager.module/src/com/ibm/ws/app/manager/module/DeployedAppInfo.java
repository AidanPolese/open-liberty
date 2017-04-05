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
package com.ibm.ws.app.manager.module;

import com.ibm.ws.app.manager.module.internal.ModuleHandler;
import com.ibm.ws.container.service.app.deploy.extended.ExtendedModuleInfo;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

public interface DeployedAppInfo {
    void moduleMetaDataCreated(ExtendedModuleInfo moduleInfo, ModuleHandler moduleHandler, ModuleMetaData mmd);

    DeployedModuleInfo getDeployedModule(ExtendedModuleInfo moduleInfo);

    boolean uninstallApp();
}
