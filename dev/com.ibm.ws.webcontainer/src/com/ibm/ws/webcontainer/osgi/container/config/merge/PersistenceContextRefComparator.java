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

import com.ibm.ws.javaee.dd.common.PersistenceContextRef;

/**
 *
 */
public class PersistenceContextRefComparator extends ResourceBaseGroupComparator<PersistenceContextRef> {

    @Override
    public boolean compare(PersistenceContextRef o1, PersistenceContextRef o2) {
        if (!super.compare(o1, o2)) {
            return false;
        }
        if (o1.getPersistenceUnitName() == null) {
            if (o2.getPersistenceUnitName() != null)
                return false;
        } else if (!o1.getPersistenceUnitName().equals(o2.getPersistenceUnitName())) {
            return false;
        }
        if (o1.getTypeValue() != o2.getTypeValue()) {
            return false;
        }
        if (!compareProperties(o1.getProperties(), o2.getProperties())) {
            return false;
        }
        return compareDescriptions(o1.getDescriptions(), o2.getDescriptions());
    }

}
