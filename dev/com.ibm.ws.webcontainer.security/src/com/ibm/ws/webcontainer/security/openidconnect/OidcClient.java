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
import com.ibm.ws.webcontainer.security.ReferrerURLCookieHandler;

/**
 *
 */
public interface OidcClient {

	// authenticated by the oidc propagation token, such as: access_token... etc
	public static final String PROPAGATION_TOKEN_AUTHENTICATED = "com.ibm.ws.webcontainer.security.openidconnect.propagation.token.authenticated";
	// In case it's authenticated by the oidc propagation token, do not create a cookie
	public static final String INBOUND_PROPAGATION_VALUE = "com.ibm.ws.webcontainer.security.openidconnect.inbound.propagation.value";
	public static final String AUTHN_SESSION_DISABLED = "com.ibm.ws.webcontainer.security.openidconnect.authn.session.disabled";

	public static final String inboundNone = "none";
	public static final String inboundRequired = "required";
	public static final String inboundSupported = "supported";

	ProviderAuthenticationResult authenticate(HttpServletRequest req,
			HttpServletResponse res,
			String provide,
			ReferrerURLCookieHandler referrerURLCookieHandler,
			boolean firstCall);

	/**
	 * Check whether the request is OpenID Connect client or not
	 * 
	 * @param HttpServletRequest
	 * @return String
	 */
	String getOidcProvider(HttpServletRequest req);

	boolean isMapIdentityToRegistryUser(String provider);

	boolean isValidRedirectUrl(HttpServletRequest req);

	/**
	 * @return
	 */
	boolean anyClientIsBeforeSso();
}
