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

import java.util.List;

import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.container.service.app.deploy.ContainerInfo;
import com.ibm.ws.container.service.app.deploy.EJBModuleInfo;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 *
 */
public class EJBModuleInfoImpl extends ExtendedModuleInfoImpl implements EJBModuleInfo {

    public EJBModuleInfoImpl(ApplicationInfo appInfo, String moduleName, String path,
                             Container moduleContainer, Entry altDDEntry, List<ContainerInfo> moduleClassesContainers,
                             ModuleClassLoaderFactory classLoaderFactory) throws UnableToAdaptException {
        super(appInfo, moduleName, path, moduleContainer, altDDEntry, moduleClassesContainers, classLoaderFactory,
              ContainerInfo.Type.EJB_MODULE, EJBModuleInfo.class);
    }
}
