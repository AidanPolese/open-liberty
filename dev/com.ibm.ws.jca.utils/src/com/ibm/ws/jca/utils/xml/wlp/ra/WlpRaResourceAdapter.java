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

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * ra.xml resourceadapter element
 */
@Trivial
@XmlType
public class WlpRaResourceAdapter {
    @XmlElement(name = "config-property")
    private final List<WlpRaConfigProperty> configProperties = new LinkedList<WlpRaConfigProperty>();
    private WlpRaOutboundResourceAdapter outboundResourceAdapter;
    private WlpRaInboundResourceAdapter inboundResourceAdapter;
    @XmlElement(name = "adminobject")
    private final List<WlpRaAdminObject> adminObjects = new LinkedList<WlpRaAdminObject>();

    @XmlAttribute(name = "nlsKey")
    private String wlp_nlsKey;
    @XmlAttribute(name = "name")
    private String wlp_name;
    @XmlAttribute(name = "description")
    private String wlp_description;
    @XmlAttribute(name = "autoStart")
    private Boolean wlp_autoStart;

    public String getName() {
        return wlp_name;
    }

    public String getDescription() {
        return wlp_description;
    }

    public String getNLSKey() {
        return wlp_nlsKey;
    }

    public Boolean getAutoStart() {
        return wlp_autoStart;
    }

    public List<WlpRaConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @XmlElement(name = "outbound-resourceadapter")
    public void setOutboundResourceAdapter(WlpRaOutboundResourceAdapter outboundResourceAdapter) {
        this.outboundResourceAdapter = outboundResourceAdapter;
    }

    public WlpRaOutboundResourceAdapter getOutboundResourceAdapter() {
        return outboundResourceAdapter;
    }

    @XmlElement(name = "inbound-resourceadapter")
    public void setInboundResourceAdapter(WlpRaInboundResourceAdapter inboundResourceAdapter) {
        this.inboundResourceAdapter = inboundResourceAdapter;
    }

    public WlpRaInboundResourceAdapter getInboundResourceAdapter() {
        return inboundResourceAdapter;
    }

    public List<WlpRaAdminObject> getAdminObjects() {
        return adminObjects;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WlpRaResourceAdapter{");
        sb.append("name='");
        if (wlp_name != null)
            sb.append(wlp_name);
        sb.append("'}");
        return sb.toString();
    }
}
