/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.app.deploy.internal;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;
import com.ibm.ws.runtime.metadata.MetaDataImpl;

/**
 *
 */
class ApplicationMetaDataImpl extends MetaDataImpl implements ApplicationMetaData {

    private final J2EEName j2eeName;

    public ApplicationMetaDataImpl(J2EEName j2eeName) {
        super(0);
        this.j2eeName = j2eeName;
    }

    @Override
    public String getName() {
        return this.j2eeName.getApplication();
    }

    @Override
    public J2EEName getJ2EEName() {
        return this.j2eeName;
    }

    @Override
    public boolean createComponentMBeans() {
        return false;
    }

}
