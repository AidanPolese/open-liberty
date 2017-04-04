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
package com.ibm.ws.webcontainer.osgi.container.config.merge;

import com.ibm.ws.javaee.dd.common.ResourceRef;

public class ResourceRefComparator extends ResourceGroupComparator<ResourceRef> {

    @Override
    public boolean compare(ResourceRef o1, ResourceRef o2) {
        if (!super.compare(o1, o2)) {
            return false;
        }
        if (o1.getType() == null) {
            if (o2.getType() != null)
                return false;
        } else if (!o1.getType().equals(o2.getType())) {
            return false;
        }
        if (o1.getAuthValue() != o2.getAuthValue()) {
            return false;
        }
        if (o1.getSharingScopeValue() != o2.getSharingScopeValue()) {
            return false;
        }
        return compareDescriptions(o1.getDescriptions(), o2.getDescriptions());
    }

}
