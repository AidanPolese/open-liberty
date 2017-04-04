/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.utils.xml.wlp.ra;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * ra.xml inbound-resourceadapter element
 */
@Trivial
@XmlType
public class WlpRaInboundResourceAdapter {
    private WlpRaMessageAdapter messageAdapter;

    @XmlElement(name = "messageadapter")
    public void setMessageAdapter(WlpRaMessageAdapter messageAdapter) {
        this.messageAdapter = messageAdapter;
    }

    public WlpRaMessageAdapter getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    public String toString() {
        return "WlpRaInboundResourceAdapter{" + messageAdapter.toString() + "}";
    }
}
