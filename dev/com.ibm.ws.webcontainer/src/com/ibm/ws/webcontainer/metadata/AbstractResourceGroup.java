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

import com.ibm.ws.javaee.dd.common.ResourceGroup;

/**
 *
 */
public abstract class AbstractResourceGroup extends AbstractResourceBaseGroup implements ResourceGroup {

    private String lookupName;

    public AbstractResourceGroup(ResourceGroup resourceGroup) {
        super(resourceGroup);
        this.lookupName = resourceGroup.getLookupName();
        if (lookupName != null) {
            lookupName = lookupName.trim();
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getLookupName() {
        return lookupName;
    }

}
