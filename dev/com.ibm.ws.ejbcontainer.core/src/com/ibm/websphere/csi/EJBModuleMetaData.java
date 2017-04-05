/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

import com.ibm.ws.runtime.metadata.ModuleMetaData;

/**
 * @ibm-private-in-use
 */
public interface EJBModuleMetaData
                extends ModuleMetaData
{
    /**
     * Get the Module Version of this EJB Module
     */
    public int getEJBModuleVersion();

    /**
     * Set the base application and module names for EJBs defined in this
     * module if it uses a forward-compatible version strategy.
     */
    // F54184 F54184.2
    public void setVersionedModuleBaseName(String appBaseName, String modBaseName);
}
