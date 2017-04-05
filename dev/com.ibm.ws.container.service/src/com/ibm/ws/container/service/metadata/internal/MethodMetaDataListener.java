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
import com.ibm.ws.container.service.metadata.MethodMetaDataListener;
import com.ibm.ws.runtime.metadata.MethodMetaData;

class MethodMetaDataManager extends MetaDataManager<MethodMetaData, MethodMetaDataListener> {
    MethodMetaDataManager(String listenerRefName) {
        super(listenerRefName);
    }

    @Override
    public Class<MethodMetaData> getMetaDataClass() {
        return MethodMetaData.class;
    }

    @Override
    public void fireMetaDataCreated(MethodMetaDataListener listener, MetaDataEvent<MethodMetaData> event) throws MetaDataException {
        listener.methodMetaDataCreated(event);
    }

    @Override
    public void fireMetaDataDestroyed(MethodMetaDataListener listener, MetaDataEvent<MethodMetaData> event) {
        listener.methodMetaDataDestroyed(event);
    }
}
