/*
 * 1.2 4/25/02
 *
 * IBM Confidential OCO Source Material
 * 5639-D57 (C) COPYRIGHT International Business Machines Corp. 2002
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.ws.http;

@Deprecated
public class Alias {
    private final String hostname;
    private final String port;

    @Deprecated
    public Alias(String host, String port) {
        hostname = host;
        this.port = port;
    }

    @Deprecated
    public String getHostname() {
        return hostname;
    }

    @Deprecated
    public String getPort() {
        return port;
    }

    //LI3816
    @Override
    public String toString() {
        return hostname + ":" + port;
    }
}
