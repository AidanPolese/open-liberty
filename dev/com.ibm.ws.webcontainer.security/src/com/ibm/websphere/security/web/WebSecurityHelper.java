/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.security.web;

import javax.servlet.http.Cookie;

import com.ibm.ws.webcontainer.security.WebAppSecurityConfig;
import com.ibm.ws.webcontainer.security.internal.WebSecurityHelperImpl;

/**
 * Provides methods to perform security functions for web applications.
 * 
 * @author International Business Machines Corp.
 * @version WAS 8.5
 * @since WAS 8.0
 * @ibm-api
 */
public class WebSecurityHelper {

    /**
     * Extracts the SSO token from the subject of current thread
     * and builds an SSO cookie out of it for use on downstream web invocations.
     * The caller must check for null return value.
     * <p>
     * When the returned value is not null use Cookie methods getName() and getValue()
     * to set the Cookie header on an HTTP request with header value of
     * Cookie.getName()=Cookie.getValue()
     * 
     * @return an object of type javax.servlet.http.Cookie. May return {@code null}.
     */
    public static Cookie getSSOCookieFromSSOToken() throws Exception {
        return WebSecurityHelperImpl.getSSOCookieFromSSOToken();
    }

    /**
     * Extracts the SSO cookie name for use on downstream web invocations.
     * Return null when the service is not started or activated.
     * 
     * @return a String.
     */

    public static String getSSOCookieName() throws Exception {
        WebAppSecurityConfig config = WebSecurityHelperImpl.getWebAppSecurityConfig();
        if (config != null) {
            return config.getSSOCookieName();
        }
        return null;
    }
}
