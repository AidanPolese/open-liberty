/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents a &lt;webApplication> configuration element
 */
public class WebApplication extends Application {

    private String contextRoot;

    /**
     * @return the contextRoot
     */
    public String getContextRoot() {
        return contextRoot;
    }

    /**
     * @param contextRoot the contextRoot to set
     */
    @XmlAttribute
    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("WebApplication{");
        buf.append(super.toString());
        if (contextRoot != null)
            buf.append("contextRoot=\"" + contextRoot + "\" ");
        buf.append("}");

        return buf.toString();
    }

    @Override
    public WebApplication clone() throws CloneNotSupportedException {
        return (WebApplication) super.clone();
    }

}
