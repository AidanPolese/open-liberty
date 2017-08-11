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

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.topology.impl.LibertyServerFactory;

public class BasicJSONPTest extends AbstractTest {

    @BeforeClass
    public static void setUp() throws Exception {
        server = LibertyServerFactory.getLibertyServer("jsonp.fat.basic");

        WebArchive customAppJSONPWAR = ShrinkWrap.create(WebArchive.class, "customAppJSONPWAR.war")
                        .addAsServiceProvider(javax.json.spi.JsonProvider.class, jsonp.app.custom.provider.JsonProviderImpl.class)
                        .addPackages(true, "jsonp.app.custom");
        ShrinkHelper.exportToServer(server, "dropins", customAppJSONPWAR);
        server.addInstalledAppForValidation("customAppJSONPWAR");

        JavaArchive customLibJSONPProvider = ShrinkWrap.create(JavaArchive.class, "customLibJSONPProvider.jar")
                        .addAsServiceProvider(javax.json.spi.JsonProvider.class, jsonp.lib.provider.JsonProviderImpl.class)
                        .addPackage("jsonp.lib.provider");
        ShrinkHelper.exportToServer(server, "JSONPProviderLib", customLibJSONPProvider);

        WebArchive customLibJSONPWAR = ShrinkWrap.create(WebArchive.class, "customLibJSONPWAR.war")
                        .addPackage("jsonp.lib.web");
        ShrinkHelper.exportAppToServer(server, customLibJSONPWAR);
        server.addInstalledAppForValidation("customLibJSONPWAR");

        WebArchive jsonpWar = ShrinkWrap.create(WebArchive.class, "JSONPWAR.war")
                        .addAsWebInfResource(new File("test-applications/JSONPWAR.war/resources/WEB-INF/json_read_test_data.js"))
                        .addPackage("jsonp.app.web");
        ShrinkHelper.exportToServer(server, "dropins", jsonpWar);
        server.addInstalledAppForValidation("JSONPWAR");

        server.startServer("BasicJSONPTest.log");
    }

    /**
     * Ensure that JsonObjectBuilder is functioning.
     */
    @Test
    public void testJsonBuild() throws Exception {
        this.servlet = "/JSONPWAR/BuildJSONPServlet";
        runTest();
    }

    /**
     * Ensure that JsonReader is functioning.
     */
    @Test
    public void testJsonRead() throws Exception {
        this.servlet = "/JSONPWAR/ReadJSONPServlet";
        runTest();
    }

    /**
     * Ensure that JsonWriter is functioning.
     */
    @Test
    public void testJsonWrite() throws Exception {
        this.servlet = "/JSONPWAR/WriteJSONPServlet";
        runTest();
    }

    /**
     * Ensure that JsonGenerator is functioning.
     */
    @Test
    public void testJsonStream() throws Exception {
        this.servlet = "/JSONPWAR/StreamJSONPServlet";
        runTest();
    }

    /**
     * Test plugging in a custom implementation for JSON processing,
     * where the custom implementation is packaged in the application.
     */
    @Test
    public void testCustomAppJsonProvider() throws Exception {
        this.servlet = "/customAppJSONPWAR/CustomJsonProviderServlet";
        runTest();
    }

    /**
     * Test plugging in a custom implementation for JSON processing,
     * where the custom implementation is packaged in a shared library.
     */
    @Test
    public void testCustomLibJsonProvider() throws Exception {
        this.servlet = "/customLibJSONPWAR/CustomJsonProviderServlet";
        runTest();
    }
}
