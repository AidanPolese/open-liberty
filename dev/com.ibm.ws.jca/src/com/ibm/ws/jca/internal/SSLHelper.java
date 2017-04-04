/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.internal;

import javax.net.ssl.SSLSocketFactory;

/**
 * Interface for creating a SSLSocketFactory from SSLConfiguration.
 */
public interface SSLHelper {
    /**
     * Get an SSLSocketFactory for the SSLConfiguration with the specified id.
     * 
     * @param sslConfigID id of a sslConfiguration element.
     * @return SSLSocketFactory for the SSLConfiguration with the specified id.
     * @throws Exception if an error occurs.
     */
    SSLSocketFactory getSSLSocketFactory(String sslConfigID) throws Exception;
}
