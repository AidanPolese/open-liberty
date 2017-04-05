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

import com.ibm.ws.container.service.metadata.ApplicationMetaDataListener;
import com.ibm.ws.container.service.metadata.MetaDataEvent;
import com.ibm.ws.container.service.metadata.MetaDataException;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;

class ApplicationMetaDataManager extends MetaDataManager<ApplicationMetaData, ApplicationMetaDataListener> {
    ApplicationMetaDataManager(String listenerRefName) {
        super(listenerRefName);
    }

    @Override
    public Class<ApplicationMetaData> getMetaDataClass() {
        return ApplicationMetaData.class;
    }

    @Override
    public void fireMetaDataCreated(ApplicationMetaDataListener listener, MetaDataEvent<ApplicationMetaData> event) throws MetaDataException {
        listener.applicationMetaDataCreated(event);
    }

    @Override
    public void fireMetaDataDestroyed(ApplicationMetaDataListener listener, MetaDataEvent<ApplicationMetaData> event) {
        listener.applicationMetaDataDestroyed(event);
    }
}
