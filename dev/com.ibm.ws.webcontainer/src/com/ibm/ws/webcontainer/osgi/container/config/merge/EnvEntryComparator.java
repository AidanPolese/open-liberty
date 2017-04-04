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

import com.ibm.ws.javaee.dd.common.EnvEntry;

public class EnvEntryComparator extends ResourceGroupComparator<EnvEntry> {

    @Override
    public boolean compare(EnvEntry o1, EnvEntry o2) {
        if (!super.compare(o1, o2)) {
            return false;
        }
        if (o1.getValue() == null) {
            if (o2.getValue() != null)
                return false;
        } else if (!o1.getValue().equals(o2.getValue())) {
            return false;
        }
        if (o1.getTypeName() == null) {
            if (o2.getTypeName() != null)
                return false;
        } else if (!o1.getTypeName().equals(o2.getTypeName())) {
            return false;
        }
        return compareDescriptions(o1.getDescriptions(), o2.getDescriptions());
    }

}
