/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.csiv2;

import java.security.cert.X509Certificate;

import javax.security.auth.Subject;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.security.authentication.AuthenticationException;

public interface Authenticator {

    /**
     * Authenticate with user name and password.
     * 
     * @param username
     * @param password
     * @return The authenticated subject.
     * @throws AuthenticationException
     */
    Subject authenticate(String username, @Sensitive String password) throws AuthenticationException;

    /**
     * Authenticate with certificate chain.
     * 
     * @param certificateChain
     * @return The authenticated subject.
     * @throws AuthenticationException
     */
    Subject authenticate(@Sensitive X509Certificate[] certificateChain) throws AuthenticationException;

    /**
     * Authenticate with asserted user.
     * 
     * @param assertedUser
     * @return The authenticated subject.
     * @throws AuthenticationException
     */
    Subject authenticate(String assertedUser) throws AuthenticationException;

    /**
     * Authenticate with token bytes.
     * 
     * @param tokenBytes
     * @return The authenticated subject.
     */
    Subject authenticate(@Sensitive byte[] tokenBytes) throws AuthenticationException;
}
