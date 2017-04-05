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

import com.ibm.ws.javaee.dd.common.EJBRef;

public class EJBRefComparator extends ResourceGroupComparator<EJBRef> {

    @Override
    public boolean compare(EJBRef o1, EJBRef o2) {
        if (!super.compare(o1, o2)) {
            return false;
        }
        if (o1.getHome() == null) {
            if (o2.getHome() != null)
                return false;
        } else if (!o1.getHome().equals(o2.getHome())) {
            return false;
        }
        if (o1.getInterface() == null) {
            if (o2.getInterface() != null)
                return false;
        } else if (!o1.getInterface().equals(o2.getInterface())) {
            return false;
        }
        if (o1.getKindValue() != o2.getKindValue()) {
            return false;
        }
        if (o1.getLink() == null) {
            if (o2.getLink() != null)
                return false;
        } else if (!o1.getLink().equals(o2.getLink())) {
            return false;
        }
        if (o1.getTypeValue() != o2.getTypeValue()) {
            return false;
        }
        return compareDescriptions(o1.getDescriptions(), o2.getDescriptions());
    }

}
