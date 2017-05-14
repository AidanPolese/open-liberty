/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.security.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.SharedOutputManager;

import com.ibm.ws.security.SecurityService;
import com.ibm.ws.security.authentication.AuthenticationService;
import com.ibm.ws.security.authentication.tai.TAIService;
import com.ibm.ws.security.registry.RegistryException;
import com.ibm.ws.webcontainer.security.AuthResult;
import com.ibm.ws.webcontainer.security.AuthenticationResult;
import com.ibm.ws.webcontainer.security.PostParameterHelper;
import com.ibm.ws.webcontainer.security.WebAppSecurityCollaboratorImpl;
import com.ibm.ws.webcontainer.security.WebAppSecurityConfig;
import com.ibm.ws.webcontainer.security.WebAuthenticatorProxy;
import com.ibm.ws.webcontainer.security.WebProviderAuthenticatorProxy;
import com.ibm.ws.webcontainer.security.WebRequest;
import com.ibm.ws.webcontainer.security.metadata.FormLoginConfiguration;
import com.ibm.ws.webcontainer.security.metadata.LoginConfiguration;
import com.ibm.ws.webcontainer.security.metadata.LoginConfigurationImpl;
import com.ibm.ws.webcontainer.security.metadata.SecurityMetadata;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;

/**
 *
 */
@SuppressWarnings("unchecked")
public class WebAuthenticatorProxyTest {

    private static SharedOutputManager outputMgr;
    private static final String AUTH_METHOD_BASIC = "BASIC";
    private static final String AUTH_METHOD_CERT = "CLIENT-CERT";
    private static final String AUTH_METHOD_CERT_UNDERSCORE = "CLIENT_CERT";
    private static final String AUTH_METHOD_FORM = "FORM";
    private final Mockery mockery = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private final WebAppSecurityConfig webAppSecurityConfig = mockery.mock(WebAppSecurityConfig.class);
    private final PostParameterHelper postParameterHelper = mockery.mock(PostParameterHelper.class);
    private final AtomicServiceReference<SecurityService> securityServiceRef = mockery.mock(AtomicServiceReference.class, "securityServiceRef");
    private final AtomicServiceReference<TAIService> taiServiceRef = mockery.mock(AtomicServiceReference.class, "taiServiceRef");
    final TAIService taiService = mockery.mock(TAIService.class);

//    private final TrustAssociationInterceptor interceptorFeatureService = mockery.mock(TrustAssociationInterceptor.class, "interceptorFeatureService");
//    private final ConcurrentServiceReferenceMap<String, TrustAssociationInterceptor> interceptorFeatureRef =
//                    new ConcurrentServiceReferenceMap<String, TrustAssociationInterceptor>("interceptorFeatureService");

    private final BasicAuthAuthenticator basicAuthenticator = mockery.mock(BasicAuthAuthenticator.class);
    private final FormLoginAuthenticator formLoginAuthenticator = mockery.mock(FormLoginAuthenticator.class);
    private final CertificateLoginAuthenticator certLoginAuthenticator = mockery.mock(CertificateLoginAuthenticator.class);

    private final WebProviderAuthenticatorProxy providerAuthenticatorProxy = mockery.mock(WebProviderAuthenticatorProxy.class, "providerAuthenticatorProxy");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        outputMgr = SharedOutputManager.getInstance();
        outputMgr.captureStreams();
    }

    @Before
    public void setUp() throws Exception {
        WebAppSecurityCollaboratorImpl.setGlobalWebAppSecurityConfig(null);

        createTAIServiceExpectations();
        createSecurityServiceExpectations();
        createPostParameterHelperExpectations();
    }

    private void createTAIServiceExpectations() {
        final Map<String, TrustAssociationInterceptor> tais = new HashMap<String, TrustAssociationInterceptor>();
        mockery.checking(new Expectations() {
            {
                allowing(taiServiceRef).getService();
                will(returnValue(taiService));
                allowing(taiService).getTais(true);
                will(returnValue(tais));
            }
        });
    }

    private void createSecurityServiceExpectations() {
        final SecurityService securityService = mockery.mock(SecurityService.class);
        final AuthenticationService authenticationService = mockery.mock(AuthenticationService.class);
        mockery.checking(new Expectations() {
            {
                allowing(securityServiceRef).getService();
                will(returnValue(securityService));
                allowing(securityService).getAuthenticationService();
                will(returnValue(authenticationService));
            }
        });
    }

    private void createPostParameterHelperExpectations() {
        mockery.checking(new Expectations() {
            {
                allowing(postParameterHelper).restore(with(any(HttpServletRequest.class)), with(any(HttpServletResponse.class)));
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        mockery.assertIsSatisfied();
        outputMgr.resetStreams();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        outputMgr.restoreStreams();
    }

    @Test
    public void authenticate_BASIC_failure() {
        final String methodName = "authenticate_BASIC_failure";
        try {
            LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_BASIC, null);
            SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
            HttpServletRequest req = createHttpServletRequest();
            HttpServletResponse resp = createHttpServletResponse();
            boolean formLoginRedirectEnabled = false;
            final WebRequest webRequest = createWebRequest(securityMetadata, req, resp, formLoginRedirectEnabled);
            createBasicAuthenticatorExpectations(webRequest, new AuthenticationResult(AuthResult.FAILURE, (String) null));
            WebAuthenticatorProxy authenticatorProxy =
                            new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);
            AuthenticationResult authenticationResult = authenticatorProxy.authenticate(webRequest);
            assertTrue("There must be an authentication failure.", AuthResult.FAILURE == authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void authenticate_FORM() {
        final String methodName = "authenticate_FORM";
        try {
            FormLoginConfiguration formLoginConfiguration = createFormLoginConfiguration();
            LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_FORM, formLoginConfiguration);
            SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
            HttpServletRequest req = createHttpServletRequest();
            HttpServletResponse resp = createHttpServletResponse();
            boolean formLoginRedirectEnabled = true;
            final WebRequest webRequest = createWebRequest(securityMetadata, req, resp, formLoginRedirectEnabled);

            createFormLoginAuthenticatorExpectations(webRequest, new AuthenticationResult(AuthResult.SUCCESS, new Subject()));

            WebAuthenticatorProxy authenticatorProxy =
                            new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);
            AuthenticationResult authenticationResult = authenticatorProxy.authenticate(webRequest);
            assertNotNull("There must be an authentication result.", authenticationResult);
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    @Test
    public void authenticate_FORM_noRedirection_returnsNull() {
        final String methodName = "authenticate_FORM_noRedirection_returnsNull";
        try {
            LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_FORM, null);
            SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
            HttpServletRequest req = createHttpServletRequest();
            HttpServletResponse resp = createHttpServletResponse();
            boolean formLoginRedirectEnabled = false;
            final WebRequest webRequest = createWebRequest(securityMetadata, req, resp, formLoginRedirectEnabled);

            createFormLoginAuthenticatorExpectations(webRequest, null);

            WebAuthenticatorProxy authenticatorProxy =
                            new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);
            AuthenticationResult authenticationResult = authenticatorProxy.authenticate(webRequest);
            assertNull("There must not be an authentication result.", authenticationResult);
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    /**
     * No failover - final result is FAILURE
     */
    @Test
    public void authenticate_CLIENT_CERT_failover_NONE() {
        final String methodName = "authenticate_CLIENT_CERT_failover_NONE";
        try {
            LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_CERT_UNDERSCORE, null);
            SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
            HttpServletRequest req = createHttpServletRequest();
            HttpServletResponse resp = createHttpServletResponse();
            boolean formLoginRedirectEnabled = true;
            final WebRequest webRequest = createWebRequest(securityMetadata, req, resp, formLoginRedirectEnabled);

            mockery.checking(new Expectations() {
                {
                    one(providerAuthenticatorProxy).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.CONTINUE, "notUsed")));

                    one(certLoginAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.FAILURE, "notUsed")));

                    allowing(webAppSecurityConfig).getAllowFailOverToBasicAuth();
                    will(returnValue(false));

                    allowing(webAppSecurityConfig).getAllowFailOverToFormLogin();
                    will(returnValue(false));

                    allowing(webAppSecurityConfig).allowFailOver();
                    will(returnValue(false));
                }
            });

            WebAuthenticatorProxy authenticatorProxy = new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

            AuthenticationResult authenticationResult = authenticatorProxy.authenticate(webRequest);
            assertEquals("There must be an authentication failure.",
                         AuthResult.FAILURE, authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    /**
     * Allow failover to BASIC w/ final result success
     */
    @Test
    public void authenticate_CLIENT_CERT_failover_BASIC() {
        final String methodName = "authenticate_CLIENT_CERT_failover_NONE";
        try {
            LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_CERT_UNDERSCORE, null);
            SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
            HttpServletRequest req = createHttpServletRequest();
            HttpServletResponse resp = createHttpServletResponse();
            boolean formLoginRedirectEnabled = true;
            final WebRequest webRequest = createWebRequest(securityMetadata, req, resp, formLoginRedirectEnabled);

            mockery.checking(new Expectations() {
                {
                    one(providerAuthenticatorProxy).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.CONTINUE, "notUsed")));

                    one(certLoginAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.FAILURE, "notUsed")));

                    allowing(webAppSecurityConfig).getAllowFailOverToBasicAuth();
                    will(returnValue(true));

                    allowing(webAppSecurityConfig).getAllowFailOverToFormLogin();
                    will(returnValue(false));

                    allowing(webAppSecurityConfig).allowFailOver();
                    will(returnValue(true));

                    one(basicAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.SUCCESS, "notUsed")));
                }
            });

            WebAuthenticatorProxy authenticatorProxy = new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

            AuthenticationResult authenticationResult = authenticatorProxy.authenticate(webRequest);
            assertEquals("There must be an authentication success.",
                         AuthResult.SUCCESS, authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    /**
     * Allow failover to FORM - final result SUCCESS
     */
    @Test
    public void authenticate_CLIENT_CERT_failover_FORM() {
        final String methodName = "authenticate_CLIENT_CERT_failover_FORM";
        try {
            FormLoginConfiguration formLoginConfiguration = createFormLoginConfiguration();
            LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_CERT_UNDERSCORE, formLoginConfiguration);
            SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
            HttpServletRequest req = createHttpServletRequest();
            HttpServletResponse resp = createHttpServletResponse();
            boolean formLoginRedirectEnabled = true;
            final WebRequest webRequest = createWebRequest(securityMetadata, req, resp, formLoginRedirectEnabled);

            mockery.checking(new Expectations() {
                {
                    one(providerAuthenticatorProxy).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.CONTINUE, "notUsed")));

                    one(certLoginAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.FAILURE, "notUsed")));

                    allowing(webAppSecurityConfig).getAllowFailOverToBasicAuth();
                    will(returnValue(false));

                    allowing(webAppSecurityConfig).getAllowFailOverToFormLogin();
                    will(returnValue(true));

                    allowing(webAppSecurityConfig).allowFailOver();
                    will(returnValue(true));

                    one(formLoginAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.SUCCESS, "notUsed")));
                }
            });

            WebAuthenticatorProxy authenticatorProxy = new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

            AuthenticationResult authenticationResult = authenticatorProxy.authenticate(webRequest);
            assertEquals("There must be an authentication success.",
                         AuthResult.SUCCESS, authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    /**
     * Allow failover to FORM - final result SUCCESS
     */
    @Test
    public void authenticate_CLIENT_CERT_failover_BOTH_with_FORM() {
        final String methodName = "authenticate_CLIENT_CERT_failover_BOTH_with_FORM";
        try {
            FormLoginConfiguration formLoginConfiguration = createFormLoginConfiguration();
            LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_CERT_UNDERSCORE, formLoginConfiguration);
            SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
            HttpServletRequest req = createHttpServletRequest();
            HttpServletResponse resp = createHttpServletResponse();
            boolean formLoginRedirectEnabled = true;
            final WebRequest webRequest = createWebRequest(securityMetadata, req, resp, formLoginRedirectEnabled);

            mockery.checking(new Expectations() {
                {
                    one(providerAuthenticatorProxy).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.CONTINUE, "notUsed")));

                    one(certLoginAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.FAILURE, "notUsed")));

                    allowing(webAppSecurityConfig).getAllowFailOverToBasicAuth();
                    will(returnValue(true));

                    allowing(webAppSecurityConfig).getAllowFailOverToFormLogin();
                    will(returnValue(true));

                    allowing(webAppSecurityConfig).allowFailOver();
                    will(returnValue(true));

                    one(formLoginAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.SUCCESS, "notUsed")));
                }
            });

            WebAuthenticatorProxy authenticatorProxy = new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

            AuthenticationResult authenticationResult = authenticatorProxy.authenticate(webRequest);
            assertEquals("There must be an authentication success.",
                         AuthResult.SUCCESS, authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    /**
     * Allow failover to FORM - final result SUCCESS
     */
    @Test
    public void authenticate_CLIENT_CERT_failover_BOTH_with_global_FORM() {
        final String methodName = "authenticate_CLIENT_CERT_failover_BOTH_with_global_FORM";
        try {
            LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_CERT_UNDERSCORE, null);
            SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
            HttpServletRequest req = createHttpServletRequest();
            HttpServletResponse resp = createHttpServletResponse();
            boolean formLoginRedirectEnabled = true;
            final WebRequest webRequest = createWebRequest(securityMetadata, req, resp, formLoginRedirectEnabled);

            WebAppSecurityCollaboratorImpl.setGlobalWebAppSecurityConfig(webAppSecurityConfig);

            mockery.checking(new Expectations() {
                {
                    one(providerAuthenticatorProxy).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.CONTINUE, "notUsed")));

                    one(certLoginAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.FAILURE, "notUsed")));

                    allowing(webAppSecurityConfig).getAllowFailOverToBasicAuth();
                    will(returnValue(true));

                    allowing(webAppSecurityConfig).getAllowFailOverToFormLogin();
                    will(returnValue(true));

                    allowing(webAppSecurityConfig).allowFailOver();
                    will(returnValue(true));

                    allowing(webAppSecurityConfig).getLoginFormURL();
                    will(returnValue("/login.jsp"));

                    one(formLoginAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.SUCCESS, "notUsed")));
                }
            });

            WebAuthenticatorProxy authenticatorProxy = new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

            AuthenticationResult authenticationResult = authenticatorProxy.authenticate(webRequest);
            assertEquals("There must be an authentication success.",
                         AuthResult.SUCCESS, authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

    /**
     * Allow failover to FORM - final result SUCCESS
     */
    @Test
    public void authenticate_CLIENT_CERT_failover_BOTH_without_FORM() {
        final String methodName = "authenticate_CLIENT_CERT_failover_BOTH_without_FORM";
        try {
            LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_CERT_UNDERSCORE, null);
            SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
            HttpServletRequest req = createHttpServletRequest();
            HttpServletResponse resp = createHttpServletResponse();
            boolean formLoginRedirectEnabled = true;
            final WebRequest webRequest = createWebRequest(securityMetadata, req, resp, formLoginRedirectEnabled);

            mockery.checking(new Expectations() {
                {
                    one(providerAuthenticatorProxy).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.CONTINUE, "notUsed")));

                    one(certLoginAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.FAILURE, "notUsed")));

                    allowing(webAppSecurityConfig).getAllowFailOverToBasicAuth();
                    will(returnValue(true));

                    allowing(webAppSecurityConfig).getAllowFailOverToFormLogin();
                    will(returnValue(true));

                    allowing(webAppSecurityConfig).allowFailOver();
                    will(returnValue(true));

                    one(basicAuthenticator).authenticate(webRequest);
                    will(returnValue(new AuthenticationResult(AuthResult.SUCCESS, "notUsed")));
                }
            });

            WebAuthenticatorProxy authenticatorProxy = new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

            AuthenticationResult authenticationResult = authenticatorProxy.authenticate(webRequest);
            assertEquals("There must be an authentication success.",
                         AuthResult.SUCCESS, authenticationResult.getStatus());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(methodName, t);
        }
    }

//    @Test(expected = NullPointerException.class)
//    public void getSSOAuthenticator_nullNotSupported() {
//        WebAuthenticatorProxy authenticatorProxy =
//                        new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);
//        authenticatorProxy.getSSOAuthenticator(null);
//    }

//    @Test
//    public void getSSOAuthenticator() {
//        WebAuthenticatorProxy authenticatorProxy =
//                        new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);
//
//        LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_BASIC, null);
//        SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
//        WebRequest webRequest = createWebRequest(securityMetadata, null, null, false);
//        assertNotNull("If all of the required collaborators are available, an SSOAuthenticator should be created",
//                      authenticatorProxy.getSSOAuthenticator(webRequest));
//    }

    @Test
    public void getWebAuthenticator_basicLoginConfig() throws Exception {
        WebAuthenticatorProxy authenticatorProxy =
                        new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

        LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_BASIC, null);
        SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
        WebRequest webRequest = createWebRequest(securityMetadata, null, null, false);
        assertTrue("If the login configuration is BASIC, use BasicAuth",
                   authenticatorProxy.getWebAuthenticator(webRequest) instanceof BasicAuthAuthenticator);
    }

    @Test
    public void getWebAuthenticator_clientDASHCertLoginConfig() {
        WebAuthenticatorProxy authenticatorProxy =
                        new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

        LoginConfiguration loginConfiguration = new LoginConfigurationImpl(AUTH_METHOD_CERT, null, null);
        SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
        WebRequest webRequest = createWebRequest(securityMetadata, null, null, false);

        assertTrue("If the login configuration is CLIENT-CERT, create a CertificateLoginAuthenticator",
                   authenticatorProxy.getWebAuthenticator(webRequest) instanceof CertificateLoginAuthenticator);
    }

    @Test
    public void getWebAuthenticator_clientUNDERSCORECertLoginConfig() {
        WebAuthenticatorProxy authenticatorProxy =
                        new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

        LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_CERT_UNDERSCORE, null);
        SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
        WebRequest webRequest = createWebRequest(securityMetadata, null, null, false);

        assertTrue("If the login configuration is CLIENT_CERT, create a CertificateLoginAuthenticator",
                   authenticatorProxy.getWebAuthenticator(webRequest) instanceof CertificateLoginAuthenticator);
    }

    @Test
    public void getWebAuthenticator_formLoginConfig() {
        WebAuthenticatorProxy authenticatorProxy =
                        new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

        LoginConfiguration loginConfiguration = createLoginConfiguration(AUTH_METHOD_FORM, null);
        SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
        WebRequest webRequest = createWebRequest(securityMetadata, null, null, false);

        assertTrue("If the login configuration is FORM, create a FormLoginAuthenticator",
                   authenticatorProxy.getWebAuthenticator(webRequest) instanceof FormLoginAuthenticator);
    }

    @Test
    public void getWebAuthenticator_nullLoginConfig() throws Exception {
        WebAuthenticatorProxy authenticatorProxy =
                        new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

        SecurityMetadata securityMetadata = createSecurityMetadata(null);
        WebRequest webRequest = createWebRequest(securityMetadata, null, null, false);

        assertTrue("If no login configuration is defined, use BasicAuth",
                   authenticatorProxy.getWebAuthenticator(webRequest) instanceof BasicAuthAuthenticator);
    }

    @Test(expected = NullPointerException.class)
    public void getWebAuthenticator_nullNotSupported() {
        WebAuthenticatorProxy authenticatorProxy =
                        new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);
        authenticatorProxy.getWebAuthenticator(null);
    }

    @Test
    public void getWebAuthenticator_unknownLoginConfig() throws Exception {
        WebAuthenticatorProxy authenticatorProxy =
                        new WebAuthenticatorProxyTestDouble(webAppSecurityConfig, postParameterHelper, securityServiceRef, taiServiceRef);

        LoginConfiguration loginConfiguration = createLoginConfiguration("UNKNOWN", null);
        SecurityMetadata securityMetadata = createSecurityMetadata(loginConfiguration);
        WebRequest webRequest = createWebRequest(securityMetadata, null, null, false);

        assertTrue("If the login configuration is unknown, use BasicAuth",
                   authenticatorProxy.getWebAuthenticator(webRequest) instanceof BasicAuthAuthenticator);
    }

    private FormLoginConfiguration createFormLoginConfiguration() {
        final FormLoginConfiguration formLoginConfiguration = mockery.mock(FormLoginConfiguration.class);
        mockery.checking(new Expectations() {
            {
                allowing(formLoginConfiguration).getLoginPage();
                will(returnValue("/login.jsp"));

                allowing(formLoginConfiguration).getErrorPage();
                will(returnValue("/loginError.jsp"));
            }
        });
        return formLoginConfiguration;
    }

    private LoginConfiguration createLoginConfiguration(final String authenticationMethod, final FormLoginConfiguration formLoginConfiguration) {
        final LoginConfiguration loginConfiguration = mockery.mock(LoginConfiguration.class);
        mockery.checking(new Expectations() {
            {
                allowing(loginConfiguration).getAuthenticationMethod();
                will(returnValue(authenticationMethod));
                allowing(loginConfiguration).getFormLoginConfiguration();
                will(returnValue(formLoginConfiguration));
            }
        });
        return loginConfiguration;
    }

    private SecurityMetadata createSecurityMetadata(final LoginConfiguration loginConfiguration) {
        final SecurityMetadata securityMetadata = mockery.mock(SecurityMetadata.class);
        mockery.checking(new Expectations() {
            {
                allowing(securityMetadata).getLoginConfiguration();
                will(returnValue(loginConfiguration));

            }
        });
        return securityMetadata;
    }

    private HttpServletRequest createHttpServletRequest() {
        final HttpServletRequest req = mockery.mock(HttpServletRequest.class);
        mockery.checking(new Expectations() {
            {
                allowing(req).getCookies();
                will(returnValue(null));

            }
        });
        return req;
    }

    private HttpServletResponse createHttpServletResponse() {
        final HttpServletResponse resp = mockery.mock(HttpServletResponse.class);
        return resp;
    }

    private WebRequest createWebRequest(final SecurityMetadata securityMetadata,
                                        final HttpServletRequest req,
                                        final HttpServletResponse resp,
                                        final boolean formLoginRedirectEnabled) {
        final WebRequest webRequest = mockery.mock(WebRequest.class);
        final LoginConfiguration loginConfig = securityMetadata.getLoginConfiguration();
        final FormLoginConfiguration formLoginConfiguration;
        if (loginConfig != null) {
            formLoginConfiguration = loginConfig.getFormLoginConfiguration();
        } else {
            formLoginConfiguration = null;
        }
        mockery.checking(new Expectations() {
            {
                allowing(webRequest).getSecurityMetadata();
                will(returnValue(securityMetadata));
                allowing(webRequest).getHttpServletRequest();
                will(returnValue(req));
                allowing(webRequest).getHttpServletResponse();
                will(returnValue(resp));
                allowing(webRequest).getLoginConfig();
                will(returnValue(loginConfig));
                allowing(webRequest).getFormLoginConfiguration();
                will(returnValue(formLoginConfiguration));
                allowing(webRequest).isUnprotectedURI();
            }
        });
        return webRequest;
    }

    private void createBasicAuthenticatorExpectations(final WebRequest webRequest, final AuthenticationResult authenticationResult) {
        mockery.checking(new Expectations() {
            {
                allowing(basicAuthenticator).authenticate(webRequest);
                will(returnValue(authenticationResult));
                allowing(webRequest).isUnprotectedURI();
                allowing(taiService).getTais(false);
                allowing(providerAuthenticatorProxy).authenticate(webRequest);
                will(returnValue(authenticationResult));

            }
        });
    }

    private void createFormLoginAuthenticatorExpectations(final WebRequest webRequest, final AuthenticationResult authenticationResult) {
        final AuthenticationResult authResult = new AuthenticationResult(AuthResult.CONTINUE, "continue ...");
        mockery.checking(new Expectations() {
            {
                allowing(formLoginAuthenticator).authenticate(webRequest);
                will(returnValue(authenticationResult));
                allowing(taiService).getTais(false);
                allowing(providerAuthenticatorProxy).authenticate(webRequest);
                will(returnValue(authResult));
            }
        });
    }

    class WebAuthenticatorProxyTestDouble extends WebAuthenticatorProxy {

        public WebAuthenticatorProxyTestDouble(WebAppSecurityConfig webAppSecurityConfig, PostParameterHelper postParameterHelper,
                                               AtomicServiceReference<SecurityService> securityServiceRef, AtomicServiceReference<TAIService> taiServiceRef) {
            super(webAppSecurityConfig, postParameterHelper, securityServiceRef, providerAuthenticatorProxy);
        }

        @Override
        protected BasicAuthAuthenticator createBasicAuthenticator() throws RegistryException {
            return basicAuthenticator;
        }

        @Override
        protected FormLoginAuthenticator createFormLoginAuthenticator(WebRequest webRequest) {
            return formLoginAuthenticator;
        }

        @Override
        public CertificateLoginAuthenticator createCertificateLoginAuthenticator() {
            return certLoginAuthenticator;
        }

    }

}
