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
package com.ibm.ws.webcontainer.security.admin.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.ws.webcontainer.security.WebAppSecurityConfig;
import com.ibm.ws.webcontainer.security.openidconnect.OidcClient;
import com.ibm.ws.webcontainer.security.openidconnect.OidcServer;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

/**
 * Represents security configurable options for web admin applications.
 */
class WebAdminSecurityConfigImpl implements WebAppSecurityConfig {

    private final Boolean logoutOnHttpSessionExpire = false;
    private final Boolean singleSignonEnabled = true;
    private final Boolean preserveFullyQualifiedReferrerUrl = false;
    private final String postParamSaveMethod = "Cookie";
    private final Integer postParamCookieSize = 16384;
    private final Boolean allowLogoutPageRedirectToAnyHost = false;
    private final String wasReqURLRedirectDomainNames = null;
    private final String logoutPageRedirectDomainNames = null;
    private final String ssoCookieName = "LtpaToken2";
    // Admin supports CLIENT_CERT, but also needs to support basic auth
    private final Boolean allowFailOverToBasicAuth = true;
    private final Boolean displayAuthenticationRealm = false;
    private final Boolean httpOnlyCookies = true;
    private final Boolean webAlwaysLogin = false;
    private final Boolean ssoRequiresSSL = false;
    private final String ssoDomainNames = null;
    private final Boolean ssoUseDomainFromURL = false;
    private final Boolean useAuthenticationDataForUnprotectedResource = true;
    private final Boolean allowFailOverToFormLogin = true;
    private final Boolean includePathInWASReqURL = false;
    private final Boolean trackLoggedOutSSOCookies = false;
    private final Boolean useOnlyCustomCookieName = false;

    WebAdminSecurityConfigImpl(Map<String, Object> newProperties) {
        //nothing to do, values are hard-coded
    }

    /** {@inheritDoc} */
    @Override
    public boolean getLogoutOnHttpSessionExpire() {
        return logoutOnHttpSessionExpire;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isIncludePathInWASReqURL() {
        return includePathInWASReqURL;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSingleSignonEnabled() {
        return singleSignonEnabled.booleanValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean getPreserveFullyQualifiedReferrerUrl() {
        return preserveFullyQualifiedReferrerUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String getPostParamSaveMethod() {
        return postParamSaveMethod;
    }

    /** {@inheritDoc} */
    @Override
    public int getPostParamCookieSize() {
        return postParamCookieSize.intValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean getAllowLogoutPageRedirectToAnyHost() {
        return allowLogoutPageRedirectToAnyHost;
    }

    /** {@inheritDoc} */
    @Override
    public String getSSOCookieName() {
        return ssoCookieName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getAllowFailOverToBasicAuth() {
        return allowFailOverToBasicAuth;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getDisplayAuthenticationRealm() {
        return displayAuthenticationRealm;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getWASReqURLRedirectDomainNames() {
        return domainNamesToList(wasReqURLRedirectDomainNames);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getLogoutPageRedirectDomainList() {
        return domainNamesToList(logoutPageRedirectDomainNames);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getHttpOnlyCookies() {
        return httpOnlyCookies;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getWebAlwaysLogin() {
        return webAlwaysLogin;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getSSORequiresSSL() {
        return ssoRequiresSSL;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getSSODomainList() {
        return domainNamesToList(ssoDomainNames);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getSSOUseDomainFromURL() {
        return ssoUseDomainFromURL;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUseAuthenticationDataForUnprotectedResourceEnabled() {
        return useAuthenticationDataForUnprotectedResource;
    }

    /** {@inheritDoc} */
    private List<String> domainNamesToList(String domainNames) {
        if (domainNames == null || domainNames.length() == 0)
            return null;
        List<String> domainNameList = new ArrayList<String>();
        String[] sd = domainNames.split("\\|");
        for (int i = 0; i < sd.length; i++) {
            domainNameList.add(sd[i]);
        }
        return domainNameList;
    }

    /**
     * {@inheritDoc}<p>
     * This does not need an implemented as the Admin Application security
     * configuration properties never change.
     * 
     * @return {@code null}
     */
    @Override
    public String getChangedProperties(WebAppSecurityConfig original) {
        return null;
    }

    /**
     * {@inheritDoc} Admin Applications do not have a default Form Login URL.
     * 
     * @return {@code null}
     */
    @Override
    public String getLoginFormURL() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getAllowFailOverToFormLogin() {
        return allowFailOverToFormLogin;
    }

    /** {@inheritDoc} */
    @Override
    public boolean allowFailOver() {
        return allowFailOverToBasicAuth || allowFailOverToFormLogin;
    }

    /** {@inheritDoc} */
    @Override
    public void setSsoCookieName(AtomicServiceReference<OidcServer> oidcServerRef, AtomicServiceReference<OidcClient> oidcClientRef) {
        //do nothing
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTrackLoggedOutSSOCookiesEnabled() {
        return trackLoggedOutSSOCookies;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUseOnlyCustomCookieName() {
        return useOnlyCustomCookieName;
    }

}
