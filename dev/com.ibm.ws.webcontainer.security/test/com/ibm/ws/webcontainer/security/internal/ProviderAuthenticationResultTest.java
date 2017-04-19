/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.security.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Hashtable;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.ibm.ws.webcontainer.security.AuthResult;
import com.ibm.ws.webcontainer.security.ProviderAuthenticationResult;

/**
 *
 */
public class ProviderAuthenticationResultTest {

    @Test
    public void testContruction1() {
        ProviderAuthenticationResult result = new ProviderAuthenticationResult(AuthResult.FAILURE, HttpServletResponse.SC_FORBIDDEN);
        assertEquals("status code should be failure", AuthResult.FAILURE, result.getStatus());
        assertEquals("http status code should be " + HttpServletResponse.SC_FORBIDDEN, HttpServletResponse.SC_FORBIDDEN, result.getHttpStatusCode());
        assertNull("user name should be null", result.getUserName());
        assertNull("subject should be null", result.getSubject());
        assertNull("custom properties should be null", result.getCustomProperties());
        assertNull("redirect Url should be null", result.getRedirectUrl());
    }

    @Test
    public void testContruction2() {
        String userName = "utle";
        Subject subject = new Subject();
        Hashtable<String, Object> customProperties = new Hashtable<String, Object>();
        String redirectUrl = "https://redirectUrl";

        ProviderAuthenticationResult result = new ProviderAuthenticationResult(AuthResult.SUCCESS, HttpServletResponse.SC_OK, userName, subject, customProperties, redirectUrl);
        assertEquals("status code should be success", AuthResult.SUCCESS, result.getStatus());
        assertEquals("http status code should be " + HttpServletResponse.SC_OK, HttpServletResponse.SC_OK, result.getHttpStatusCode());
        assertEquals("user name should be " + userName, userName, result.getUserName());
        assertNotNull("subject should be not null", result.getSubject());
        assertNotNull("custom properties should be not null", result.getCustomProperties());
        assertEquals("redirect Url should be " + redirectUrl, redirectUrl, result.getRedirectUrl());
    }
}
