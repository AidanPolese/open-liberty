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
package com.ibm.ws.webcontainer.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.security.audit.AuditEvent;
import com.ibm.ws.security.SecurityService;
import com.ibm.ws.security.authentication.tai.TAIService;
import com.ibm.ws.webcontainer.security.internal.SSOAuthenticator;
import com.ibm.ws.webcontainer.security.internal.TAIAuthenticator;
import com.ibm.ws.webcontainer.security.metadata.SecurityMetadata;
import com.ibm.ws.webcontainer.security.oauth20.OAuth20Service;
import com.ibm.ws.webcontainer.security.openid20.OpenidClientService;
import com.ibm.ws.webcontainer.security.openidconnect.OidcClient;
import com.ibm.ws.webcontainer.security.openidconnect.OidcServer;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceMap;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;

/**
 * The responsibility of this WebProviderAuthenticatorProxy is to authenticate request with TAI, SSO, access token or
 * OpenID and OpenID Connect providers
 * 
 */
public class WebProviderAuthenticatorProxy implements WebAuthenticator {

    private static final TraceComponent tc = Tr.register(WebProviderAuthenticatorProxy.class);

    static final List<String> authenticatorOdering = Collections.unmodifiableList(Arrays.asList(new String[] { "com.ibm.ws.security.spnego", "com.ibm.ws.security.openid" }));

    AuthenticationResult OAUTH_CONT = new AuthenticationResult(AuthResult.CONTINUE, "OAuth service said continue...");
    AuthenticationResult OPENID_CLIENT_CONT = new AuthenticationResult(AuthResult.CONTINUE, "OpenID client service said continue...");
    AuthenticationResult OIDC_SERVER_CONT = new AuthenticationResult(AuthResult.CONTINUE, "OpenID Connect server said continue...");
    AuthenticationResult OIDC_CLIENT_CONT = new AuthenticationResult(AuthResult.CONTINUE, "OpenID Connect client said continue...");
    AuthenticationResult SPNEGO_CONT = new AuthenticationResult(AuthResult.CONTINUE, "SPNEGO said continue...");
    AuthenticationResult JASPI_CONT = new AuthenticationResult(AuthResult.CONTINUE, "JASPI said continue...");
    private final AtomicServiceReference<SecurityService> securityServiceRef;
    private final AtomicServiceReference<TAIService> taiServiceRef;
    private final ConcurrentServiceReferenceMap<String, TrustAssociationInterceptor> interceptorServiceRef;
    private volatile WebAppSecurityConfig webAppSecurityConfig;

    private final ConcurrentServiceReferenceMap<String, WebAuthenticator> webAuthenticatorRef;

    private final AtomicServiceReference<OAuth20Service> oauthServiceRef;
    private final AtomicServiceReference<OpenidClientService> openIdClientServiceRef;
    private final AtomicServiceReference<OidcServer> oidcServerRef;
    private final AtomicServiceReference<OidcClient> oidcClientRef;
    private WebProviderAuthenticatorHelper authHelper;
    private ReferrerURLCookieHandler referrerURLCookieHandler = null;

    public WebProviderAuthenticatorProxy(AtomicServiceReference<SecurityService> securityServiceRef,
                                         AtomicServiceReference<TAIService> taiServiceRef,
                                         ConcurrentServiceReferenceMap<String, TrustAssociationInterceptor> interceptorServiceRef,
                                         WebAppSecurityConfig webAppSecurityConfig,
                                         AtomicServiceReference<OAuth20Service> oauthServiceRef,
                                         AtomicServiceReference<OpenidClientService> openIdClientServiceRef,
                                         AtomicServiceReference<OidcServer> oidcServerRef,
                                         AtomicServiceReference<OidcClient> oidcClientRef,
                                         ConcurrentServiceReferenceMap<String, WebAuthenticator> webAuthenticatorRef) {

        this.securityServiceRef = securityServiceRef;
        this.taiServiceRef = taiServiceRef;
        this.interceptorServiceRef = interceptorServiceRef;
        this.webAppSecurityConfig = webAppSecurityConfig;
        this.oauthServiceRef = oauthServiceRef;
        this.oidcServerRef = oidcServerRef;
        this.openIdClientServiceRef = openIdClientServiceRef;
        this.oidcClientRef = oidcClientRef;
        this.webAuthenticatorRef = webAuthenticatorRef;
        authHelper = new WebProviderAuthenticatorHelper(securityServiceRef);
        referrerURLCookieHandler = new ReferrerURLCookieHandler(webAppSecurityConfig);
    }

    /*
     * need for unit test*
     */
    public void setWebProviderAuthenticatorHelper(WebProviderAuthenticatorHelper authHelper) {
        this.authHelper = authHelper;
    }

    /*
     * This method is the main method calling by the WebAuthenticatorProxy to handle TAI, SSO, and access token
     * 
     * TODO: Refactor to use config filters/ordering
     */
    @Override
    public AuthenticationResult authenticate(WebRequest webRequest) {
        HttpServletRequest request = webRequest.getHttpServletRequest();
        HttpServletResponse response = webRequest.getHttpServletResponse();
        AuthenticationResult authResult = handleTAI(webRequest, true);
        if (authResult.getStatus() == AuthResult.CONTINUE) {
            authResult = handleAccessToken(webRequest);
            if (authResult.getStatus() == AuthResult.CONTINUE) {
                webRequest.setCallAfterSSO(false);
                authResult = handleSpnego(webRequest);
                if (authResult.getStatus() == AuthResult.CONTINUE) {
                    authResult = handleOidcClient(request, response, true);
                    if (authResult.getStatus() == AuthResult.CONTINUE) {
                        authResult = handleSSO(webRequest, null);
                        if (authResult.getStatus() == AuthResult.CONTINUE) {
                            webRequest.setCallAfterSSO(true);
                            authResult = handleSpnego(webRequest);
                            if (authResult.getStatus() == AuthResult.CONTINUE) {
                                authResult = handleTAI(webRequest, false);
                                if (authResult.getStatus() == AuthResult.CONTINUE) {
                                    authResult = handleOidcClient(request, response, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        return authResult;
    }

    /**
     * @param webRequest
     * @return
     */
    AuthenticationResult handleJaspi(WebRequest webRequest, HashMap<String, Object> props) {
        AuthenticationResult authResult = JASPI_CONT;
        if (webAuthenticatorRef != null) {
            WebAuthenticator jaspiAuthenticator = webAuthenticatorRef.getService("com.ibm.ws.security.jaspi");
            if (jaspiAuthenticator != null) {
                if (props == null) { // Not processing form login creds
                    // first see if we have an ltpa token (from form login)
                    authResult = handleSSO(webRequest, null);
                    if (authResult.getStatus() == AuthResult.CONTINUE) { // no ltpatoken
                        // JASPI session requires the subject from the previous invocation
                        // to be passed in to the JASPI provider on subsequent calls
                        authResult = handleSSO(webRequest, "jaspicSession");
                        if (authResult.getStatus() == AuthResult.SUCCESS) {
                            Map<String, Object> requestProps = new HashMap<String, Object>();
                            requestProps.put("javax.servlet.http.registerSession.subject", authResult.getSubject());
                            webRequest.setProperties(requestProps);
                        }
                        authResult = jaspiAuthenticator.authenticate(webRequest);
                        if (authResult.getStatus() != AuthResult.CONTINUE) {
                            authResult.setAuditCredType(AuditEvent.CRED_TYPE_JASPIC);
                        }
                    }
                } else { // Processing form login creds
                    try {
                        authResult = jaspiAuthenticator.authenticate(webRequest.getHttpServletRequest(),
                                                                     webRequest.getHttpServletResponse(),
                                                                     props);
                        if (authResult.getStatus() != AuthResult.CONTINUE) {
                            authResult.setAuditCredType(AuditEvent.CRED_TYPE_JASPIC);
                        }
                    } catch (Exception e) {
                        if (tc.isDebugEnabled()) {
                            Tr.debug(tc, "Internal error handling JASPI request", e);
                        }
                    }
                }
                //
                // After a successful JASPI login set or clear the ltpa token cookie
                // and the JASPI session cookie.
                //
                if (authResult.getStatus() == AuthResult.SUCCESS) {
                    boolean registerSession = false;
                    Map<String, Object> reqProps = webRequest.getProperties();
                    if (reqProps != null) {
                        registerSession = Boolean.valueOf((String) reqProps.get("javax.servlet.http.registerSession")).booleanValue();
                    }
                    if (registerSession) {
                        SSOCookieHelper ssoCh = new SSOCookieHelperImpl(webAppSecurityConfig, "jaspicSession");
                        ssoCh.addSSOCookiesToResponse(authResult.getSubject(),
                                                      webRequest.getHttpServletRequest(),
                                                      webRequest.getHttpServletResponse());
                    }
                    SSOCookieHelper ssoCh = new SSOCookieHelperImpl(webAppSecurityConfig);
                    if (props != null &&
                        props.get("authType") != null &&
                        props.get("authType").equals("FORM_LOGIN")) {
                        //
                        // login form successfully processed, add ltpatoken for redirect
                        //
                        ssoCh.addSSOCookiesToResponse(authResult.getSubject(),
                                                      webRequest.getHttpServletRequest(),
                                                      webRequest.getHttpServletResponse());
                    } else { // not processing a login form
                        // We only want an ltpa token after form login. in all other cases remove it
                        // EXCEPT if the JASPI provider has committed the response
                        HttpServletResponse response = webRequest.getHttpServletResponse();
                        if (!response.isCommitted()) {
                            ssoCh.removeSSOCookieFromResponse(response);
                        }
                    }
                }
            }
        }
        return authResult;
    }

    /*
     * This method is called by the FormLoginExtensionProcessor
     */
    @Override
    public AuthenticationResult authenticate(HttpServletRequest request,
                                             HttpServletResponse response,
                                             HashMap<String, Object> props) throws Exception {
        WebRequest webRequest = new WebRequestImpl(request, response, null, null, null, null, null);
        AuthenticationResult authResult = handleJaspi(webRequest, props);
        if (authResult.getStatus() == AuthResult.CONTINUE) {
            authResult = handleOpenidClient(request, response);
        }
        return authResult;
    }

    /**
     * @param taiAuthenticator
     * @param webRequest
     * @param beforeSSO
     * @return
     */
    private AuthenticationResult handleTAI(WebRequest webRequest, boolean beforeSSO) {
        TAIAuthenticator taiAuthenticator = getTaiAuthenticator();
        AuthenticationResult authResult = null;
        if (taiAuthenticator == null) {
            authResult = new AuthenticationResult(AuthResult.CONTINUE, "TAI invoke " + (beforeSSO == true ? "before" : "after") + " SSO is not available, skipping TAI...");
        }
        else {
            authResult = taiAuthenticator.authenticate(webRequest, beforeSSO);
            if (authResult.getStatus() != AuthResult.CONTINUE) {
                authResult.setAuditCredType(AuditEvent.CRED_TYPE_TAI);
            }
        }
        return authResult;
    }

    private AuthenticationResult handleSSO(WebRequest webRequest, String ssoCookieName) {
        WebAuthenticator authenticator = getSSOAuthenticator(webRequest, ssoCookieName);
        AuthenticationResult authResult = authenticator.authenticate(webRequest);
        if (authResult == null || authResult.getStatus() != AuthResult.SUCCESS) {
            authResult = new AuthenticationResult(AuthResult.CONTINUE, "SSO did not succeed, so continue ...");
        }
        return authResult;
    }

    /**
     * @param webRequest
     * @param req
     * @param res
     * @return
     */
    private AuthenticationResult handleAccessToken(WebRequest webRequest) {
        HttpServletRequest req = webRequest.getHttpServletRequest();
        HttpServletResponse res = webRequest.getHttpServletResponse();

        AuthenticationResult authResult = handleOAuth(req, res);
        if (authResult.getStatus() != AuthResult.CONTINUE) {
            authResult.setAuditCredType(AuditEvent.CRED_TYPE_OAUTH_TOKEN);
        }
        return authResult;
    }

    private AuthenticationResult handleSpnego(WebRequest webRequest) {
        AuthenticationResult authResult = SPNEGO_CONT;
        if (webAuthenticatorRef != null) {
            WebAuthenticator webAuthenticator = webAuthenticatorRef.getService("com.ibm.ws.security.spnego");
            if (webAuthenticator != null) {
                authResult = webAuthenticator.authenticate(webRequest);
                if (authResult.getStatus() == AuthResult.SUCCESS) {
                    HttpServletRequest request = webRequest.getHttpServletRequest();
                    HttpServletResponse response = webRequest.getHttpServletResponse();
                    authResult = authHelper.loginWithHashtable(request, response, authResult.getSubject());
                    if (AuthResult.SUCCESS == authResult.getStatus()) {
                        SSOCookieHelper ssoCh = new SSOCookieHelperImpl(webAppSecurityConfig);
                        ssoCh.addSSOCookiesToResponse(authResult.getSubject(), request, response);
                    }
                }
            }
        }
        if (authResult.getStatus() != AuthResult.CONTINUE) {
            authResult.setAuditCredType(AuditEvent.CRED_TYPE_SPNEGO);
        }
        return authResult;
    }

    /*
     * The OpenID client redirects the request to OpenID provider for authentication
     */
    private AuthenticationResult handleOpenidClient(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AuthenticationResult authResult = OPENID_CLIENT_CONT;
        OpenidClientService openIdClientService = openIdClientServiceRef.getService();
        if (openIdClientService != null) {
            String opId = openIdClientService.getOpenIdIdentifier(request);
            if (opId != null && !opId.isEmpty()) {
                openIdClientService.createAuthRequest(request, response);
                authResult = new AuthenticationResult(AuthResult.REDIRECT_TO_PROVIDER, "OpenID client creates auth request...");
            } else if (openIdClientService.getRpRequestIdentifier(request, response) != null) {
                ProviderAuthenticationResult result = openIdClientService.verifyOpResponse(request, response);
                if (result.getStatus() != AuthResult.SUCCESS) {
                    return new AuthenticationResult(AuthResult.FAILURE, "OpenID client failed with status code " + result.getStatus());
                }

                authResult = authHelper.loginWithUserName(request, response, result.getUserName(), result.getSubject(), result.getCustomProperties(),
                                                          openIdClientService.isMapIdentityToRegistryUser());
            }
        }
        if (authResult.getStatus() != AuthResult.CONTINUE) {
            authResult.setAuditCredType(AuditEvent.CRED_TYPE_IDTOKEN);
        }
        return authResult;
    }

    /**
     * The OpenID Connect client redirects a request to the OpenID Connect provider for authentication.
     * 
     * @param req
     * @param res
     * @return
     */
    private AuthenticationResult handleOidcClient(HttpServletRequest req, HttpServletResponse res, boolean firstCall) {
        AuthenticationResult authResult = OIDC_CLIENT_CONT;
        OidcClient oidcClient = oidcClientRef.getService();
        if (oidcClient == null) {
            return new AuthenticationResult(AuthResult.CONTINUE, "OpenID Connect client is not available, skipping OpenID Connect client...");
        }

        if (firstCall) {
            // let's check if any oidcClient need to be called beforeSso. If not, return
            if (!oidcClient.anyClientIsBeforeSso()) {
                return authResult;
            }
        }

        String provider = oidcClient.getOidcProvider(req);
        if (provider == null) {
            return new AuthenticationResult(AuthResult.CONTINUE, "not an OpenID Connect client request, skipping OpenID Connect client...");
        }
        ProviderAuthenticationResult oidcResult = oidcClient.authenticate(req, res, provider, referrerURLCookieHandler, firstCall);

        if (oidcResult.getStatus() == AuthResult.CONTINUE) {
            return OIDC_CLIENT_CONT;
        }

        if (oidcResult.getStatus() == AuthResult.REDIRECT_TO_PROVIDER) {
            return new AuthenticationResult(AuthResult.REDIRECT, oidcResult.getRedirectUrl());
        }

        if (oidcResult.getStatus() == AuthResult.FAILURE) {
            if (HttpServletResponse.SC_UNAUTHORIZED == oidcResult.getHttpStatusCode()) {
                // return new AuthenticationResult(AuthResult.SEND_401, "OpenID Connect client failed the request...");
                return new AuthenticationResult(AuthResult.OAUTH_CHALLENGE, "OpenID Connect client failed the request...");
            } else {
                return new AuthenticationResult(AuthResult.FAILURE, "OpenID Connect client failed the request...");
            }
        }

        if (oidcResult.getStatus() != AuthResult.SUCCESS) {
            if (HttpServletResponse.SC_UNAUTHORIZED == oidcResult.getHttpStatusCode()) {
                // return new AuthenticationResult(AuthResult.SEND_401, "OpenID Connect client returned with status: " + oidcResult.getStatus());
                return new AuthenticationResult(AuthResult.OAUTH_CHALLENGE, "OpenID Connect client returned with status: " + oidcResult.getStatus());
            } else {
                return new AuthenticationResult(AuthResult.FAILURE, "OpenID Connect client returned with status: " + oidcResult.getStatus());
            }
        }

        if (oidcResult.getStatus() == AuthResult.SUCCESS && oidcResult.getUserName() != null) {
            authResult = authHelper.loginWithUserName(req, res, oidcResult.getUserName(), oidcResult.getSubject(),
                                                      oidcResult.getCustomProperties(), oidcClient.isMapIdentityToRegistryUser(provider));
            if (AuthResult.SUCCESS == authResult.getStatus()) {
                // If firstCall is true then disableLtpaCookie is true
                boolean bDisableLtpaCookie = firstCall; // let's make it clear
                boolean bPropagationTokenAuthenticated = isNotNullAndTrue(req, OidcClient.PROPAGATION_TOKEN_AUTHENTICATED);
                boolean bAuthnSessionDisabled = (Boolean) req.getAttribute(OidcClient.AUTHN_SESSION_DISABLED); // this will not be null
                String inboundValue = (String) req.getAttribute(OidcClient.INBOUND_PROPAGATION_VALUE); // this will not be null
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "Booleans: fisrtCall:" + firstCall +
                                 " tokenAuthenticated:" + bPropagationTokenAuthenticated +
                                 " SessionDisabled:" + bAuthnSessionDisabled +
                                 " inboundValue:" + inboundValue);
                }
                // extra handling when authenticated by the inbound propagation token (task 210993)
                if ((OidcClient.inboundNone.equals(inboundValue) && !bDisableLtpaCookie) ||
                    (OidcClient.inboundRequired.equals(inboundValue) && !bAuthnSessionDisabled) ||
                    (OidcClient.inboundSupported.equals(inboundValue) && (!bPropagationTokenAuthenticated) && !bDisableLtpaCookie)) {
                    SSOCookieHelper ssoCh = new SSOCookieHelperImpl(webAppSecurityConfig);
                    ssoCh.addSSOCookiesToResponse(authResult.getSubject(), req, res);
                }
            }
        }

        return authResult;
    }

    /**
     * @param req
     * @param propagationTokenAuthenticated
     * @return
     */
    boolean isNotNullAndTrue(HttpServletRequest req, String key) {
        Boolean result = (Boolean) req.getAttribute(key);
        if (result != null) {
            return result.booleanValue();
        }
        return false;
    }

    /**
     * The oauth service will call the provider to authenticate a user with the access token
     * 
     * @param webRequest
     * @return
     */
    private AuthenticationResult handleOAuth(HttpServletRequest req, HttpServletResponse res) {
        AuthenticationResult authResult = OAUTH_CONT;
        if (oauthServiceRef != null) {
            OAuth20Service oauthService = oauthServiceRef.getService();
            if (oauthService == null) {
                return new AuthenticationResult(AuthResult.CONTINUE, "OAuth service is not available, skipping OAuth...");
            }

            ProviderAuthenticationResult oauthResult = oauthService.authenticate(req, res);
            if (oauthResult.getStatus() == AuthResult.CONTINUE) {
                return OAUTH_CONT;
            }

            if (oauthResult.getStatus() == AuthResult.FAILURE) {
                if (HttpServletResponse.SC_UNAUTHORIZED == oauthResult.getHttpStatusCode()) {
                    return new AuthenticationResult(AuthResult.OAUTH_CHALLENGE, "OAuth service failed the request");
                }
                return new AuthenticationResult(AuthResult.FAILURE, "OAuth service failed the request...");
            }
            if (oauthResult.getStatus() != AuthResult.SUCCESS) {
                if (HttpServletResponse.SC_UNAUTHORIZED == oauthResult.getHttpStatusCode()) {
                    return new AuthenticationResult(AuthResult.OAUTH_CHALLENGE, "OAuth service failed the request due to unsuccessful request");
                }
                return new AuthenticationResult(AuthResult.FAILURE, "OAuth service returned with status: " + oauthResult.getStatus());
            }
            if (oauthResult.getUserName() != null) {
                authResult = authHelper.loginWithUserName(req, res, oauthResult.getUserName(), oauthResult.getSubject(), oauthResult.getCustomProperties(), true);
            }
        }

        return authResult;
    }

    /**
     * @return
     */
    private TAIAuthenticator getTaiAuthenticator() {
        TAIAuthenticator taiAuthenticator = null;
        TAIService taiService = taiServiceRef.getService();
        Iterator<TrustAssociationInterceptor> interceptorServices = interceptorServiceRef.getServices();
        if (taiService != null || (interceptorServices != null && interceptorServices.hasNext())) {
            SecurityService securityService = securityServiceRef.getService();
            taiAuthenticator = new TAIAuthenticator(taiService, interceptorServiceRef, securityService.getAuthenticationService(), new SSOCookieHelperImpl(webAppSecurityConfig, oidcServerRef));
        }

        return taiAuthenticator;
    }

    /**
     * Create an instance of SSOAuthenticator.
     * 
     * @param webRequest
     * @return The SSOAuthenticator, or {@code null} if it could not be created.
     */
    public WebAuthenticator getSSOAuthenticator(WebRequest webRequest, String ssoCookieName) {
        SecurityMetadata securityMetadata = webRequest.getSecurityMetadata();
        SecurityService securityService = securityServiceRef.getService();
        SSOCookieHelper cookieHelper;
        if (ssoCookieName != null) {
            cookieHelper = new SSOCookieHelperImpl(webAppSecurityConfig, ssoCookieName);
        } else {
            cookieHelper = new SSOCookieHelperImpl(webAppSecurityConfig, oidcServerRef);
        }
        return new SSOAuthenticator(securityService.getAuthenticationService(), securityMetadata, webAppSecurityConfig, cookieHelper);
    }

    /**
     * @return
     */
    public ConcurrentServiceReferenceMap<String, WebAuthenticator> getWebAuthenticatorRefs() {
        return webAuthenticatorRef;
    }
}
