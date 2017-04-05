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
package com.ibm.ws.jaxrs20.api;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.Cookie;

/**
 *
 */
public interface JaxRsAppSecurityService {

    public Cookie getSSOCookieFromSSOToken();

    public SSLSocketFactory getSSLSocketFactory(String sslRef, Map<String, Object> props);
}
