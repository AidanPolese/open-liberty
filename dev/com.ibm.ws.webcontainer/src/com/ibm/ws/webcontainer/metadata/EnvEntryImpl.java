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
package com.ibm.ws.webcontainer.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.ws.javaee.dd.common.Description;
import com.ibm.ws.javaee.dd.common.EnvEntry;

/**
 *
 */
public class EnvEntryImpl extends AbstractResourceGroup implements EnvEntry {

    private String typeName;

    private String value;

    private List<Description> descriptions;

    public EnvEntryImpl(EnvEntry envEntry) {
        super(envEntry);
        this.typeName = envEntry.getTypeName();
        this.value = envEntry.getValue();
        this.descriptions = new ArrayList<Description>(envEntry.getDescriptions());
    }

    /** {@inheritDoc} */
    @Override
    public String getTypeName() {
        return typeName;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public List<Description> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }

}
