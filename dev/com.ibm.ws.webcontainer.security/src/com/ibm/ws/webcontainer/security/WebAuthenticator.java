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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WebAuthenticator is used for performing authentication for web users.
 * Userid/password, ltpa token, single sign-on token or certificates are the
 * types of authentication data that can be used to perform the task.
 * Authenticators are used by the WebCollaborators when performing security
 * checks.
 */
public interface WebAuthenticator {

    /**
     * Authenticate the web request.
     * 
     * @param webRequest
     * @return If successful, AuthenticationResult.getStatus() must answer SUCCESS,
     *         all other values indicate failure
     */
    AuthenticationResult authenticate(WebRequest webRequest);

    /**
     * Authenticate the web request.
     * 
     * @param request
     * @param response
     * @param props
     * @return If successful, AuthenticationResult.getStatus() must answer SUCCESS,
     *         all other values indicate failure
     * @throws Exception
     */
    AuthenticationResult authenticate(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HashMap<String, Object> props) throws Exception;
}
