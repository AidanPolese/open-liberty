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

import com.ibm.ws.javaee.dd.common.ResourceBaseGroup;

public abstract class ResourceBaseGroupComparator<T extends ResourceBaseGroup> extends AbstractBaseComparator<T> {

    @Override
    public boolean compare(T o1, T o2) {
        if (o1.getName() == null) {
            if (o2.getName() != null)
                return false;
        } else if (!o1.getName().equals(o2.getName())) {
            return false;
        }
        if (o1.getMappedName() == null) {
            if (o2.getMappedName() != null)
                return false;
        } else if (!o1.getMappedName().equals(o2.getMappedName())) {
            return false;
        }
        return true;
    }
}
