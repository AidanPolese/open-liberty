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
package com.ibm.ws.webcontainer.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.ws.javaee.dd.common.Description;
import com.ibm.ws.javaee.dd.common.MessageDestinationRef;

/**
 *
 */
public class MessageDestinationRefImpl extends AbstractResourceGroup implements MessageDestinationRef {

    private List<Description> descriptions;

    private String type;
    private String link;
    private int usageValue;

    public MessageDestinationRefImpl(MessageDestinationRef messageDestRef) {
        super(messageDestRef);
        this.descriptions = new ArrayList<Description>(messageDestRef.getDescriptions());
        this.type = messageDestRef.getType();
        this.usageValue = messageDestRef.getUsageValue();
        this.link = messageDestRef.getLink();
    }

    /** {@inheritDoc} */
    @Override
    public List<Description> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.javaee.dd.common.MessageDestinationRef#getType()
     */
    @Override
    public String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.javaee.dd.common.MessageDestinationRef#getUsageValue()
     */
    @Override
    public int getUsageValue() {
         return usageValue;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.javaee.dd.common.MessageDestinationRef#getLink()
     */
    @Override
    public String getLink() {
        return link;
    }
}
