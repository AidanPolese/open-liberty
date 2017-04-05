/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.app.deploy.extended;

import java.util.List;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

/**
 *
 */
public interface ExtendedModuleInfo extends ModuleInfo {
    /**
     * @return
     */
    ModuleMetaData getMetaData();

    /**
     * @param moduleType
     * @param nestedMetaData
     */
    void putNestedMetaData(String moduleType, ModuleMetaData nestedMetaData);

    /**
     * @param moduleType
     * @return
     */
    ModuleMetaData getNestedMetaData(String moduleType);

    /**
     * @return
     */
    List<ModuleMetaData> getNestedMetaData();
}
