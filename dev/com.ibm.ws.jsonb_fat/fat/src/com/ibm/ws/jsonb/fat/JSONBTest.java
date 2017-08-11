package com.ibm.ws.jsonb.fat;

import static com.ibm.ws.jsonb.fat.FATSuite.PROVIDER_JOHNZON;
import static com.ibm.ws.jsonb.fat.FATSuite.PROVIDER_YASSON;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;
import web.jsonbtest.JSONBTestServlet;

@RunWith(FATRunner.class)
public class JSONBTest extends FATServletClient {
    private static final String SERVLET_PATH = "jsonbapp/JSONBTestServlet";

    @Server("com.ibm.ws.jsonb.fat")
    @TestServlet(servlet = JSONBTestServlet.class, path = SERVLET_PATH)
    public static LibertyServer server;

    private static final String appName = "jsonbapp";

    @BeforeClass
    public static void setUp() throws Exception {
        WebArchive app = ShrinkWrap.create(WebArchive.class, appName + ".war")
                        .addPackage("web.jsonbtest");
        ShrinkHelper.exportAppToServer(server, app);

        server.addInstalledAppForValidation(appName);
        server.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stopServer();
    }

    @Test
    public void testApplicationClasses() throws Exception {
        runTest(server, SERVLET_PATH, testName.getMethodName() + "&JsonbProvider=" + PROVIDER_YASSON);
    }

    @Test
    public void testJsonbDeserializer() throws Exception {
        runTest(server, SERVLET_PATH, testName.getMethodName() + "&JsonbProvider=" + PROVIDER_YASSON);
    }

    @Test
    public void testJsonbProviderAvailable() throws Exception {
        runTest(server, SERVLET_PATH, testName.getMethodName() + "&JsonbProvider=" + PROVIDER_YASSON);
    }

    @Test
    public void testJsonbProviderNotAvailable() throws Exception {
        runTest(server, SERVLET_PATH, testName.getMethodName() + "&JsonbProvider=" + PROVIDER_JOHNZON);
    }

    @Test
    public void testThreadContextClassLoader() throws Exception {
        runTest(server, SERVLET_PATH, testName.getMethodName() + "&JsonbProvider=" + PROVIDER_YASSON);
    }
}
