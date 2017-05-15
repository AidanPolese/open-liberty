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
package com.ibm.ws.webcontainer.security.feature.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.ws.webcontainer.security.WebAppSecurityCollaboratorImpl;
import com.ibm.ws.webcontainer.security.WebAppSecurityConfig;

/**
 * Represents security configurable options for web admin applications.
 */
class FeatureWebSecurityConfigImpl implements WebAppSecurityConfig {

    private final Boolean logoutOnHttpSessionExpire = false;
    private final Boolean singleSignonEnabled = true;
    private final Boolean preserveFullyQualifiedReferrerUrl = false;
    private final String postParamSaveMethod = "Cookie";
    private final Integer postParamCookieSize = 16384;
    private final Boolean allowLogoutPageRedirectToAnyHost = false;
    private final String wasReqURLRedirectDomainNames = null;
    private final String logoutPageRedirectDomainNames = null;
    private final String ssoCookieName = "LtpaToken2";
    private final Boolean allowFailOverToBasicAuth = false;
    private final Boolean displayAuthenticationRealm = false;
    private final Boolean httpOnlyCookies = true;
    private final Boolean webAlwaysLogin = false;
    private final Boolean ssoRequiresSSL = false;
    private final String ssoDomainNames = null;
    private final Boolean ssoUseDomainFromURL = false;
    private final Boolean useAuthenticationDataForUnprotectedResource = true;
    private final Boolean includePathInWASReqURL = false;
    private final Boolean trackLoggedOutSSOCookies = false;
    private final Boolean useOnlyCustomCookieName = false;

    FeatureWebSecurityConfigImpl(Map<String, Object> newProperties) {
        //nothing to do, values are hard-coded
    }

    /** {@inheritDoc} */
    @Override
    public boolean isIncludePathInWASReqURL() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return globalConfig.isIncludePathInWASReqURL();
        else
            return includePathInWASReqURL;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getLogoutOnHttpSessionExpire() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return globalConfig.getLogoutOnHttpSessionExpire();
        else
            return logoutOnHttpSessionExpire;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSingleSignonEnabled() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().isSingleSignonEnabled();
        else
            return singleSignonEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getPreserveFullyQualifiedReferrerUrl() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getPreserveFullyQualifiedReferrerUrl();
        else
            return preserveFullyQualifiedReferrerUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String getPostParamSaveMethod() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getPostParamSaveMethod();
        else
            return postParamSaveMethod;
    }

    /** {@inheritDoc} */
    @Override
    public int getPostParamCookieSize() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getPostParamCookieSize();
        else
            return postParamCookieSize;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getAllowLogoutPageRedirectToAnyHost() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getAllowLogoutPageRedirectToAnyHost();
        else
            return allowLogoutPageRedirectToAnyHost;
    }

    /** {@inheritDoc} */
    @Override
    public String getSSOCookieName() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getSSOCookieName();
        else
            return ssoCookieName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getAllowFailOverToBasicAuth() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getAllowFailOverToBasicAuth();
        else
            return allowFailOverToBasicAuth;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getDisplayAuthenticationRealm() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getDisplayAuthenticationRealm();
        else
            return displayAuthenticationRealm;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getWASReqURLRedirectDomainNames() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getWASReqURLRedirectDomainNames();
        else
            return domainNamesToList(wasReqURLRedirectDomainNames);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getLogoutPageRedirectDomainList() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getLogoutPageRedirectDomainList();
        else
            return domainNamesToList(logoutPageRedirectDomainNames);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getHttpOnlyCookies() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getHttpOnlyCookies();
        else
            return httpOnlyCookies;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getWebAlwaysLogin() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getWebAlwaysLogin();
        else
            return webAlwaysLogin;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getSSORequiresSSL() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getSSORequiresSSL();
        else
            return ssoRequiresSSL;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getSSODomainList() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getSSODomainList();
        else
            return domainNamesToList(ssoDomainNames);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getSSOUseDomainFromURL() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getSSOUseDomainFromURL();
        else
            return ssoUseDomainFromURL;
    }

    @Override
    public boolean isUseAuthenticationDataForUnprotectedResourceEnabled() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().isUseAuthenticationDataForUnprotectedResourceEnabled();
        else
            return useAuthenticationDataForUnprotectedResource;
    }

    /**
     * @return
     */
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
     * This does not need an implemented as these properties never change.
     */
    @Override
    public String getChangedProperties(WebAppSecurityConfig original) {
        return "";
    }

    /** {@inheritDoc} */
    @Override
    public String getLoginFormURL() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getLoginFormURL();
        else
            return null;

    }

    /** {@inheritDoc} */
    @Override
    public boolean getAllowFailOverToFormLogin() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().getAllowFailOverToFormLogin();
        else
            return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean allowFailOver() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().allowFailOver();
        else
            return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTrackLoggedOutSSOCookiesEnabled() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().isTrackLoggedOutSSOCookiesEnabled();
        else
            return trackLoggedOutSSOCookies;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUseOnlyCustomCookieName() {
        WebAppSecurityConfig globalConfig = WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig();
        if (globalConfig != null)
            return WebAppSecurityCollaboratorImpl.getGlobalWebAppSecurityConfig().isUseOnlyCustomCookieName();
        else
            return useOnlyCustomCookieName;
    }
}
