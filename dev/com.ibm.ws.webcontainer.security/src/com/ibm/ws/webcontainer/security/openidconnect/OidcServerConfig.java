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

import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 */
public interface OidcServerConfig {
    String getProviderId();

    String getOauthProviderName();

    String getOauthProviderPid();

    String getUserIdentifier();

    String getUniqueUserIdentifier();

    String getIssuerIdentifier();

    String getAudience();

    String getUserIdentity();

    String getGroupIdentifier();

    boolean isCustomClaimsEnabled();

    boolean allowDefaultSsoCookieName();

    /**
     * @return the customClaims which does not include the default "realmName uniqueSecurityName groupIds"
     */
    Set<String> getCustomClaims();

    boolean isJTIClaimEnabled();

    String getDefaultScope();

    String getExternalClaimNames();

    Properties getScopeToClaimMap();

    Properties getClaimToUserRegistryMap();

    String getSignatureAlgorithm();

    PrivateKey getPrivateKey() throws KeyStoreException, CertificateException;

    boolean isSessionManaged();

    long getIdTokenLifetime();

    String getCheckSessionIframeEndpointUrl();

    // OIDC Discovery Configuration Metadata
    String[] getResponseTypesSupported();

    String[] getSubjectTypesSupported();

    String getIdTokenSigningAlgValuesSupported();

    String[] getScopesSupported();

    String[] getClaimsSupported();

    String[] getResponseModesSupported();

    String[] getGrantTypesSupported();

    String[] getTokenEndpointAuthMethodsSupported();

    String[] getDisplayValuesSupported();

    String[] getClaimTypesSupported();

    boolean isClaimsParameterSupported();

    boolean isRequestParameterSupported();

    boolean isRequestUriParameterSupported();

    boolean isRequireRequestUriRegistration();

    String getBackingIdpUriPrefix();

    String getAuthProxyEndpointUrl();

    String getTrustStoreRef();

    PublicKey getPublicKey(String trustAliasName) throws KeyStoreException, CertificateException;

    Pattern getProtectedEndpointsPattern();

    // End of OIDC Discovery Configuration Metadata

    Pattern getEndpointsPattern();

    /**
     * @return
     */
    Pattern getNonEndpointsPattern();

    boolean isJwkEnabled();

    String getJwkJsonString();

    JSONWebKey getJSONWebKey();

    long getJwkRotationTime();

    int getJwkSigningKeySize();
}
