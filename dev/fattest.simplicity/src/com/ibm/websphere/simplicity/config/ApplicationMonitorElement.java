/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 */
public class ApplicationMonitorElement extends ConfigElement {
    private Boolean enabled;
    private String dropins;

    public Boolean getEnabled() {
        return enabled;
    }

    public String getDropins() {
        return dropins;
    }

    @XmlAttribute(name = "enabled")
    public void setEnabled(Boolean b) {
        this.enabled = b;
    }

    @XmlAttribute(name = "dropins")
    public void setDropins(String s) {
        this.dropins = s;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("ApplicationMonitorElement{");
        if (enabled != null)
            buf.append("enabled=\"" + enabled + "\" ");
        if (dropins != null)
            buf.append("dropins=\"" + dropins + "\"");
        buf.append("}");
        return buf.toString();
    }
}
