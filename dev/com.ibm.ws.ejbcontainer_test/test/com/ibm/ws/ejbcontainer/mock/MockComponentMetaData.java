/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.mock;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.runtime.metadata.MetaDataSlot;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

/**
 *
 */
public class MockComponentMetaData implements ComponentMetaData {

    private J2EEName ivJ2eeName = null;
    private ModuleMetaData ivMMD = null;

    public MockComponentMetaData(J2EEName j2eeName, ModuleMetaData mmd) {
        ivJ2eeName = j2eeName;
        ivMMD = mmd;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "ejb";
    }

    /** {@inheritDoc} */
    @Override
    public void setMetaData(MetaDataSlot slot, Object metadata) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /** {@inheritDoc} */
    @Override
    public Object getMetaData(MetaDataSlot slot) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /** {@inheritDoc} */
    @Override
    public void release() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /** {@inheritDoc} */
    @Override
    public ModuleMetaData getModuleMetaData() {
        return ivMMD;
    }

    /** {@inheritDoc} */
    @Override
    public J2EEName getJ2EEName() {
        return ivJ2eeName;
    }

}
