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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * ra.xml outbound-resourseadapter element
 */
@Trivial
@XmlType
public class WlpRaOutboundResourceAdapter {
    @XmlElement(name = "connection-definition")
    private final List<WlpRaConnectionDefinition> connectionDefinitions = new LinkedList<WlpRaConnectionDefinition>();

    public List<WlpRaConnectionDefinition> getConnectionDefinitions() {
        return connectionDefinitions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WlpRaOutboundResourceAdapter{");
        for (WlpRaConnectionDefinition connectionDefinition : connectionDefinitions) {
            sb.append(connectionDefinition.toString()).append(",");
        }
        sb.append("}");
        return sb.toString();
    }
}
