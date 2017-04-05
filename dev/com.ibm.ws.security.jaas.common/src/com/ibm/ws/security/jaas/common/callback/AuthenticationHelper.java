/*
 *
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.jaas.common.callback;

import java.security.cert.X509Certificate;

/**
*
*/
public final class AuthenticationHelper {

    /**
     * Create a copy of the specified byte array.
     * 
     * @param credToken
     * @return A copy of the specified byte array, or null if the input was null.
     */
    public static byte[] copyCredToken(byte[] credToken) {

        if (credToken == null) {
            return null;
        }

        final int LEN = credToken.length;
        if (LEN == 0) {
            return new byte[LEN];
        }

        byte[] newCredToken = new byte[LEN];
        System.arraycopy(credToken, 0, newCredToken, 0, LEN);

        return newCredToken;
    }

    /**
     * Create a copy of the specified cert array.
     * 
     * @param certChain
     * @return A copy of the specified cert array, or null if the input was null.
     */
    public static X509Certificate[] copyCertChain(X509Certificate[] certChain) {

        if (certChain == null) {
            return null;
        }

        final int LEN = certChain.length;
        if (LEN == 0) {
            return new X509Certificate[LEN];
        }

        X509Certificate[] newCertChain = new X509Certificate[LEN];
        System.arraycopy(certChain, 0, newCertChain, 0, LEN);

        return newCertChain;
    }
}
