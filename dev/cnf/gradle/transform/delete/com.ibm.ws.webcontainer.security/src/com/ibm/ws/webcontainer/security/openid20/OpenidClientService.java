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
package com.ibm.ws.webcontainer.security.openid20;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.webcontainer.security.ProviderAuthenticationResult;

/**
 *
 */
public interface OpenidClientService {
    public String getOpenIdIdentifier(HttpServletRequest req);

    public void createAuthRequest(HttpServletRequest req, HttpServletResponse res)
                    throws Exception;

    public String getRpRequestIdentifier(HttpServletRequest req, HttpServletResponse res);

    public ProviderAuthenticationResult verifyOpResponse(HttpServletRequest request, HttpServletResponse response)
                    throws Exception;

    public boolean isMapIdentityToRegistryUser();
}
