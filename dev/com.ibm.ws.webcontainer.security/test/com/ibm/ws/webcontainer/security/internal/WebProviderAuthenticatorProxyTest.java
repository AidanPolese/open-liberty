package com.ibm.ws.webcontainer.security.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;

import com.ibm.ws.security.SecurityService;
import com.ibm.ws.security.authentication.AuthenticationService;
import com.ibm.ws.security.authentication.tai.TAIService;
import com.ibm.ws.security.registry.UserRegistry;
import com.ibm.ws.security.registry.UserRegistryService;
import com.ibm.ws.webcontainer.security.ProviderAuthenticationResult;
import com.ibm.ws.webcontainer.security.WebAppSecurityConfig;
import com.ibm.ws.webcontainer.security.WebRequest;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceMap;
import com.ibm.wsspi.security.tai.TrustAssociationInterceptor;

import test.common.SharedOutputManager;

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
    private final String AUTHORIZATION = "Authorization";

    private final HttpServletRequest req = mock.mock(HttpServletRequest.class);
    private final HttpServletResponse resp = mock.mock(HttpServletResponse.class);

    private final WebAppSecurityConfig webAppSecurityConfig = mock.mock(WebAppSecurityConfig.class);

    @SuppressWarnings("unchecked")
    private final AtomicServiceReference<SecurityService> securityServiceRef = mock.mock(AtomicServiceReference.class, "securityServiceRef");
    private final SecurityService securityService = mock.mock(SecurityService.class, "securityService");

    @SuppressWarnings("unchecked")
    private final AtomicServiceReference<TAIService> taiServiceRef = mock.mock(AtomicServiceReference.class, "taiServiceRef");
    private final TAIService taiService = mock.mock(TAIService.class, "taiService");
    private final ConcurrentServiceReferenceMap<String, TrustAssociationInterceptor> interceptorServiceRef = new ConcurrentServiceReferenceMap<String, TrustAssociationInterceptor>("interceptorServiceRef");

    private final UserRegistryService userRegistryService = mock.mock(UserRegistryService.class, "userRegistryService");
    private final UserRegistry userRegistry = mock.mock(UserRegistry.class, "userRegistry");

    @SuppressWarnings("unchecked")

    private final WebRequest webRequest = mock.mock(WebRequest.class);
    private final AuthenticationService authService = mock.mock(AuthenticationService.class);

    final ProviderAuthenticationResult providerResult = mock.mock(ProviderAuthenticationResult.class, "providerResult");
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
}