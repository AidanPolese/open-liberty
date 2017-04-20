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
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

public class TestComponentMetaData extends TestMetaDataImpl implements ComponentMetaData {
    @Override
    public ModuleMetaData getModuleMetaData() {
        return null;
    }

    @Override
    public J2EEName getJ2EEName() {
        return null;
    }
}
