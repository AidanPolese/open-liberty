/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.security.openidconnect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.webcontainer.security.ProviderAuthenticationResult;
import com.ibm.ws.webcontainer.security.oauth20.OAuth20Service;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/**
 *
 */
public interface OidcServer {
    /**
     * Perform OpenID Connect authentication for the given web request. Return an
     * OidcAuthenticationResult which contains the status and subject
     * 
     * @param HttpServletRequest
     * @param HttpServletResponse
     * @param AtomicServiceReference<OAuth20Service>
     * @return ProviderAuthenticationResult
     */
    ProviderAuthenticationResult authenticate(HttpServletRequest req,
                                              HttpServletResponse res,
                                              AtomicServiceReference<OAuth20Service> oauthServiceRef);

    /**
     * @param req
     * @param protectedOrAll :true -- check the protected URI only
     *            :false -- check all OIDC specific URI, no metter protected or not.
     * @return
     */
    boolean isOIDCSpecificURI(HttpServletRequest req, boolean protectedOrAll);

    boolean allowDefaultSsoCookieName();
}
