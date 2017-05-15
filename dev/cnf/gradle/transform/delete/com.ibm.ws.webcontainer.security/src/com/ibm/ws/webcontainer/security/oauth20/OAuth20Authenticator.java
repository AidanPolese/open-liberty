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

package com.ibm.ws.webcontainer.security.oauth20;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.webcontainer.security.ProviderAuthenticationResult;
import com.ibm.ws.webcontainer.security.openidconnect.OidcServerConfig;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceMap;

/**
 * This class handles oauth connect authentication for incoming web requests.
 */
public interface OAuth20Authenticator {
    public ProviderAuthenticationResult authenticate(HttpServletRequest req,
                                                     HttpServletResponse res);

    public ProviderAuthenticationResult authenticate(HttpServletRequest req,
                                                     HttpServletResponse res,
                                                     ConcurrentServiceReferenceMap<String, OidcServerConfig> oidcServerConfigRef);
}
