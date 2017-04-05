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

import com.ibm.ws.container.service.metadata.MetaDataEvent;
import com.ibm.ws.container.service.metadata.MetaDataException;
import com.ibm.ws.container.service.metadata.ModuleMetaDataListener;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

public class ModuleMetaDataManager extends MetaDataManager<ModuleMetaData, ModuleMetaDataListener> {
    ModuleMetaDataManager(String listenerRefName) {
        super(listenerRefName);
    }

    @Override
    public Class<ModuleMetaData> getMetaDataClass() {
        return ModuleMetaData.class;
    }

    @Override
    public void fireMetaDataCreated(ModuleMetaDataListener listener, MetaDataEvent<ModuleMetaData> event) throws MetaDataException {
        listener.moduleMetaDataCreated(event);
    }

    @Override
    public void fireMetaDataDestroyed(ModuleMetaDataListener listener, MetaDataEvent<ModuleMetaData> event) {
        listener.moduleMetaDataDestroyed(event);
    }
}
