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
package com.ibm.ws.jaxws.security;

import org.apache.cxf.transport.Conduit;

import com.ibm.websphere.ras.ProtectedString;

/**
 * Using the class to configure the security related stuff for web services
 */
public interface JaxWsSecurityConfigurationService {
    /**
     * Configure the Basic Authentication
     * 
     * @param conduit
     * @param userName
     * @param password
     */
    void configBasicAuth(Conduit conduit, String userName, ProtectedString password);

    /**
     * Configure the Client SSL configuration.
     * 
     * @param Conduit
     * @param sslRef
     * @param certAlias
     */
    void configClientSSL(Conduit Conduit, String sslRef, String certAlias);

}
