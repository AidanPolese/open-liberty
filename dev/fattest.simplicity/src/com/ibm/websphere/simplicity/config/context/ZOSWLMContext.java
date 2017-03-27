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
package com.ibm.websphere.simplicity.config.context;

import javax.xml.bind.annotation.XmlAttribute;

import com.ibm.websphere.simplicity.config.ConfigElement;

/**
 * Represents the <zosWLMContext> element which can be nested under <contextService>
 */
public class ZOSWLMContext extends ConfigElement {
    private String daemonTransactionClass;
    private String defaultTransactionClass;
    private String wlm;

    public String getDaemonTransactionClass() {
        return daemonTransactionClass;
    }

    public String getDefaultTransactionClass() {
        return defaultTransactionClass;
    }

    public String getWLM() {
        return wlm;
    }

    @XmlAttribute(name = "daemonTransactionClass")
    public void setDaemonTransactionClass(String value) {
        daemonTransactionClass = value;
    }

    @XmlAttribute(name = "defaultTransactionClass")
    public void setDefaultTransactionClass(String value) {
        defaultTransactionClass = value;
    }

    @XmlAttribute(name = "wlm")
    public void setWLM(String value) {
        wlm = value;
    }

    /**
     * Returns a string containing a list of the properties and their values.
     * 
     * @return String representing the data
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("zosWLMContext{");
        if (getId() != null)
            buf.append("id=").append(getId()).append(' ');
        if (wlm != null)
            buf.append("wlm=").append(wlm).append(' ');
        if (daemonTransactionClass != null)
            buf.append("daemonTransactionClass=").append(daemonTransactionClass).append(' ');
        if (defaultTransactionClass != null)
            buf.append("defaultTransactionClass=").append(defaultTransactionClass).append(' ');
        buf.append("}");
        return buf.toString();
    }
}