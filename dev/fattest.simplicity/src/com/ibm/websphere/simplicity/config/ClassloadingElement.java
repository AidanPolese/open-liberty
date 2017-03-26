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
 * A top level global "classloading" element, rather than an app's classloader.
 */
public class ClassloadingElement extends ConfigElement {
    private Boolean useJarUrls = false;

    public Boolean getUseJarUrls() {
        return useJarUrls;
    }

    @XmlAttribute(name = "useJarUrls")
    public void setUseJarUrls(Boolean b) {
        this.useJarUrls = b;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("ClassloadingElement{");
        if (useJarUrls != null)
            buf.append("useJarUrls=\"" + useJarUrls + "\" ");
        buf.append("}");
        return buf.toString();
    }
}
