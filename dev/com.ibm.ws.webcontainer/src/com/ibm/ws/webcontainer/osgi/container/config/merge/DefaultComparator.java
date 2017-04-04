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

import com.ibm.ws.container.service.config.ServletConfigurator.MergeComparator;

public class DefaultComparator implements MergeComparator<Object> {

    @Override
    public boolean compare(Object o1, Object o2) {
        if (o1 == null) {
            if (o2 != null) {
                return false;
            }
        } else if (!o1.equals(o2)) {
            return false;
        }
        return true;
    }

}
