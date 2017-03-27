/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
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
public class JmsEndpoint extends ConfigElement {

    private String host;
    private String wasJmsPort;
    private String wasJmsSSLPort;

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    @XmlAttribute
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the wasJmsPort
     */
    public String getWasJmsPort() {
        return wasJmsPort;
    }

    /**
     * @param wasJmsPort the wasJmsPort to set
     */
    @XmlAttribute
    public void setWasJmsPort(String wasJmsPort) {
        this.wasJmsPort = wasJmsPort;
    }

    /**
     * @return the wasJmsSSLPort
     */
    public String getWasJmsSSLPort() {
        return wasJmsSSLPort;
    }

    /**
     * @param wasJmsSSLPort the wasJmsSSLPort to set
     */
    @XmlAttribute
    public void setWasJmsSSLPort(String wasJmsSSLPort) {
        this.wasJmsSSLPort = wasJmsSSLPort;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("JmsEndpoint{");
        buf.append("id=\"" + this.getId() + "\" ");
        if (this.host != null)
            buf.append("host=\"" + this.host + "\" ");
        if (this.wasJmsPort != null)
            buf.append("wasJmsPort=\"" + this.wasJmsPort + "\" ");
        if (this.wasJmsSSLPort != null)
            buf.append("wasJmsSSLPort=\"" + this.wasJmsSSLPort + "\" ");

        buf.append("}");
        return buf.toString();
    }

}
