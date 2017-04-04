/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.osgi.container.config.merge;

import com.ibm.ws.javaee.dd.common.ConnectionFactory;

public class ConnectionFactoryComparator extends AbstractBaseComparator<ConnectionFactory> {

    @Override
    public boolean compare(ConnectionFactory o1, ConnectionFactory o2) {
        if (o1.getName() == null) {
            if (o2.getName() != null)
                return false;
        } else if (!o1.getName().equals(o2.getName())) {
            return false;
        }
        if (o1.getInterfaceNameValue() == null) {
            if (o2.getInterfaceNameValue() != null)
                return false;
        } else if (!o1.getInterfaceNameValue().equals(o2.getInterfaceNameValue())) {
            return false;
        }
        if (o1.getMaxPoolSize()!=o2.getMaxPoolSize()) {
            return false;
        }
        if (o1.getMinPoolSize()!=o2.getMinPoolSize()) {
            return false;
        }
        if (o1.getTransactionSupportValue()!=o2.getTransactionSupportValue()) {
            return false;
        }

        if (o1.getName() == null) {
            if (o2.getName() != null)
                return false;
        } else if (!o1.getName().equals(o2.getName())) {
            return false;
        }
        
        if (!compareProperties(o1.getProperties(), o2.getProperties())) {
            return false;
        }
        if (!compareDescriptions(o1.getDescriptions(), o2.getDescriptions())) {
            return false;
        }
        if (o1.getResourceAdapter() == null) {
            if (o2.getResourceAdapter() != null)
                return false;
        } else if (!o1.getResourceAdapter().equals(o2.getResourceAdapter())) {
            return false;
        }
        return true;
    }

}
