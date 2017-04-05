/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.clientcontainer.internal;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.clientcontainer.metadata.ClientModuleMetaData;
import com.ibm.ws.container.service.metadata.extended.IdentifiableComponentMetaData;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.runtime.metadata.MetaDataImpl;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

/**
 *
 */
public class ClientComponentMetaDataImpl extends MetaDataImpl implements ComponentMetaData, IdentifiableComponentMetaData {

    private final ClientModuleMetaData cmmd;

    ClientComponentMetaDataImpl(ClientModuleMetaData cmmd) {
        super(0);
        this.cmmd = cmmd;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ModuleMetaData getModuleMetaData() {
        return cmmd;
    }

    /** {@inheritDoc} */
    @Override
    public J2EEName getJ2EEName() {
        return cmmd.getJ2EEName();
    }

    /** {@inheritDoc} */
    @Override
    public String getPersistentIdentifier() {
        return "CLIENT#" + cmmd.getJ2EEName().toString();
    }

}
