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

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.ibm.ws.webcontainer.security.internal.WebSecurityHelperImpl;

/**
 *
 */
public class WebSecurityHelperTest {

    /**
     * Test method for {@link com.ibm.websphere.security.web.WebSecurityHelper#getSSOCookieFromSSOToken()}.
     */
    @Test
    public void getSSOCookieFromSSOToken_noConfigSet() throws Exception {
        assertNull("When no WebAppSecurityConfiguration is set, the cookie should be null",
                   WebSecurityHelper.getSSOCookieFromSSOToken());
    }

    /**
     * Test method for {@link com.ibm.websphere.security.web.WebSecurityHelper#getSSOCookieName()}.
     */
    @Test
    public void getSSOCookieName_noConfigSet() throws Exception {
        WebSecurityHelperImpl.setWebAppSecurityConfig(null);
        assertNull("When no WebAppSecurityConfiguration is set, the cookie name should be null",
                   WebSecurityHelper.getSSOCookieName());
    }

}
