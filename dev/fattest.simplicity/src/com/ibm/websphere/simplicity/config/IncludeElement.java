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
public class IncludeElement extends ConfigElement {
    private String location;

    public String getLocation() {
        return location;
    }

    @XmlAttribute(name = "location")
    public void setLocation(String s) {
        this.location = s;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("IncludeElement{");
        if (this.getId() != null)
            buf.append("id=\"" + this.getId() + "\" ");
        if (location != null)
            buf.append("location=\"" + location + "\" ");

        buf.append("}");
        return buf.toString();
    }
}
