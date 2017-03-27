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

import org.junit.BeforeClass;
import org.junit.Test;

import componenttest.topology.impl.LibertyServerFactory;

public class BasicJSONPTest extends AbstractTest {

    @BeforeClass
    public static void setUp() throws Exception {
        server = LibertyServerFactory.getLibertyServer("com.ibm.ws.org.glassfish.json.fat.BasicJsonpServer");
        server.addInstalledAppForValidation("JSONPWAR");
        server.addInstalledAppForValidation("customAppJSONPWAR");
        server.addInstalledAppForValidation("customLibJSONPWAR");
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
