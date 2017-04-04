/*
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
package com.ibm.wsspi.ssl;

import java.util.Properties;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;

import com.ibm.websphere.ssl.JSSEHelper;
import com.ibm.websphere.ssl.JSSEProvider;

/**
 * Marker interface for configured SSL subsystem
 */
public interface SSLSupport {

    JSSEHelper getJSSEHelper();

    /**
     * Obtain the default JSSE provider instance.
     *
     * @return JSSEProvider
     */
    JSSEProvider getJSSEProvider();

    /**
     * Obtain the possible JSSE provider for the given name. This will return null
     * if there was no match found.
     *
     * @param providerName
     * @return JSSEProvider
     */
    JSSEProvider getJSSEProvider(String providerName);

    /**
     * Obtain a Liberty SSLSocketFactory.
     *
     * @return SSLSocketFactory
     */
    SSLSocketFactory getSSLSocketFactory();

    /**
     * Obtain a Liberty SSLSocketFactory for a given SSL configuration.
     *
     * @param sslAlias - name of a SSL configuration
     * @return SSLSocketFactory
     * @throws SSLException
     */
    SSLSocketFactory getSSLSocketFactory(String sslAlias) throws SSLException;

    /**
     * Obtain a Liberty SSLSocketFactory for a given set of SSL properties.
     *
     * @param sslProps - properties to create a SSL Socket factory
     * @return SSLSocketFactory
     * @throws SSLException
     */
    SSLSocketFactory getSSLSocketFactory(Properties sslProps) throws SSLException;

}
