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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * ra.xml connector element. We are using this for now, until we get the STAX Parser implemented. If we do not go for the parser, then we can use
 * this with some refactoring such that there are implementations for all declared by Connector interface.
 */
@Trivial
@XmlRootElement(name = "connector")
@XmlType
public class WlpRaConnector {
    private String displayName;
    private WlpRaResourceAdapter resourceAdapter;

    @XmlElement(name = "groups")
    private WlpIbmuiGroups wlp_ibmuiGroups;

    public WlpIbmuiGroups getWlpIbmuiGroups() {
        return wlp_ibmuiGroups;
    }

    @XmlElement(name = "display-name")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @XmlElement(name = "resourceadapter")
    public void setResourceAdapter(WlpRaResourceAdapter resourceAdapter) {
        this.resourceAdapter = resourceAdapter;
    }

    public WlpRaResourceAdapter getResourceAdapter() {
        return resourceAdapter;
    }

    public void copyWlpSettings(WlpRaConnector connector) {
        wlp_ibmuiGroups = connector.wlp_ibmuiGroups;
    }

    public WlpRaMessageListener getMessageListener(String messageListenerType) {
        WlpRaInboundResourceAdapter inbound = resourceAdapter.getInboundResourceAdapter();
        if (inbound == null)
            return null;
        else {
            WlpRaMessageAdapter messageAdapter = inbound.getMessageAdapter();
            if (messageAdapter == null)
                return null;
            else {
                return messageAdapter.getMessageListenerByType(messageListenerType);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WlpRaConnector{display-name='");
        if (displayName != null)
            sb.append(displayName);
        sb.append("'}");
        return sb.toString();
    }
}
