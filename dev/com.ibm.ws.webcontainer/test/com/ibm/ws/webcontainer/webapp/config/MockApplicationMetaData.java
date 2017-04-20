/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.webapp.config;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;
import com.ibm.ws.runtime.metadata.MetaDataImpl;

/**
 *
 */
public class MockApplicationMetaData extends MetaDataImpl implements ApplicationMetaData {

    private J2EEName ivJ2eeName = null;

    public MockApplicationMetaData(J2EEName j2eeName) {
        super(1);
        ivJ2eeName = j2eeName;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "app";
    }

    /** {@inheritDoc} */
    @Override
    public J2EEName getJ2EEName() {
        return ivJ2eeName;
    }

    @Override
    public boolean createComponentMBeans() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}