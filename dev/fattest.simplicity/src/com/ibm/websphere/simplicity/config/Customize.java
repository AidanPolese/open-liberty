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
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents the <customize> element which can be nested under <resourceAdapter> in server.xml
 */
public class Customize extends ConfigElement {
    // attributes
    private String implementation;
    private String interfaceName;
    private String suffix;

    public String getImplementation() {
        return implementation;
    }

    public String getInterface() {
        return interfaceName;
    }

    public String getSuffix() {
        return suffix;
    }

    @XmlAttribute
    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    @XmlAttribute
    public void setInterface(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @XmlAttribute
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(getClass().getSimpleName()).append('{');
        if (getId() != null)
            buf.append("id=").append(getId()).append(' ');
        if (implementation != null)
            buf.append("implementation=").append(implementation).append(' ');
        if (interfaceName != null)
            buf.append("interfaceName=").append(interfaceName).append(' ');
        if (suffix != null)
            buf.append("suffix=").append(suffix).append(' ');
        buf.append('}');
        return buf.toString();
    }
}
