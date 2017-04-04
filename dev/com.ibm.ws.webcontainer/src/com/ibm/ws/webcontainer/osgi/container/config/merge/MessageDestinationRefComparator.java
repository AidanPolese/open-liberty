/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.osgi.container.config.merge;

import com.ibm.ws.javaee.dd.common.MessageDestinationRef;

/**
 *
 */
public class MessageDestinationRefComparator extends ResourceGroupComparator<MessageDestinationRef> {

    @Override
    public boolean compare(MessageDestinationRef o1, MessageDestinationRef o2) {
        if (!super.compare(o1, o2)) {
            return false;
        }
        if (o1.getType() == null) {
            if (o2.getType() != null)
                return false;
        } else if (!o1.getType().equals(o2.getType())) {
            return false;
        }
        
        if (o1.getLink() == null) {
            if (o2.getLink() != null)
                return false;
        } else if (!o1.getLink().equals(o2.getLink())) {
            return false;
        }
        
        if (o1.getUsageValue() != o2.getUsageValue()) {
                return false;
        }
        
        return compareDescriptions(o1.getDescriptions(), o2.getDescriptions());
    }

}
