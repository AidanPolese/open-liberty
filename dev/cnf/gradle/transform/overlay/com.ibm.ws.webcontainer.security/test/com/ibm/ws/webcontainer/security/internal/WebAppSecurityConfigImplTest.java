package com.ibm.ws.webcontainer.security.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import com.ibm.ws.webcontainer.security.WebAppSecurityConfig;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

public class WebAppSecurityConfigImplTest {
    private final Mockery mock = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private final WebAppSecurityConfig mockedConfig = mock.mock(WebAppSecurityConfig.class);
//    private final WsLocationAdmin mockLocationAdmin = mock.mock(WsLocationAdmin.class);
    private final ComponentContext cc = mock.mock(ComponentContext.class);
    private final BundleContext bundleContext = mock.mock(BundleContext.class);
    private final AtomicServiceReference<WsLocationAdmin> locationAdminRef = mock.mock(AtomicServiceReference.class, "locationAdminRef");
    private final WsLocationAdmin locateService = mock.mock(WsLocationAdmin.class);

    private final String USER_DIR = "userDir";
    private final String SERVER_NAME = "serverName";

    @Before
    public void setUp() {
        mock.checking(new Expectations() {
            {
                allowing(locationAdminRef).getService();
                will(returnValue(locateService));
                allowing(locateService).resolveString(WebAppSecurityConfigImpl.WLP_USER_DIR);
                will(returnValue(USER_DIR));
                allowing(locateService).getServerName();
                will(returnValue(SERVER_NAME));
            }
        });
    }

    @Test
    public void testGetSSODomainList_noUseDomainFromURL() {
        Map<String, Object> cfg = new HashMap<String, Object>();
        mockCookie(cfg, false);
        cfg.put("ssoDomainNames", "austin.ibm.com|raleigh.ibm.com");

        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, locationAdminRef);
        List<String> webCfgList = webCfg.getSSODomainList();
        assertTrue(webCfgList.contains("austin.ibm.com"));
        assertTrue(webCfgList.contains("raleigh.ibm.com"));
    }

    @Test
    public void testGetSSODomainList_with_UseDomainFromURL() {
        Map<String, Object> cfg = new HashMap<String, Object>();
        mockCookie(cfg, false);
        cfg.put("ssoDomainNames", "austin.ibm.com|raleigh.ibm.com|useDomainFromURL");

        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, locationAdminRef);
        List<String> webCfgList = webCfg.getSSODomainList();
        assertTrue(webCfgList.contains("austin.ibm.com"));
        assertTrue(webCfgList.contains("raleigh.ibm.com"));
    }

    /**
     * If the implementation of the WebAppSecurityConfig is not an
     * WebAppSecurityConfigImpl, an empty String should be returned.
     */
    @Test
    public void getChangedProperties_wrongImplementation() {
        Map<String, Object> cfg = new HashMap<String, Object>();
        mockCookie(cfg, false);
        cfg.put("ssoDomainNames", "austin.ibm.com|raleigh.ibm.com|useDomainFromURL");

        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, locationAdminRef);
        assertEquals("When the object is not the same implementation type, an empty String should be returned",
                     "", webCfg.getChangedProperties(mockedConfig));
    }

    /**
     * If the implementation of the WebAppSecurityConfig the same as the one
     * being compared to, an empty String should be returned.
     */
    @Test
    public void getChangedProperties_noChange() {
        Map<String, Object> cfg = new HashMap<String, Object>();
        mockCookie(cfg, false);
        cfg.put("ssoDomainNames", "austin.ibm.com|raleigh.ibm.com|useDomainFromURL");

        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, locationAdminRef);
        assertEquals("When the object is the same, an empty String should be returned",
                     "", webCfg.getChangedProperties(webCfg));
    }

    /**
     * Ensure that when only one property changes, only that property should be
     * returned as modified.
     */
    @Test
    public void getChangedProperties_oneChange() {
        Map<String, Object> cfg = new HashMap<String, Object>();
        mockCookie(cfg, false);
        cfg.put("ssoCookieName", "webSSOCookie");
        cfg.put("ssoDomainNames", "austin.ibm.com|raleigh.ibm.com|useDomainFromURL");
        WebAppSecurityConfig webCfgOld = new WebAppSecurityConfigImpl(cfg, locationAdminRef);

        String newValue = "austin.ibm.com|raleigh.ibm.com";
        // Intentionally causing a new String to be created to guard against
        // accidentally doing instance comparison
        cfg.put("ssoCookieName", new String("webSSOCookie"));
        cfg.put("ssoDomainNames", newValue);
        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, null);
        assertEquals("When one setting has changed, that new value should be returned",
                     "ssoDomainNames=" + newValue, webCfg.getChangedProperties(webCfgOld));
    }

    /**
     * When multiple properties change, all modified properties should be listed.
     */
    @Test
    public void getChangedProperties_fewChanges() {
        Map<String, Object> cfg = new HashMap<String, Object>();
        cfg.put("allowFailOverToBasicAuth", Boolean.FALSE);
        cfg.put("displayAuthenticationRealm", Boolean.FALSE);
        cfg.put("ssoCookieName", "webSSOCookie");
        cfg.put("autoGenSsoCookieName", Boolean.FALSE);
        cfg.put("ssoDomainNames", "austin.ibm.com|raleigh.ibm.com|useDomainFromURL");
        cfg.put("webAlwaysLogin", Boolean.TRUE);
        WebAppSecurityConfig webCfgOld = new WebAppSecurityConfigImpl(cfg, locationAdminRef);

        String newCookieValue = "mySSOCookie";
        cfg.put("ssoCookieName", newCookieValue);
        String newDomainValue = "";
        cfg.put("ssoDomainNames", newDomainValue);
        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, locationAdminRef);
        assertEquals("Only the settings that changed should be listed",
                     "ssoCookieName=" + newCookieValue + ",ssoDomainNames=" + newDomainValue,
                     webCfg.getChangedProperties(webCfgOld));
    }

    @Test
    public void getChangedProperties_allChanged() {
        Map<String, Object> cfg = new HashMap<String, Object>();
        cfg.put("allowFailOverToBasicAuth", Boolean.TRUE);
        cfg.put("displayAuthenticationRealm", Boolean.FALSE);
        cfg.put("ssoCookieName", "webSSOCookie");
        cfg.put("autoGenSsoCookieName", Boolean.FALSE);
        cfg.put("ssoDomainNames", "austin.ibm.com|raleigh.ibm.com|useDomainFromURL");
        cfg.put("webAlwaysLogin", Boolean.TRUE);
        WebAppSecurityConfig webCfgOld = new WebAppSecurityConfigImpl(cfg, locationAdminRef);

        cfg.put("allowFailOverToBasicAuth", Boolean.FALSE);
        cfg.put("displayAuthenticationRealm", Boolean.TRUE);
        cfg.put("ssoCookieName", "mySSOCookie");
        cfg.put("autoGenSsoCookieName", Boolean.FALSE);
        cfg.put("ssoDomainNames", "");
        cfg.put("webAlwaysLogin", Boolean.FALSE);
        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, locationAdminRef);

        assertEquals("When all settings have changed, all should be listed",
                     "allowFailOverToBasicAuth=false,displayAuthenticationRealm=true,ssoCookieName=mySSOCookie,ssoDomainNames=,webAlwaysLogin=false",
                     webCfg.getChangedProperties(webCfgOld));
    }

    private void driveSingleAttributeTest(String name, Object oldValue, Object newValue) {
        Map<String, Object> cfg = new HashMap<String, Object>();
        cfg.put(name, oldValue);
        WebAppSecurityConfig webCfgOld = new WebAppSecurityConfigImpl(cfg, locationAdminRef);

        cfg.put(name, newValue);
        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, locationAdminRef);
        assertEquals("Did not get expected name value pair for attribute " + name,
                     name + "=" + newValue, webCfg.getChangedProperties(webCfgOld));
    }

    @Test
    public void getChangedProperties_allowFailOverToBasicAuth() {
        driveSingleAttributeTest("allowFailOverToBasicAuth",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_allowLogoutPageRedirectToAnyHost() {
        driveSingleAttributeTest("allowLogoutPageRedirectToAnyHost",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_displayAuthenticationRealm() {
        driveSingleAttributeTest("displayAuthenticationRealm",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_httpOnlyCookies() {
        driveSingleAttributeTest("httpOnlyCookies",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_logoutOnHttpSessionExpire() {
        driveSingleAttributeTest("logoutOnHttpSessionExpire",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_logoutPageRedirectDomainNames() {
        driveSingleAttributeTest("logoutPageRedirectDomainNames",
                                 "abc", "abc|123");
    }

    @Test
    public void getChangedProperties_preserveFullyQualifiedReferrerUrl() {
        driveSingleAttributeTest("preserveFullyQualifiedReferrerUrl",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_postParamCookieSize() {
        driveSingleAttributeTest("postParamCookieSize",
                                 1024, 2048);
    }

    @Test
    public void getChangedProperties_postParamSaveMethod() {
        driveSingleAttributeTest("postParamSaveMethod",
                                 "NONE", "COOKIE");
    }

    @Test
    public void getChangedProperties_singleSignonEnabled() {
        driveSingleAttributeTest("singleSignonEnabled",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_ssoCookieName() {
        driveSingleAttributeTest("ssoCookieName",
                                 "webSSOCookie", "mySSOCookie");
    }

    @Test
    public void getChangedProperties_useOnlyCustomCookieName() {
        driveSingleAttributeTest("useOnlyCustomCookieName",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_autoGenSsoCookieName() {
        driveSingleAttributeTest("autoGenSsoCookieName",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_ssoDomainNames() {
        driveSingleAttributeTest("ssoDomainNames",
                                 "ibm.com", "google.com");
    }

    @Test
    public void getChangedProperties_ssoRequiresSSL() {
        driveSingleAttributeTest("ssoRequiresSSL",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_ssoUseDomainFromURL() {
        driveSingleAttributeTest("ssoUseDomainFromURL",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_useAuthenticationDataForUnprotectedResource() {
        driveSingleAttributeTest("useAuthenticationDataForUnprotectedResource",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void getChangedProperties_webAlwaysLogin() {
        driveSingleAttributeTest("webAlwaysLogin",
                                 Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    public void testSetSsoCookieName_autoGenSsoCookieName_true_defaultSsoCookieName() {
        Map<String, Object> cfg = new HashMap<String, Object>();
        mockCookie(cfg, true);
        cfg.put("ssoCookieName", WebAppSecurityConfigImpl.DEFAULT_SSO_COOKIE_NAME);
        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, locationAdminRef);
        String expectCookieName = createExpectSsoCookieName(webCfg);
        assertEquals("Did not get expected ssoCookieName " + expectCookieName, expectCookieName, webCfg.getSSOCookieName());
    }

    @Test
    public void testSetSsoCookieName_autoGenSsoCookieName_true_notDefaultSsoCookieName() {
        Map<String, Object> cfg = new HashMap<String, Object>();
        mockCookie(cfg, true);
        cfg.put("ssoCookieName", "myCookieName");
        WebAppSecurityConfig webCfg = new WebAppSecurityConfigImpl(cfg, locationAdminRef);
        assertEquals("Did not get expected ssoCookieName myCookieName", "myCookieName", webCfg.getSSOCookieName());
    }

    /**
     * @return
     */
    private String createExpectSsoCookieName(WebAppSecurityConfig webCfg) {

        String slash = USER_DIR.endsWith("/") ? "" : "/";
        String serverUniq = getHostName() + "_" + USER_DIR + slash + "servers/" + SERVER_NAME;
        return "WAS_" + hash(serverUniq);
    }

    private String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
        } catch (java.net.UnknownHostException e) {
            return "localhost";
        }
    }

    /**
     * @param cfg
     * @param autoGenCookieName TODO
     */
    private void mockCookie(Map<String, Object> cfg, Boolean autoGenCookieName) {
        if (!autoGenCookieName) {
            cfg.put("ssoCookieName", "webSSOCookie");
        }
        cfg.put("autoGenSsoCookieName", autoGenCookieName);
    }

    static String hash(String stringToEncrypt) {
        int hashCode = stringToEncrypt.hashCode();
        if (hashCode < 0) {
            hashCode = hashCode * -1;
            return "n" + hashCode;
        } else {
            return "p" + hashCode;
        }
    }
}
