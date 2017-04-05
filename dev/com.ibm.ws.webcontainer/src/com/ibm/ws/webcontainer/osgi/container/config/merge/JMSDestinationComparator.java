/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.osgi.container.config.merge;

import com.ibm.ws.javaee.dd.common.JMSDestination;

public class JMSDestinationComparator extends AbstractBaseComparator<JMSDestination> {

    @Override
    public boolean compare(JMSDestination o1, JMSDestination o2) {
        //Name
        if (o1.getName() == null) {
            if (o2.getName() != null)
                return false;
        } else if (!o1.getName().equals(o2.getName())) {
            return false;
        }
        
        // Interface Name
        if (o1.getInterfaceNameValue() == null) {
            if (o2.getInterfaceNameValue() != null)
                return false;
        } else if (!o1.getInterfaceNameValue().equals(o2.getInterfaceNameValue())) {
            return false;
        }
 
        //Class Name
        if (o1.getClassNameValue() == null) {
            if (o2.getClassNameValue() != null)
                return false;
        } else if (!o1.getClassNameValue().equals(o2.getClassNameValue())) {
            return false;
        }

        // Resource Adapter
        if (o1.getResourceAdapter() == null) {
            if (o2.getResourceAdapter() != null)
                return false;
        } else if (!o1.getResourceAdapter().equals(o2.getResourceAdapter())) {
            return false;
        }
        
        //Destination Name
        if (o1.getDestinationName() == null) {
            if (o2.getDestinationName() != null)
                return false;
        } else if (!o1.getDestinationName().equals(o2.getDestinationName())) {
            return false;
        }
        
        //Properties
        if (!compareProperties(o1.getProperties(), o2.getProperties())) {
            return false;
        }
        
        //Description
        if (!compareDescriptions(o1.getDescriptions(), o2.getDescriptions())) {
            return false;
        }

        return true;
    }

}
