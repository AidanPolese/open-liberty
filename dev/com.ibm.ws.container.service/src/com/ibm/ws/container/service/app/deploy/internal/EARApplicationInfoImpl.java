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
package com.ibm.ws.container.service.app.deploy.internal;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.container.service.app.deploy.NestedConfigHelper;
import com.ibm.ws.container.service.app.deploy.extended.AppClassLoaderFactory;
import com.ibm.ws.container.service.app.deploy.extended.ExtendedEARApplicationInfo;
import com.ibm.wsspi.adaptable.module.Container;

/**
 *
 */
final class EARApplicationInfoImpl extends ApplicationInfoImpl implements ExtendedEARApplicationInfo {

    private final Container libDirContainer;
    private final AppClassLoaderFactory appClassLoaderFactory;
    private volatile ClassLoader appClassLoader;

    EARApplicationInfoImpl(String appName, J2EEName j2eeName, Container appContainer, NestedConfigHelper configHelper,
                           Container libDirContainer, AppClassLoaderFactory appClassLoaderFactory) {
        super(appName, j2eeName, appContainer, configHelper);
        this.libDirContainer = libDirContainer;
        this.appClassLoaderFactory = appClassLoaderFactory;
    }

    @Override
    public Container getLibraryDirectoryContainer() {
        return libDirContainer;
    }

    @Override
    public synchronized ClassLoader getApplicationClassLoader() {
        if (appClassLoader == null && appClassLoaderFactory != null) {
            appClassLoader = appClassLoaderFactory.createAppClassLoader();
        }
        return appClassLoader;
    }
}
