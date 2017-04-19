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
package com.ibm.ws.container.service.metadata.internal;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;

public class TestApplicationMetaData extends TestMetaDataImpl implements ApplicationMetaData {
    @Override
    public boolean createComponentMBeans() {
        return false;
    }

    @Override
    public J2EEName getJ2EEName() {
        return null;
    }
}
