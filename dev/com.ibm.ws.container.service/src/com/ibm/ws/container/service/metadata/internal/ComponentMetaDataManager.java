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

import com.ibm.ws.container.service.metadata.ComponentMetaDataListener;
import com.ibm.ws.container.service.metadata.MetaDataEvent;
import com.ibm.ws.container.service.metadata.MetaDataException;
import com.ibm.ws.runtime.metadata.ComponentMetaData;

class ComponentMetaDataManager extends MetaDataManager<ComponentMetaData, ComponentMetaDataListener> {
    ComponentMetaDataManager(String listenerRefName) {
        super(listenerRefName);
    }

    @Override
    public Class<ComponentMetaData> getMetaDataClass() {
        return ComponentMetaData.class;
    }

    @Override
    public void fireMetaDataCreated(ComponentMetaDataListener listener, MetaDataEvent<ComponentMetaData> event) throws MetaDataException {
        listener.componentMetaDataCreated(event);
    }

    @Override
    public void fireMetaDataDestroyed(ComponentMetaDataListener listener, MetaDataEvent<ComponentMetaData> event) {
        listener.componentMetaDataDestroyed(event);
    }
}
