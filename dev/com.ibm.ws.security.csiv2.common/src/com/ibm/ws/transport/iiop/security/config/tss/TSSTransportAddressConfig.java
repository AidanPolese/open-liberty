/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security.config.tss;

import java.io.Serializable;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * @version $Revision: 503274 $ $Date: 2007-02-03 10:19:18 -0800 (Sat, 03 Feb 2007) $
 */
public class TSSTransportAddressConfig implements Serializable {
    private short port;
    private String hostname;

    public TSSTransportAddressConfig() {}

    public TSSTransportAddressConfig(short port, String hostname) {
        this.port = port;
        this.hostname = hostname;
    }

    public short getPort() {
        return port;
    }

    public void setPort(short port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        toString("", buf);
        return buf.toString();
    }

    @Trivial
    void toString(String spaces, StringBuilder buf) {
        String moreSpaces = spaces + "  ";
        buf.append(spaces).append("TSSTransportAddressConfig: [\n");
        buf.append(moreSpaces).append("port    : ").append(port).append("\n");
        buf.append(moreSpaces).append("hostName: ").append(hostname).append("\n");
        buf.append(spaces).append("]\n");
    }

}
