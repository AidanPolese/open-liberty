/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
