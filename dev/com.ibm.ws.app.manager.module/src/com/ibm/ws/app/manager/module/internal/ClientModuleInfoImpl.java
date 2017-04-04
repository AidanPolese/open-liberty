/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.module.internal;

import java.util.List;

import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.container.service.app.deploy.ClientModuleInfo;
import com.ibm.ws.container.service.app.deploy.ContainerInfo;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 *
 */
public class ClientModuleInfoImpl extends ExtendedModuleInfoImpl implements ClientModuleInfo {

    private final String mainClassName;

    public ClientModuleInfoImpl(ApplicationInfo appInfo, String moduleName, String path,
                                Container moduleContainer, Entry altDDEntry, List<ContainerInfo> moduleClassesContainers,
                                String mainClassName, ModuleClassLoaderFactory classLoaderFactory) throws UnableToAdaptException {
        super(appInfo, moduleName, path, moduleContainer, altDDEntry, moduleClassesContainers, classLoaderFactory,
              ContainerInfo.Type.CLIENT_MODULE, ClientModuleInfo.class);

        this.mainClassName = mainClassName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.container.service.app.deploy.ClientModuleInfo#getMainClassName()
     */
    @Override
    public String getMainClassName() {
        return mainClassName;
    }
}
