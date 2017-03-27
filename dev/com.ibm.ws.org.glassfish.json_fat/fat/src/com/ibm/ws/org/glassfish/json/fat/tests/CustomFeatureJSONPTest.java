/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.org.glassfish.json.fat.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import componenttest.topology.impl.LibertyServerFactory;

public class CustomFeatureJSONPTest extends AbstractTest {
    private static final String FEATURE_NAME = "customJsonpProvider-1.0";
    private static final String BUNDLE_NAME = "com.ibm.ws.jsonp.feature.provider.1.0_1.0.0";

    @BeforeClass
    public static void setUp() throws Exception {
        server = LibertyServerFactory.getLibertyServer("com.ibm.ws.org.glassfish.json.fat.CustomFeatureJsonpServer");
        server.installUserFeature(FEATURE_NAME);
        server.installUserBundle(BUNDLE_NAME);
        server.addInstalledAppForValidation("customFeatureJSONPWAR");
        server.startServer("customFeatureJSONPTest.log");
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stopServer();
        server.uninstallUserBundle(BUNDLE_NAME);
        server.uninstallUserFeature(FEATURE_NAME);
    }

    /**
     * Test plugging in a custom implementation for JSON processing,
     * where the custom implementation is packaged in a user defined feature.
     */
    @Test
    public void testCustomFeatureJsonProvider() throws Exception {
        this.servlet = "/customFeatureJSONPWAR/CustomJsonProviderServlet";
        runTest();
    }
}
