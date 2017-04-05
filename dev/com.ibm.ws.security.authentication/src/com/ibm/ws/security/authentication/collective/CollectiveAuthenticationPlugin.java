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
package com.ibm.ws.security.authentication.collective;

import java.security.cert.X509Certificate;

import com.ibm.ws.security.authentication.AuthenticationException;

/**
 * Plug-point for the collective to provide authentication checks to the
 * authentication flow.
 */
public interface CollectiveAuthenticationPlugin {

    /**
     * Determine if the X509Certificate chain represents a collective
     * certificate. A collective certificate should be handled by {@link #authenticateCertificateChain(X509Certificate[], boolean)}.
     * 
     * @param certChain
     * @return {@code true} if the certificate is a collective certificate, {@code false} otherwise.
     */
    boolean isCollectiveCertificateChain(X509Certificate[] certChain);

    /**
     * Determine if the X509Certificate chain represents a collective
     * certificate. A collective certificate should be handled by {@link #authenticateCertificateChain(X509Certificate[], boolean)}.
     * 
     * @param certChain
     * @return {@code true} if the certificate is a collective certificate, {@code false} otherwise.
     */
    boolean isCollectiveCACertificate(X509Certificate[] certChain);

    /**
     * Determine if the X509Certificate chain represents an authenticated
     * collective identity. The certificate chain must reflect a currently
     * registered collective member.
     * 
     * @param certChain
     * @param collectiveCert
     * @throws AuthenticationException if the certificate is not an authenticated collective certificate
     */
    void authenticateCertificateChain(X509Certificate certChain[], boolean collectiveCert) throws AuthenticationException;

}
