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

import java.util.Hashtable;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.security.authentication.AuthenticationException;

public interface JaspiService {

    void postInvoke(WebSecurityContext webSecurityContext) throws AuthenticationException;

    Hashtable<String, Object> getCustomCredentials(Subject subject);

    Subject getUnauthenticatedSubject();

    interface JaspiAuthContext {
        Object getServerAuthContext();

        Object getMessageInfo();

        boolean runSecureResponse();

        void setRunSecureResponse(boolean isSet);
    }

    /**
     * Invoke the matching JASPI provider's cleanSubject method.
     * Throw an AuthenticationException if cleanSubject throws an
     * exception.
     * 
     * @param req
     * @param res
     * @param webAppSecConfig
     * @throws AuthenticationException
     */
    void logout(HttpServletRequest req,
                HttpServletResponse res,
                WebAppSecurityConfig webAppSecConfig) throws AuthenticationException;

    /**
     * Returns true if any providers are registered
     */
    boolean isAnyProviderRegistered();
}
