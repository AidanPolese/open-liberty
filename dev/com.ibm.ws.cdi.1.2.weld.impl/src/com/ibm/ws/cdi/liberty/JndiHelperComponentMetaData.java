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
import com.ibm.ws.runtime.metadata.MetaDataImpl;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

/**
 * A Component MetaData used for jndi validation
 */
public class JndiHelperComponentMetaData extends MetaDataImpl implements ComponentMetaData {

    private final ModuleMetaData module;

    public JndiHelperComponentMetaData(ModuleMetaData module) {
        super(0);
        this.module = module;
    }

    public JndiHelperComponentMetaData(ApplicationMetaData app) {
        this(new JndiHelperModuleMetaData(app));
    }

    @Override
    public ModuleMetaData getModuleMetaData() {
        return module;
    }

    @Override
    public J2EEName getJ2EEName() {
        return module.getJ2EEName();
    }

    @Override
    public String getName() {
        return module.getName();
    }
}
