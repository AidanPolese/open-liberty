/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.security;

import javax.security.auth.Subject;

import com.ibm.ws.security.context.SubjectManager;
import com.ibm.ws.webcontainer.security.internal.WebReply;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;

/**
 * Encapsulate jacc related methods which are consumed by WebAppSecurityCollaborator.
 */
public interface WebAppAuthorizationHelper {
    boolean isUserInRole(String role, IExtendedRequest req, Subject subject);

    boolean authorize(AuthenticationResult authResult, WebRequest webRequest, String uriName);

    boolean isSSLRequired(WebRequest webRequest, String uriName);

    WebReply checkPrecludedAccess(WebRequest webRequest, String uriName);

}
