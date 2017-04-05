/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.metadata;

import com.ibm.ws.runtime.metadata.ComponentMetaData;

public interface ComponentMetaDataListener {
    /**
     * Notification that the component for a module has been created.
     * 
     * @param event the event
     */
    void componentMetaDataCreated(MetaDataEvent<ComponentMetaData> event);

    /**
     * Notification that the metadata for a component has been destroyed.
     * 
     * This event might be fired without a corresponding {@link #componentMetaDataCreated} event if an error
     * occurred while creating the metadata.
     * 
     * @param event the event
     */
    void componentMetaDataDestroyed(MetaDataEvent<ComponentMetaData> event);
}
