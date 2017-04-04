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
package com.ibm.ws.container.service.metadata.internal;

import com.ibm.ws.container.service.metadata.MetaDataEvent;
import com.ibm.ws.runtime.metadata.MetaData;
import com.ibm.wsspi.adaptable.module.Container;

public class MetaDataEventImpl<M extends MetaData> implements MetaDataEvent<M> {

    private final M metaData;
    private final Container container;

    MetaDataEventImpl(M metaData, Container container) {
        this.metaData = metaData;
        this.container = container;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + metaData + ", " + container + ']';
    }

    @Override
    public M getMetaData() {
        return metaData;
    }

    @Override
    public Container getContainer() {
        return container;
    }

}
