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
package com.ibm.ws.security.authentication.internal.collective;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.ibm.ws.security.authentication.AuthenticationException;

/**
 *
 */
public class NullCollectiveAuthenticationPluginTest {
    private final NullCollectiveAuthenticationPlugin nullPlugin = new NullCollectiveAuthenticationPlugin();

    /**
     * Test method for
     * {@link com.ibm.ws.security.authentication.internal.collective.NullCollectiveAuthenticationPlugin#isCollectiveCertificateChain(java.security.cert.X509Certificate[])}.
     */
    @Test
    public void isCollectiveCertificateChain() {
        assertFalse("Null plugin should always return false",
                    nullPlugin.isCollectiveCertificateChain(null));
    }

    /**
     * Test method for
     * {@link com.ibm.ws.security.authentication.internal.collective.NullCollectiveAuthenticationPlugin#authenticateCertificateChain(java.security.cert.X509Certificate[], boolean)}
     * .
     */
    @Test(expected = AuthenticationException.class)
    public void authenticateCertificateChain() throws Exception {
        nullPlugin.authenticateCertificateChain(null, true);
    }

}
