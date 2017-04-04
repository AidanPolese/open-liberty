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
import com.ibm.ws.javaee.dd.common.ResourceEnvRef;

/**
 *
 */
public class ResourceEnvRefImpl extends AbstractResourceGroup implements ResourceEnvRef {

    private List<Description> descriptions;

    private String typeName;

    public ResourceEnvRefImpl(ResourceEnvRef resourceEnvRef) {
        super(resourceEnvRef);
        this.typeName = resourceEnvRef.getTypeName();
        this.descriptions = new ArrayList<Description>(resourceEnvRef.getDescriptions());
    }

    /** {@inheritDoc} */
    @Override
    public List<Description> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }

    /** {@inheritDoc} */
    @Override
    public String getTypeName() {
        return typeName;
    }

}
