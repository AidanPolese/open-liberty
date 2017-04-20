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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.ibm.ws.container.service.metadata.MetaDataEvent;
import com.ibm.ws.runtime.metadata.MetaData;
import com.ibm.wsspi.adaptable.module.Container;

class MetaDataEventChecker<M extends MetaData> extends TypeSafeMatcher<MetaDataEvent<M>> {
    M metaData;
    Container container;

    MetaDataEventChecker(M metaData, Container container) {
        init(metaData, container);
    }

    public void init(M metaData, Container container) {
        this.metaData = metaData;
        this.container = container;
    }

    @Override
    public boolean matchesSafely(MetaDataEvent<M> event) {
        return event.getMetaData() == metaData &&
               event.getContainer() == container;
    }

    @Override
    public void describeTo(Description desc) {
        desc.appendText("a MetaDataEvent with specified metadata and adaptable");
    }
}
