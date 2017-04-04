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
/**
 * 04/26/10 F743-25523     leou      Initial version
 * 05/10/10 F743-25523.1   leou      Move Jaspi hooks to WebAuthenticator
 * 05/27/10 654357         leou      CTS6: jaspic failure - testName:  CheckValidateReqAuthException, do not call secureResponse during postInvoke 
 * 08/11/10 665302         leou      Authorization problem with cache key using JASPI authentication
 */
package com.ibm.ws.webcontainer.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UnprotectedResourceService {

    /**
     * Returns true if the unprotected Resource need to be authenticated further
     * by the service owner later
     */
    boolean isAuthenticationRequired(HttpServletRequest request);

    /**
     * Returns true if we take some actions.
     * Otherwise, return false
     * example of userName: "user:sp2_realm_No/user2"
     */
    boolean logout(HttpServletRequest request, HttpServletResponse response, String userName);
}
