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

import com.ibm.ws.javaee.dd.common.PersistenceUnitRef;

/**
 *
 */
public class PersistenceUnitRefComparator extends ResourceBaseGroupComparator<PersistenceUnitRef> {

    @Override
    public boolean compare(PersistenceUnitRef o1, PersistenceUnitRef o2) {
        if (!super.compare(o1, o2)) {
            return false;
        }
        if (o1.getPersistenceUnitName() == null) {
            if (o2.getPersistenceUnitName() != null)
                return false;
        } else if (!o1.getPersistenceUnitName().equals(o2.getPersistenceUnitName())) {
            return false;
        }
        return compareDescriptions(o1.getDescriptions(), o2.getDescriptions());
    }

}
