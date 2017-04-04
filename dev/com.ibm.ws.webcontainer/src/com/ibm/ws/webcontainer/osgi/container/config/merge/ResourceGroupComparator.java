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

import com.ibm.ws.javaee.dd.common.ResourceGroup;

public abstract class ResourceGroupComparator<T extends ResourceGroup> extends ResourceBaseGroupComparator<T> {

    private String trim(String s) {
        return s != null ? s.trim() : null;
    }

    @Override
    public boolean compare(T o1, T o2) {
        if (!super.compare(o1, o2)) {
            return false;
        }
        if (o1.getLookupName() == null) {
            return o2.getLookupName() == null;
        } else {
            return o1.getLookupName().trim().equals(trim(o2.getLookupName()));
        }
    }

}
