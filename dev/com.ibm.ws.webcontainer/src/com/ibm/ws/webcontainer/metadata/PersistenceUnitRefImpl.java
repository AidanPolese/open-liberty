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
import com.ibm.ws.javaee.dd.common.PersistenceUnitRef;

/**
 *
 */
public class PersistenceUnitRefImpl extends AbstractResourceBaseGroup implements PersistenceUnitRef {

    private String persistenceUnitName;

    private List<Description> descriptions;

    public PersistenceUnitRefImpl(PersistenceUnitRef persistenceUnitRef) {
        super(persistenceUnitRef);
        this.persistenceUnitName = persistenceUnitRef.getPersistenceUnitName();
        this.descriptions = new ArrayList<Description>(persistenceUnitRef.getDescriptions());
    }

    /** {@inheritDoc} */
    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    /** {@inheritDoc} */
    @Override
    public List<Description> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }
}
