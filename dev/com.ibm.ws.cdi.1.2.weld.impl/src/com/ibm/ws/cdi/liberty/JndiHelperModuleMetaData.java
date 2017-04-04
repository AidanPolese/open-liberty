/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.liberty;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.runtime.metadata.MetaDataSlot;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

/**
 * A module MetaData used for JNDI validation
 */
public class JndiHelperModuleMetaData implements ModuleMetaData {

    private final ApplicationMetaData application;

    public JndiHelperModuleMetaData(ApplicationMetaData application) {
        this.application = application;

    }

    @Override
    public void setMetaData(MetaDataSlot slot, Object metadata) {}

    @Override
    public void release() {}

    @Override
    public String getName() {
        return application.getName();
    }

    @Override
    public Object getMetaData(MetaDataSlot slot) {
        return null;
    }

    @Override
    public J2EEName getJ2EEName() {
        return application.getJ2EEName();
    }

    /** {@inheritDoc} */
    @Override
    public ApplicationMetaData getApplicationMetaData() {
        return application;
    }

    /** {@inheritDoc} */
    @Override
    public ComponentMetaData[] getComponentMetaDatas() {
        return null;
    }

}
