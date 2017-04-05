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

import com.ibm.ws.runtime.metadata.MetaData;
import com.ibm.wsspi.adaptable.module.Container;

public interface MetaDataEvent<M extends MetaData> {
    /**
     * The metadata for the event.
     */
    M getMetaData();

    /**
     * The {@link Container} associated with this metadata event, or null if no
     * Container is associated with this event.
     */
    Container getContainer();
}
