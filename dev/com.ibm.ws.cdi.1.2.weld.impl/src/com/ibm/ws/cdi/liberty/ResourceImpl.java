/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.liberty;

import java.net.URL;

import com.ibm.ws.cdi.interfaces.Resource;
import com.ibm.wsspi.adaptable.module.Entry;

/**
 *
 */
public class ResourceImpl implements Resource {

    private final Entry entry;

    /**
     * @param entry
     */
    public ResourceImpl(Entry entry) {
        this.entry = entry;
    }

    /** {@inheritDoc} */
    @Override
    public URL getURL() {
        return entry.getResource();
    }

    @Override
    public String toString() {
        return "ResourceImpl: " + entry.getPath();
    }

}
