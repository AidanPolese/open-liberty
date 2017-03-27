/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.repository.base;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.junit.rules.ExternalResource;

/**
 * Rule to restore the original SSLContext and the HttpsURLConnection defaultSSLSocketFactory and defaultHostnameVerifier at the end of the test.
 * <p>
 * This allows any tests to change these values without interfering with other tests.
 */
public class RestoreTrustStoreRule extends ExternalResource {

    private SSLContext originalSslContext = null;
    private SSLSocketFactory originalSocketFactory = null;
    private HostnameVerifier originalHostnameVerifier = null;

    /** {@inheritDoc} */
    @Override
    protected void before() throws Throwable {
        originalSslContext = SSLContext.getDefault();
        originalSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        originalHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
    }

    /** {@inheritDoc} */
    @Override
    protected void after() {
        SSLContext.setDefault(originalSslContext);
        HttpsURLConnection.setDefaultSSLSocketFactory(originalSocketFactory);
        HttpsURLConnection.setDefaultHostnameVerifier(originalHostnameVerifier);
    }

}
