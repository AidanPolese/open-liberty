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

/**
 * Encapsulate the web application security settings.
 */
public interface WebAppSecurityConfig {

    /**
     * These values are enumerated in the metatype.xml
     */
    static final String POST_PARAM_SAVE_TO_COOKIE = "Cookie";
    static final String POST_PARAM_SAVE_TO_SESSION = "Session";
    static final String POST_PARAM_SAVE_TO_NONE = "None";

    boolean getLogoutOnHttpSessionExpire();

    boolean isIncludePathInWASReqURL();

    boolean isSingleSignonEnabled();

    boolean getPreserveFullyQualifiedReferrerUrl();

    String getPostParamSaveMethod();

    int getPostParamCookieSize();

    boolean getAllowLogoutPageRedirectToAnyHost();

    String getSSOCookieName();

    /**
     * Is failover to BASIC from CLIENT_CERT allowed?
     *
     * @return {@code true} if BASIC failover is allowed
     */
    boolean getAllowFailOverToBasicAuth();

    boolean getDisplayAuthenticationRealm();

    List<String> getLogoutPageRedirectDomainList();

    boolean getHttpOnlyCookies();

    boolean getWebAlwaysLogin();

    boolean getSSORequiresSSL();

    List<String> getSSODomainList();

    List<String> getWASReqURLRedirectDomainNames();

    boolean getSSOUseDomainFromURL();

    boolean isUseAuthenticationDataForUnprotectedResourceEnabled();

    /**
     * Calculates the delta between this WebAppSecurityConfig and the provided
     * WebAppSecurityConfig. The values returned are the values from this Object.
     * If no properties were changed, an empty String should be returned.
     *
     * @param webAppSecConfig WebAppSecurityConfig object to compare settings against
     * @return String in the format of "name=value, name=value, ..." encapsulating the
     *         properties that are different between this WebAppSecurityConfig and the specified one
     */
    String getChangedProperties(WebAppSecurityConfig original);

    String getLoginFormURL();

    /**
     * Is failover to FORM from CLIENT_CERT allowed?
     *
     * @return {@code true} if FORM failover is allowed
     */
    boolean getAllowFailOverToFormLogin();

    /**
     * Is any failover from CLIENT_CERT allowed?
     *
     * @return {@code true} if any failover is allowed
     */
    boolean allowFailOver();

    /**
     * Is tracking of logged out LTPA tokens enabled.
     */
    boolean isTrackLoggedOutSSOCookiesEnabled();

    /**
     * Is use only custom cookie name enabled.
     */
    boolean isUseOnlyCustomCookieName();

}