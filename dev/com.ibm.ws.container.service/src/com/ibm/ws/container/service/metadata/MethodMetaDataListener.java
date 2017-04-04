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

import com.ibm.ws.runtime.metadata.MethodMetaData;

public interface MethodMetaDataListener {
    /**
     * Notification that the metadata for a method has been created.
     * 
     * @param event the event
     */
    void methodMetaDataCreated(MetaDataEvent<MethodMetaData> event);

    /**
     * Notification that the metadata for a method has been destroyed.
     * 
     * This event might be fired without a corresponding {@link #methodMetaDataCreated} event if an error occurred
     * while creating the metadata.
     * 
     * @param event the event
     */
    void methodMetaDataDestroyed(MetaDataEvent<MethodMetaData> event);
}
