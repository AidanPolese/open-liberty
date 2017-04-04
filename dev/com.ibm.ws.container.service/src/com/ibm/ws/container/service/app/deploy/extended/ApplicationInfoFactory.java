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
package com.ibm.ws.container.service.app.deploy.extended;

import com.ibm.ws.container.service.app.deploy.ApplicationClassesContainerInfo;
import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.container.service.app.deploy.NestedConfigHelper;
import com.ibm.wsspi.adaptable.module.Container;

/**
 *
 */
public interface ApplicationInfoFactory {

    public ExtendedApplicationInfo createApplicationInfo(String appMgrName, String preferredName, Container container,
                                                         ApplicationClassesContainerInfo appClassesContainerInfo,
                                                         NestedConfigHelper configHelper);

    public ExtendedEARApplicationInfo createEARApplicationInfo(String appMgrName, String preferredName, Container container,
                                                               ApplicationClassesContainerInfo appClassesContainerInfo,
                                                               NestedConfigHelper configHelper,
                                                               Container libDirContainer, AppClassLoaderFactory classLoaderFactory);

    public void destroyApplicationInfo(ApplicationInfo appInfo);
}
