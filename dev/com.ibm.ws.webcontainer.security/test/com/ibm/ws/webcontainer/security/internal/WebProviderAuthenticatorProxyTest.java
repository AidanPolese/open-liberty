package com.ibm.ws.webcontainer.security.internal;

import static org.junit.Assert.assertTrue;

import java.util.Hashtable;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

import com.ibm.ws.security.SecurityService;
import com.ibm.ws.security.authentication.AuthenticationService;
import com.ibm.ws.security.authentication.tai.TAIService;
import com.ibm.ws.security.registry.UserRegistry;
import com.ibm.ws.security.registry.UserRegistryService;
import com.ibm.ws.webcontainer.security.AuthResult;
import com.ibm.ws.webcontainer.security.AuthenticationResult;
import com.ibm.ws.webcontainer.security.ProviderAuthenticationResult;
import com.ibm.ws.webcontainer.security.ReferrerURLCookieHandler;
import com.ibm.ws.webcontainer.security.WebAppSecurityConfig;
import com.ibm.ws.webcontainer.security.WebProviderAuthenticatorHelper;
import com.ibm.ws.webcontainer.security.WebProviderAuthenticatorProxy;
import com.ibm.ws.webcontainer.security.WebRequest;
import com.ibm.ws.webcontainer.security.oauth20.OAuth20Service;
import com.ibm.ws.webcontainer.security.openid20.OpenidClientService;
import com.ibm.ws.webcontainer.security.openidconnect.OidcClient;
import com.ibm.ws.webcontainer.security.openidconnect.OidcServer;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceMap;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;

public class WebProviderAuthenticatorProxyTest {

    private static SharedOutputManager outputMgr = SharedOutputManager.getInstance();
    /**
     * Using the test rule will drive capture/restore and will dump on error..
     * Notice this is not a static variable, though it is being assigned a value we
     * allocated statically. -- the normal-variable-ness is for before/after processing
     */
    @Rule
    public TestRule managerRule = outputMgr;

    private final Mockery mock = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private final String ACCESS_TOKEN = "access_token";
    private final String AUTHORIZATION = "Authorization";
    private final String MY_OIDC_CLIENT = "openidConnectClient1";

    private final HttpServletRequest req = mock.mock(HttpServletRequest.class);
    private final HttpServletResponse resp = mock.mock(HttpServletResponse.class);

    private final WebAppSecurityConfig webAppSecurityConfig = mock.mock(WebAppSecurityConfig.class);

    @SuppressWarnings("unchecked")
    private final AtomicServiceReference<SecurityService> securityServiceRef = mock.mock(AtomicServiceReference.class, "securityServiceRef");
    private final SecurityService securityService = mock.mock(SecurityService.class, "securityService");

    @SuppressWarnings("unchecked")
    private final AtomicServiceReference<TAIService> taiServiceRef = mock.mock(AtomicServiceReference.class, "taiServiceRef");
    private final TAIService taiService = mock.mock(TAIService.class, "taiService");
    private final ConcurrentServiceReferenceMap<String, TrustAssociationInterceptor> interceptorServiceRef =
                    new ConcurrentServiceReferenceMap<String, TrustAssociationInterceptor>("interceptorServiceRef");

    private final UserRegistryService userRegistryService = mock.mock(UserRegistryService.class, "userRegistryService");
    private final UserRegistry userRegistry = mock.mock(UserRegistry.class, "userRegistry");

    @SuppressWarnings("unchecked")
    private final AtomicServiceReference<OpenidClientService> openIdClientServiceRef = mock.mock(AtomicServiceReference.class, "openIdClientServiceRef");
    private final OpenidClientService openidClientService = mock.mock(OpenidClientService.class, "openidClientService");

    @SuppressWarnings("unchecked")
    private final AtomicServiceReference<OAuth20Service> oauthServiceRef = mock.mock(AtomicServiceReference.class, "oauthServiceRef");
    private final OAuth20Service oauthService = mock.mock(OAuth20Service.class, "oauthService");

    @SuppressWarnings("unchecked")
    private final AtomicServiceReference<OidcServer> oidcServerRef = mock.mock(AtomicServiceReference.class, "OidcServerRef");

    @SuppressWarnings("unchecked")
    private final AtomicServiceReference<OidcClient> oidcClientRef = mock.mock(AtomicServiceReference.class, "OidcClientRef");
    private final OidcClient oidcClient = mock.mock(OidcClient.class, "oidcClient");

    private final WebRequest webRequest = mock.mock(WebRequest.class);
    private final WebProviderAuthenticatorHelper authHelper = mock.mock(WebProviderAuthenticatorHelper.class);
    private final AuthenticationService authService = mock.mock(AuthenticationService.class);

    final ProviderAuthenticationResult providerResult = mock.mock(ProviderAuthenticationResult.class, "providerResult");
    final ProviderAuthenticationResult oidcProviderResult = mock.mock(ProviderAuthenticationResult.class, "oidcProviderResult");
    final ProviderAuthenticationResult oauthProviderResult = mock.mock(ProviderAuthenticationResult.class, "oauthProviderResult");

    /**
     * common set of Expectations shared by all the
     * test methods
     * 
     */
    @Before
    public void setup() {
        mock.checking(new Expectations() {
            {
                allowing(securityServiceRef).getService();
                will(returnValue(securityService));
                allowing(securityService).getAuthenticationService();
                will(returnValue(authService));
                allowing(taiServiceRef).getService();
                will(returnValue(taiService));
                allowing(webRequest).setCallAfterSSO(true);
            }
        });
    }

    @After
    public void tearDown() {
        mock.assertIsSatisfied();
    }

    @Test
    public void testAuthenticate_webRequest_accessToken_no_oauthService() {
        final String methodName = "testAuthenticate_webRequest_accessToken_no_oauthService";

        try {
            mock.checking(new Expectations() {
                {
                    allowing(oauthServiceRef).getService();
                    will(returnValue(null));
                    allowing(oidcClientRef).getService();
                    will(returnValue(null));
                    allowing(webRequest).setCallAfterSSO(false);
                }
            });
            mockAccessTokenRequest();
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication result should be continue.", AuthResult.CONTINUE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOAuth_continue() {
        final String methodName = "testHandleOAuth_continue";
        try {
            mockOauthResult(AuthResult.CONTINUE);

            mockAccessTokenRequest();
            mock.checking(new Expectations() {
                {
                    allowing(oidcClientRef).getService();
                    will(returnValue(null));
                    allowing(webRequest).setCallAfterSSO(false);

                }
            });
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication result should be continue.", AuthResult.CONTINUE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOAuth_failure_unauthorized() {
        final String methodName = "testHandleOAuth_failure_unauthorized";
        try {
            mockOauthResult(AuthResult.FAILURE);
            mockAccessTokenRequest();
            mock.checking(new Expectations() {
                {
                    allowing(providerResult).getHttpStatusCode();
                    will(returnValue(401));
                }
            });
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication should have failed with a 401.", AuthResult.OAUTH_CHALLENGE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOAuth_failure_forbidden() {
        final String methodName = "testHandleOAuth_failure_forbidden";
        try {
            mockOauthResult(AuthResult.FAILURE);
            mockAccessTokenRequest();
            mock.checking(new Expectations() {
                {
                    allowing(providerResult).getHttpStatusCode();
                    will(returnValue(403));
                }
            });
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication should have failed.", AuthResult.FAILURE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOAuth_failure_unknown_unauthorized() {
        final String methodName = "testHandleOAuth_failure_unknown_unauthorized";
        try {
            mockOauthResult(AuthResult.UNKNOWN);
            mockAccessTokenRequest();
            mock.checking(new Expectations() {
                {
                    allowing(providerResult).getHttpStatusCode();
                    will(returnValue(401));
                }
            });
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication should have failed with a 401.", AuthResult.OAUTH_CHALLENGE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOAuth_failure_unknown_forbidden() {
        final String methodName = "testHandleOAuth_failure_unknown_forbidden";
        try {
            mockOauthResult(AuthResult.UNKNOWN);
            mockAccessTokenRequest();
            mock.checking(new Expectations() {
                {
                    allowing(providerResult).getHttpStatusCode();
                    will(returnValue(403));
                }
            });
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication should have failed.", AuthResult.FAILURE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOAuth_success() {
        final String methodName = "testHandleOAuth_success";
        try {
            mockOauthResult(AuthResult.SUCCESS);
            mockAccessTokenRequest();
            mockProviderAuthSuccessAndAuthHelper(null, true);
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            webProviderAuthenticatorProxy.setWebProviderAuthenticatorHelper(authHelper);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication result should be success.", AuthResult.SUCCESS == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOidcClient_continue_oidcClient_null() {
        final String methodName = "testHandleOidcClient_continue_oidcClient_null";
        try {
            mockOidcClientGetProvider(null);
            mock.checking(new Expectations() {
                {
                    allowing(openIdClientServiceRef).getService();
                    will(returnValue(null));
                }
            });
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(req, resp, null);
            assertTrue("Authentication result should be continue.", AuthResult.CONTINUE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOidcClient_redirect_to_provider() {
        final String methodName = "testHandleOidcClient_redirect_to_provider";
        final ProviderAuthenticationResult providerAuthResult = new ProviderAuthenticationResult(AuthResult.REDIRECT_TO_PROVIDER, HttpServletResponse.SC_OK);
        try {
            mock.checking(new Expectations() {
                {
                    allowing(webRequest).setCallAfterSSO(false);
                    allowing(oidcClient).anyClientIsBeforeSso();//
                    will(returnValue(true));//
                }
            });
            mockForOidcClientRequest();
            mockOidcClientGetProvider(MY_OIDC_CLIENT);
            mockOidcClientProviderResult(MY_OIDC_CLIENT, providerAuthResult);
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication result should be redirect.", AuthResult.REDIRECT == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOidcClient_failure() {
        final String methodName = "testHandleOidcClient_failure";
        final ProviderAuthenticationResult providerAuthResult = new ProviderAuthenticationResult(AuthResult.FAILURE, HttpServletResponse.SC_FORBIDDEN);
        try {
            mock.checking(new Expectations() {
                {
                    allowing(webRequest).setProviderSpecialUnprotectedURI(false);
                    allowing(webRequest).setCallAfterSSO(false);
                    allowing(oidcClient).anyClientIsBeforeSso();//
                    will(returnValue(true));//
                }
            });
            mockForOidcClientRequest();
            mockOidcClientGetProvider(MY_OIDC_CLIENT);
            mockOidcClientProviderResult(MY_OIDC_CLIENT, providerAuthResult);
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication result should be failure.", AuthResult.FAILURE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOidcClient_unknown_error() {
        final String methodName = "testHandleOidcClient_unknown_error";
        final ProviderAuthenticationResult providerAuthResult = new ProviderAuthenticationResult(AuthResult.UNKNOWN, HttpServletResponse.SC_FORBIDDEN);
        try {
            mock.checking(new Expectations() {
                {
                    allowing(webRequest).setProviderSpecialUnprotectedURI(false);
                    allowing(webRequest).setCallAfterSSO(false);
                    allowing(oidcClient).anyClientIsBeforeSso();//
                    will(returnValue(true));//
                }
            });
            mockForOidcClientRequest();
            mockOidcClientGetProvider(MY_OIDC_CLIENT);
            mockOidcClientProviderResult(MY_OIDC_CLIENT, providerAuthResult);
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication result should be failure.", AuthResult.FAILURE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOidcClient_success() {
        final String methodName = "testHandleOidcClient_success";
        final Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
        try {
            mockForOidcClientRequest();
            mockOidcClientGetProvider(MY_OIDC_CLIENT);
            mock.checking(new Expectations() {
                {
                    allowing(oidcClient).authenticate(with(req), with(resp), with(MY_OIDC_CLIENT), with(any(ReferrerURLCookieHandler.class)), with(any(Boolean.class)));
                    will(returnValue(providerResult));

                    allowing(oidcClient).isMapIdentityToRegistryUser(MY_OIDC_CLIENT);
                    will(returnValue(false));
                    allowing(webAppSecurityConfig).isSingleSignonEnabled();
                    will(returnValue(true));
                    allowing(req).isSecure();
                    will(returnValue(false));
                    allowing(webAppSecurityConfig).getSSORequiresSSL();
                    will(returnValue(false));
                    allowing(webRequest).setCallAfterSSO(false);
                    allowing(oidcClient).anyClientIsBeforeSso();//
                    will(returnValue(true));//
                    allowing(req).getAttribute(OidcClient.PROPAGATION_TOKEN_AUTHENTICATED);// 
                    will(returnValue(Boolean.FALSE)); //
                    allowing(req).getAttribute(OidcClient.AUTHN_SESSION_DISABLED);// 
                    will(returnValue(Boolean.TRUE)); //
                    allowing(req).getAttribute(OidcClient.INBOUND_PROPAGATION_VALUE);// 
                    will(returnValue("none")); //
                }
            });
            mockProviderAuthSuccessAndAuthHelper(hashtable, false);
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            webProviderAuthenticatorProxy.setWebProviderAuthenticatorHelper(authHelper);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(webRequest);
            assertTrue("Authentication result should be success.", AuthResult.SUCCESS == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOpenidClient_continue() {
        final String methodName = "testHandleOpenidClient_continue";
        try {

            mockOidcClientResult(AuthResult.CONTINUE);
            mock.checking(new Expectations() {
                {
                    allowing(openIdClientServiceRef).getService();
                    will(returnValue(null));
                }
            });
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(req, resp, null);
            assertTrue("Authentication result should be continue.", AuthResult.CONTINUE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOpenidClient_createAuthRequest() {
        final String methodName = "testHandleOpenidClient_createAuthRequest";
        try {
            mockOidcClientResult(AuthResult.CONTINUE);
            mock.checking(new Expectations() {
                {
                    allowing(openIdClientServiceRef).getService();
                    will(returnValue(openidClientService));
                    allowing(openidClientService).getOpenIdIdentifier(req);
                    will(returnValue("yahoo"));
                    allowing(openidClientService).createAuthRequest(req, resp);
                }
            });

            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(req, resp, null);
            assertTrue("Authentication result should be redirect to provider", AuthResult.REDIRECT_TO_PROVIDER == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOpenidClient_verifyOpResponse_failure() {
        final String methodName = "testHandleOpenidClient_verifyOpResponse";
        final ProviderAuthenticationResult authResult = new ProviderAuthenticationResult(AuthResult.FAILURE, 401);
        try {
            mockOidcClientResult(AuthResult.CONTINUE);
            mock.checking(new Expectations() {
                {
                    allowing(openIdClientServiceRef).getService();
                    will(returnValue(openidClientService));
                    allowing(openidClientService).getOpenIdIdentifier(req);
                    will(returnValue(null));
                    allowing(openidClientService).getRpRequestIdentifier(req, resp);
                    will(returnValue("requestIdentifier"));
                    allowing(openidClientService).verifyOpResponse(req, resp);
                    will(returnValue(authResult));
                }
            });

            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(req, resp, null);
            assertTrue("Verify OP responsed should fail", AuthResult.FAILURE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void testHandleOpenidClient_verifyOpResponse_success() {
        final String methodName = "testHandleOpenidClient_verifyOpResponse_success";
        final Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
        try {
            mockOidcClientResult(AuthResult.CONTINUE);
            mock.checking(new Expectations() {
                {
                    allowing(openIdClientServiceRef).getService();
                    will(returnValue(openidClientService));
                    allowing(openidClientService).getOpenIdIdentifier(req);
                    will(returnValue(null));
                    allowing(openidClientService).getRpRequestIdentifier(req, resp);
                    will(returnValue("requestIdentifier"));
                    allowing(openidClientService).verifyOpResponse(req, resp);
                    will(returnValue(providerResult));

                    allowing(openidClientService).isMapIdentityToRegistryUser();
                    will(returnValue(false));
                }
            });
            mockProviderAuthSuccessAndAuthHelper(hashtable, false);
            WebProviderAuthenticatorProxy webProviderAuthenticatorProxy =
                            new WebProviderAuthenticatorProxy(securityServiceRef, taiServiceRef, interceptorServiceRef, webAppSecurityConfig, oauthServiceRef, openIdClientServiceRef, oidcServerRef, oidcClientRef, null);
            webProviderAuthenticatorProxy.setWebProviderAuthenticatorHelper(authHelper);
            AuthenticationResult authenticationResult = webProviderAuthenticatorProxy.authenticate(req, resp, null);
            assertTrue("Verify OP responsed should success", AuthResult.SUCCESS == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    public void mockProviderAuthSuccessAndAuthHelper(final Hashtable<String, Object> hashtable, final boolean mapIdentityToRegistryUser) {
        String methodName = "mockProviderAuthSuccess_authHelper";
        final AuthenticationResult authResult = new AuthenticationResult(AuthResult.SUCCESS, methodName);
        final Subject subject = new Subject();

        try {
            mock.checking(new Expectations() {
                {
                    allowing(providerResult).getStatus();
                    will(returnValue(AuthResult.SUCCESS));
                    allowing(providerResult).getUserName();
                    will(returnValue("utle"));
                    allowing(providerResult).getSubject();
                    will(returnValue(subject));
                    allowing(providerResult).getCustomProperties();
                    will(returnValue(hashtable));
                    allowing(securityService).getUserRegistryService();
                    will(returnValue(userRegistryService));
                    allowing(userRegistryService).getUserRegistry();
                    will(returnValue(userRegistry));
                    allowing(userRegistry).getRealm();
                    will(returnValue("myRealm"));
                    allowing(authHelper).loginWithUserName(req, resp, "utle", subject, hashtable, mapIdentityToRegistryUser);
                    will(returnValue(authResult));
                }
            });
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @SuppressWarnings("unused")
    private void createNoAccessTokenRequest() {
        mock.checking(new Expectations() {
            {
                allowing(req).getCookies();
                will(returnValue(null));
                allowing(webRequest).getSecurityMetadata();
                allowing(taiService).getTais(with(any(boolean.class)));
                will(returnValue(null));
                allowing(webRequest).getHttpServletRequest();
                will(returnValue(req));
                allowing(webRequest).getHttpServletResponse();
                will(returnValue(resp));
                allowing(req).getHeader(AUTHORIZATION);
                will(returnValue(null));
                allowing(req).getHeader(ACCESS_TOKEN);
                will(returnValue(null));
                allowing(req).getParameter(ACCESS_TOKEN);
                will(returnValue(null));
            }
        });
    }

    @SuppressWarnings("unused")
    private void mockForOidcClientRequest() {
        mock.checking(new Expectations() {
            {
                allowing(oauthServiceRef).getService();
                will(returnValue(null));
                allowing(req).getCookies();
                will(returnValue(null));
                allowing(webRequest).getSecurityMetadata();
                allowing(taiService).getTais(with(any(boolean.class)));
                will(returnValue(null));
                allowing(webRequest).getHttpServletRequest();
                will(returnValue(req));
                allowing(webRequest).getHttpServletResponse();
                will(returnValue(resp));
                allowing(openIdClientServiceRef).getService();
                will(returnValue(null));
            }
        });
    }

    private void mockAccessTokenRequest() {
        mock.checking(new Expectations() {
            {
                allowing(req).getCookies();
                will(returnValue(null));
                allowing(webRequest).getSecurityMetadata();
                allowing(taiService).getTais(with(any(boolean.class)));
                will(returnValue(null));
                allowing(webRequest).getHttpServletRequest();
                will(returnValue(req));
                allowing(webRequest).getHttpServletResponse();
                will(returnValue(resp));
                allowing(req).getHeader(AUTHORIZATION);
                will(returnValue(ACCESS_TOKEN + "=myAccessToken"));
                allowing(req).getHeader(ACCESS_TOKEN);
                will(returnValue("access_token=xyx"));
                allowing(req).getParameter(ACCESS_TOKEN);
                will(returnValue("myAccessToken"));
            }
        });
    }

    private void mockOauthResult(final AuthResult continue1) {
        mock.checking(new Expectations() {
            {
                allowing(oauthServiceRef).getService();
                will(returnValue(oauthService));
                allowing(oauthService).authenticate(req, resp);
                will(returnValue(providerResult));
                allowing(providerResult).getStatus();
                will(returnValue(continue1));
            }
        });
    }

    public void mockOidcClientGetProvider(final String providerId) {
        mock.checking(new Expectations() {
            {
                allowing(oidcClientRef).getService();
                will(returnValue(oidcClient));
                allowing(oidcClient).getOidcProvider(req);
                will(returnValue(providerId));
            }
        });
    }

    public void mockOidcClientProviderResult(final String providerId, final ProviderAuthenticationResult providerAuthResult) {
        mock.checking(new Expectations() {
            {
                allowing(oidcClient).authenticate(with(req), with(resp), with(providerId), with(any(ReferrerURLCookieHandler.class)), with(any(Boolean.class)));
                will(returnValue(providerAuthResult));
            }
        });
    }

    private void mockOidcClientResult(final AuthResult result) {
        final String provider = "google";
        mock.checking(new Expectations() {
            {
                allowing(oidcClientRef).getService();
                will(returnValue(oidcClient));
                allowing(oidcClient).getOidcProvider(req);
                will(returnValue(provider));
                allowing(oidcClient).authenticate(with(req), with(resp), with(provider), with(any(ReferrerURLCookieHandler.class)), with(any(Boolean.class)));
                will(returnValue(oidcProviderResult));
                allowing(oidcProviderResult).getStatus();
                will(returnValue(result));
            }
        });
    }
}