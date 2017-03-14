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
package com.ibm.ws.http.dispatcher.internal.channel;

import javax.net.ssl.SSLSession;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.http.SSLContext;
import com.ibm.wsspi.tcpchannel.SSLConnectionContext;

/**
 * Implementation of the publicly accessible SSL information for a secure
 * HTTP connection.
 */
@Trivial
public class SSLContextImpl implements SSLContext {
    private SSLConnectionContext context = null;

    /**
     * Constructor wrapping a given SSL context object.
     * 
     * @param ssl
     */
    public SSLContextImpl(SSLConnectionContext ssl) {
        this.context = ssl;
    }

    /*
     * @see com.ibm.websphere.http.SSLContext#getEnabledCipherSuites()
     */
    @Override
    public String[] getEnabledCipherSuites() {
        return this.context.getEnabledCipherSuites();
    }

    /*
     * @see com.ibm.websphere.http.SSLContext#getEnabledProtocols()
     */
    @Override
    public String[] getEnabledProtocols() {
        return this.context.getEnabledProtocols();
    }

    /*
     * @see com.ibm.websphere.http.SSLContext#getNeedClientAuth()
     */
    @Override
    public boolean getNeedClientAuth() {
        return this.context.getNeedClientAuth();
    }

    /*
     * @see com.ibm.websphere.http.SSLContext#getSession()
     */
    @Override
    public SSLSession getSession() {
        return this.context.getSession();
    }

    /*
     * @see com.ibm.websphere.http.SSLContext#getUseClientMode()
     */
    @Override
    public boolean getUseClientMode() {
        return this.context.getUseClientMode();
    }

    /*
     * @see com.ibm.websphere.http.SSLContext#getWantClientAuth()
     */
    @Override
    public boolean getWantClientAuth() {
        return this.context.getWantClientAuth();
    }

}
