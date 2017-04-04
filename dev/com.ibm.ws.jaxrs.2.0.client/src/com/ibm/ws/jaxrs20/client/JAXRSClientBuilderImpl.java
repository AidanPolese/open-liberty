/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.client;

import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.cxf.jaxrs.client.spec.ClientBuilderImpl;
import org.apache.cxf.jaxrs.client.spec.TLSConfiguration;

/**
 *
 */
public class JAXRSClientBuilderImpl extends ClientBuilderImpl {

    private final TLSConfiguration secConfig = new TLSConfiguration();

    public JAXRSClientBuilderImpl() {
        super();
    }

    @Override
    public Client build() {

        return new JAXRSClientImpl(super.getConfiguration(), secConfig);
    }

    @Override
    public ClientBuilder hostnameVerifier(HostnameVerifier verifier) {
        secConfig.getTlsClientParams().setHostnameVerifier(verifier);
        return this;
    }

    @Override
    public ClientBuilder sslContext(SSLContext sslContext) {
        secConfig.getTlsClientParams().setKeyManagers(null);
        secConfig.getTlsClientParams().setTrustManagers(null);
        secConfig.setSslContext(sslContext);
        return this;
    }

    @Override
    public ClientBuilder keyStore(KeyStore store, char[] password) {
        secConfig.setSslContext(null);
        try {
            KeyManagerFactory tmf =
                            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            tmf.init(store, password);
            secConfig.getTlsClientParams().setKeyManagers(tmf.getKeyManagers());
        } catch (Exception ex) {
            throw new ProcessingException(ex);
        }
        return this;
    }

    @Override
    public ClientBuilder trustStore(KeyStore store) {
        secConfig.setSslContext(null);
        try {
            TrustManagerFactory tmf =
                            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(store);
            secConfig.getTlsClientParams().setTrustManagers(tmf.getTrustManagers());
        } catch (Exception ex) {
            throw new ProcessingException(ex);
        }

        return this;
    }
}
