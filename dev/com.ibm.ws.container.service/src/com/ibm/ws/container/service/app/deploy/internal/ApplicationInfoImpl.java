/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.app.deploy.internal;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.container.service.app.deploy.NestedConfigHelper;
import com.ibm.ws.container.service.app.deploy.extended.ExtendedApplicationInfo;
import com.ibm.ws.container.service.metadata.extended.MetaDataGetter;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;
import com.ibm.wsspi.adaptable.module.Container;

/**
 *
 */
class ApplicationInfoImpl implements ExtendedApplicationInfo, MetaDataGetter<ApplicationMetaData> {

    private final String appName;
    private final ApplicationMetaData appMetaData;
    private final Container appContainer;
    private final NestedConfigHelper configHelper;

    ApplicationInfoImpl(String appName, J2EEName j2eeName, Container appContainer, NestedConfigHelper configHelper) {
        this.appName = appName;
        this.appMetaData = new ApplicationMetaDataImpl(j2eeName);
        this.appContainer = appContainer;
        this.configHelper = configHelper;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + appName + ']';
    }

    @Override
    public String getName() {
        return appName;
    }

    @Override
    public Container getContainer() {
        return appContainer;
    }

    @Override
    public ApplicationMetaData getMetaData() {
        return appMetaData;
    }

    @Override
    public NestedConfigHelper getConfigHelper() {
        return configHelper;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.container.service.app.deploy.ApplicationInfo#getDeploymentName()
     */
    @Override
    public String getDeploymentName() {
        if (appMetaData.getJ2EEName() == null)
            return null;
        return appMetaData.getJ2EEName().getApplication();
    }

}
