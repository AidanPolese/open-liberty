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
package com.ibm.ws.jsonp.fat;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.topology.impl.LibertyServerFactory;

public class CustomFeatureJSONPTest extends AbstractTest {
    private static final String FEATURE_NAME = "customJsonpProvider-1.0.mf";
    private static final String BUNDLE_NAME = "com.ibm.ws.jsonp.feature.provider.1.0.jar";

    @BeforeClass
    public static void setUp() throws Exception {
        server = LibertyServerFactory.getLibertyServer("jsonp.fat.customFeature");
//        server.installUserFeature(FEATURE_NAME);
        server.copyFileToLibertyInstallRoot("usr/extension/lib/features/", "features/" + FEATURE_NAME);
        server.copyFileToLibertyInstallRoot("usr/extension/lib/", "bundles/" + BUNDLE_NAME);
//        server.installUserBundle(BUNDLE_NAME);

        WebArchive customFeatureJSONPWAR = ShrinkWrap.create(WebArchive.class, "customFeatureJSONPWAR.war")
                        .addPackages(true, "jsonp.app.feature.web");
        ShrinkHelper.exportToServer(server, "dropins", customFeatureJSONPWAR);
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
