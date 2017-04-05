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
package com.ibm.ws.webcontainer.security;

import java.util.List;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.wsspi.security.token.SingleSignonToken;

/**
 * Single sign-on cookie helper class.
 */
public interface SSOCookieHelper {

    void addSSOCookiesToResponse(Subject subject, HttpServletRequest req, HttpServletResponse resp);

    void createLogoutCookies(HttpServletRequest req, HttpServletResponse resp);

    SingleSignonToken getDefaultSSOTokenFromSubject(final javax.security.auth.Subject subject);

    String getSSOCookiename();

    void removeSSOCookieFromResponse(HttpServletResponse resp);

    boolean allowToAddCookieToResponse(HttpServletRequest req);

    String getSSODomainName(HttpServletRequest req, List<String> ssoDomainList, boolean useURLDomain);

}