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
package com.ibm.ws.jca.utils.xml.wlp.ra;

import javax.xml.bind.annotation.XmlAttribute;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * wlp-ra.xml option element
 */
@Trivial
public class WlpConfigOption {
    @XmlAttribute(name = "label")
    private String label;
    @XmlAttribute(name = "value")
    private String value;
    @XmlAttribute(name = "nlsKey")
    private String wlp_nlsKey;

    public String getNLSKey() {
        return wlp_nlsKey;
    }

    public String getLabel() {
        if (label == null)
            return value;
        else
            return label;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('{');
        sb.append("value='" + value + "' ");
        sb.append("label='" + getLabel() + "' ");
        if (wlp_nlsKey != null)
            sb.append("nlsKey='" + wlp_nlsKey + "' ");
        sb.append("}");

        return sb.toString();
    }
}
