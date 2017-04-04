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
package com.ibm.ws.container.service.app.deploy.extended;

import java.util.concurrent.Future;

import com.ibm.ws.container.service.metadata.MetaDataException;
import com.ibm.ws.container.service.state.StateChangeException;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

/**
 *
 */
public interface ModuleRuntimeContainer {

    /**
     * @param moduleInfo
     * @return non-null metadata otherwise must throw a MetaDataException
     */
    ModuleMetaData createModuleMetaData(ExtendedModuleInfo moduleInfo) throws MetaDataException;

    /**
     * @param moduleInfo
     */
    Future<Boolean> startModule(ExtendedModuleInfo moduleInfo) throws StateChangeException;

    /**
     * @param moduleInfo
     */
    void stopModule(ExtendedModuleInfo moduleInfo);
}
