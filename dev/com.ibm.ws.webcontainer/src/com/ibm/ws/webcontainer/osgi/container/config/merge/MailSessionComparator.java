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

import com.ibm.ws.javaee.dd.common.MailSession;

public class MailSessionComparator extends AbstractBaseComparator<MailSession> {

    @Override
    public boolean compare(MailSession o1, MailSession o2) {
        if (!isMatchingString(o1.getName(), o2.getName())) {
            return false;
        }
        if (!isMatchingString(o1.getUser(), o2.getUser())) {
            return false;
        }
        if (!isMatchingString(o1.getPassword(), o2.getPassword())) {
            return false;
        }
        if (!isMatchingString(o1.getStoreProtocol(), o2.getStoreProtocol())) {
            return false;
        }
        if (!isMatchingString(o1.getStoreProtocolClassName(), o2.getStoreProtocolClassName())) {
            return false;
        }
        if (!isMatchingString(o1.getTransportProtocol(), o2.getTransportProtocol())) {
            return false;
        }
        if (!isMatchingString(o1.getTransportProtocolClassName(), o2.getTransportProtocolClassName())) {
            return false;
        }
        if (!isMatchingString(o1.getHost(), o2.getHost())) {
            return false;
        }
        if (!isMatchingString(o1.getFrom(), o2.getFrom())) {
            return false;
        }
        if (!compareProperties(o1.getProperties(), o2.getProperties())) {
            return false;
        }
        return true;
    }

    /**
     * Compare two string values.
     * 
     * @param value1
     * @param value2
     * @return true if matching
     */
    private boolean isMatchingString(String value1, String value2) {
        
        boolean valuesMatch = true;
        
        if (value1 == null) {
            if (value2 != null) {
                valuesMatch = false;
            }
        } else { 
            valuesMatch = value1.equals(value2);
        }
        
        return valuesMatch;
    }
}
