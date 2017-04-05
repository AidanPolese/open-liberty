/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authentication;

import java.security.cert.X509Certificate;

import javax.security.auth.login.LoginException;

/**
 * This class:
 * - authenticates a certificate chain
 * - if successfully authenticated returns the components that will comprise the
 * Subject's accessid: type, realm, username
 * (an accessid has the general format: type:realm/username)
 */
public interface CertificateAuthenticator {
    /**
     * 
     * @param certChain
     * @return true - certificate authenticated
     *         false - certificate not authenticated, continue authenticating
     * @throws LoginException - authentication failed, fail the request
     */
    boolean authenticateCertificateChain(X509Certificate certChain[]) throws LoginException;

    /**
     * Return the credential type: AccessidUtil.TYPE_USER, AccessidUtil.TYPE_GROUP
     * 
     * @return credential type
     */
    public String getType();

    /**
     * Return the realm name
     * 
     * @return realm name
     */
    public String getRealm();

    /**
     * Return the username
     * 
     * @return username
     */
    public String getUsername();

}
