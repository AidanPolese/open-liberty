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
package com.ibm.ws.webcontainer.security.oauth20;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.webcontainer.security.ProviderAuthenticationResult;
import com.ibm.ws.webcontainer.security.openidconnect.OidcServerConfig;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceMap;

/**
 *
 */
public interface OAuth20Service {
    /**
     * Perform OAuth authentication for the given web request. Return an
     * OAuthAuthenticationResult which contains the status and subject
     * 
     * @param HttpServletRequest
     * @param HttpServletResponse
     * @return OAuthAuthenticationResult
     */
    ProviderAuthenticationResult authenticate(HttpServletRequest req,
                                              HttpServletResponse res);

    ProviderAuthenticationResult authenticate(HttpServletRequest req,
                                              HttpServletResponse res,
                                              ConcurrentServiceReferenceMap<String, OidcServerConfig> oidcServerConfigRef);

    /**
     * @param req
     * @param protectedOrAll : true - check the protected URI only
     *            : false - check all the Oauth specific URI, no matter protected or not
     * @return
     */
    boolean isOauthSpecificURI(HttpServletRequest req, boolean protectedOrAll);
}
