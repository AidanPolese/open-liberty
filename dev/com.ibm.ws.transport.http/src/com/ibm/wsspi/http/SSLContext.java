/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.wsspi.http;

import javax.net.ssl.SSLSession;

/**
 * SSL information available for an HTTP connection.
 */
public interface SSLContext {

    /**
     * get the list of enabled cipher suites
     * 
     * @return String[]
     */
    String[] getEnabledCipherSuites();

    /**
     * get a list of the enabled protocols.
     * 
     * @return String[]
     */
    String[] getEnabledProtocols();

    /**
     * configured to require client authentication
     * 
     * @return boolean
     */
    boolean getNeedClientAuth();

    /**
     * get the SSLSession that is associated with this session.
     * 
     * @return javax.net.ssl.SSLSession
     */
    SSLSession getSession();

    /**
     * returns true if the socket requires client mode in its first handshake.
     * 
     * @return boolean
     */
    boolean getUseClientMode();

    /**
     * whether the socket would like the client to authenticate
     * 
     * @return boolean
     */
    boolean getWantClientAuth();

}
