/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
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
public class EJBAsynchronousElement extends ConfigElement {

    private String contextServiceRef;
    private String unclaimedRemoteResultTimeout;
    private String maxUnclaimedRemoteResults;

    public String getContextServiceRef() {
        return contextServiceRef;
    }

    @XmlAttribute(name = "contextServiceRef")
    public void setContextServiceRef(String contextServiceRef) {
        this.contextServiceRef = contextServiceRef;
    }

    public String getUnclaimedRemoteResultTimeout() {
        return unclaimedRemoteResultTimeout;
    }

    @XmlAttribute(name = "unclaimedRemoteResultTimeout")
    public void setUnclaimedRemoteResultTimeout(String unclaimedRemoteResultTimeout) {
        this.unclaimedRemoteResultTimeout = unclaimedRemoteResultTimeout;
    }

    public String getMaxUnclaimedRemoteResults() {
        return maxUnclaimedRemoteResults;
    }

    @XmlAttribute(name = "maxUnclaimedRemoteResults")
    public void setMaxUnclaimedRemoteResults(String maxUnclaimedRemoteResults) {
        this.maxUnclaimedRemoteResults = maxUnclaimedRemoteResults;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("EJBAsynchronousElement {");

        if (contextServiceRef != null)
            buf.append("contextServiceRef=\"" + contextServiceRef + "\" ");
        if (unclaimedRemoteResultTimeout != null)
            buf.append("unclaimedRemoteResultTimeout=\"").append(unclaimedRemoteResultTimeout).append("\" ");
        if (maxUnclaimedRemoteResults != null)
            buf.append("maxUnclaimedRemoteResults=\"").append(maxUnclaimedRemoteResults).append("\" ");

        buf.append("}");
        return buf.toString();
    }
}
