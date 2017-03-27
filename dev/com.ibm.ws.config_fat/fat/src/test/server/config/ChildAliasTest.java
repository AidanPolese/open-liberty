/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.server.config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

public class ChildAliasTest {

    /**
     * Utility to set the method name as a String before the test
     */
    @Rule
    public TestName name = new TestName();

    public String testName = "";

    @Before
    public void setTestName() {
        // set the current test name
        testName = name.getMethodName();
    }

    private static final String CONTEXT_ROOT = "/childalias";

    private static LibertyServer testServer = LibertyServerFactory.getLibertyServer("com.ibm.ws.config.childalias");

    @BeforeClass
    public static void setUpForConfigExtensionsTests() throws Exception {
        //copy the extensions tests features into the server features location
        testServer.copyFileToLibertyInstallRoot("lib/features", "internalFeatureForFat/childAliasTest-1.0.mf");
        testServer.copyFileToLibertyInstallRoot("lib/features", "internalFeatureForFat/childAliasTestB-1.0.mf");
        testServer.copyFileToLibertyInstallRoot("lib/features", "internalFeatureForFat/childAliasTestC-1.0.mf");

        // Copy the config fat internal feature
        testServer.copyFileToLibertyInstallRoot("lib/features", "internalFeatureForFat/configfatlibertyinternals-1.0.mf");

        //copy the extensions tests bundles into the server lib location
        testServer.copyFileToLibertyInstallRoot("lib", "bundles/test.config.childalias_1.0.0.jar");
        testServer.copyFileToLibertyInstallRoot("lib", "bundles/test.config.childalias.b_1.0.0.jar");
        testServer.copyFileToLibertyInstallRoot("lib", "bundles/test.config.childalias.c_1.0.0.jar");

        testServer.startServer();
        //make sure the URL is available
        assertNotNull(testServer.waitForStringInLog("CWWKT0016I.*" + CONTEXT_ROOT));
        assertNotNull(testServer.waitForStringInLog("CWWKF0011I"));
    }

    @AfterClass
    public static void shutdown() throws Exception {
        testServer.stopServer();
        testServer.deleteFileFromLibertyInstallRoot("lib/features/childAliasTest-1.0.mf");
        testServer.deleteFileFromLibertyInstallRoot("lib/features/childAliasTestB-1.0.mf");
        testServer.deleteFileFromLibertyInstallRoot("lib/features/childAliasTestC-1.0.mf");
        testServer.deleteFileFromLibertyInstallRoot("lib/test.config.childalias_1.0.0.jar");
        testServer.deleteFileFromLibertyInstallRoot("lib/test.config.childalias.b_1.0.0.jar");
        testServer.deleteFileFromLibertyInstallRoot("lib/test.config.childalias.c_1.0.0.jar");

        testServer.deleteFileFromLibertyInstallRoot("lib/features/configfatlibertyinternals-1.0.mf");
    }

    @Test
    public void testChildAlias1() throws Exception {
        test(testServer);
    }

    @Test
    public void testChildAlias2() throws Exception {
        test(testServer);
    }

    @Test
    public void testChildAliasSingleton1() throws Exception {
        test(testServer);
    }

    @Test
    public void testChildAliasSingleton2() throws Exception {
        test(testServer);
    }

    @Test
    public void testBundleOrdering1() throws Exception {
        // Because this test ensures bundle ordering with config elements defined 
        // in different bundles, a fresh start of the server with the config is needed
        // so that this test can run in any order while simulating bundle start ordering.
        testServer.stopServer();
        testServer.setServerConfigurationFile("childalias/server.xml");
        testServer.startServer();

        testServer.setMarkToEndOfLog();
        testServer.setServerConfigurationFile("childalias/serverB.xml");
        testServer.waitForConfigUpdateInLogUsingMark(null);
        test(testServer);
    }

    @Test
    public void testBundleOrdering2() throws Exception {
        // Because this test ensures bundle ordering with config elements defined 
        // in different bundles, a fresh start of the server with the config is needed
        // so that this test can run in any order while simulating bundle start ordering.
        testServer.stopServer();
        testServer.setServerConfigurationFile("childalias/serverB.xml");
        testServer.startServer();

        testServer.setMarkToEndOfLog();
        testServer.setServerConfigurationFile("childalias/serverC.xml");
        testServer.waitForConfigUpdateInLogUsingMark(null);
        test(testServer);
    }

    @Test
    public void testBundleOrderingAliasConflict() throws Exception {
        // Because this test ensures bundle ordering with config elements defined 
        // in different bundles, a fresh start of the server with the config is needed
        // so that this test can run in any order while simulating bundle start ordering.
        testServer.stopServer();
        testServer.setServerConfigurationFile("childalias/serverB.xml");
        testServer.startServer();

        testServer.setMarkToEndOfLog();
        testServer.setServerConfigurationFile("childalias/serverC.xml");
        testServer.waitForConfigUpdateInLogUsingMark(null);
        test(testServer);
    }

    @Test
    public void testRemoveChild() throws Exception {
        testServer.setMarkToEndOfLog();
        testServer.setServerConfigurationFile("childalias/serverC2.xml");
        testServer.waitForConfigUpdateInLogUsingMark(null);
        test(testServer);
    }

    @Test
    public void testAddNewChild() throws Exception {
        testServer.setMarkToEndOfLog();
        testServer.setServerConfigurationFile("childalias/serverC3.xml");
        testServer.waitForConfigUpdateInLogUsingMark(null);
        test(testServer);
    }

    @Test
    public void testUpdateChild() throws Exception {
        testServer.setMarkToEndOfLog();
        testServer.setServerConfigurationFile("childalias/serverC4.xml");
        testServer.waitForConfigUpdateInLogUsingMark(null);
        test(testServer);
    }

    @Test
    public void testRemoveSingletonChild() throws Exception {
        testServer.setMarkToEndOfLog();
        testServer.setServerConfigurationFile("childalias/serverC5.xml");
        testServer.waitForConfigUpdateInLogUsingMark(null);
        test(testServer);
    }

    @Test
    public void testAddNewSingletonChild() throws Exception {
        testServer.setMarkToEndOfLog();
        testServer.setServerConfigurationFile("childalias/serverC6.xml");
        testServer.waitForConfigUpdateInLogUsingMark(null);
        test(testServer);
    }

    @Test
    public void testUpdateSingletonChild() throws Exception {
        testServer.setMarkToEndOfLog();
        testServer.setServerConfigurationFile("childalias/serverC7.xml");
        testServer.waitForConfigUpdateInLogUsingMark(null);
        test(testServer);
    }

    private void test(LibertyServer server) throws Exception {
        HttpURLConnection con = null;
        try {
            URL url = new URL("http://" + server.getHostname() + ":" + server.getHttpDefaultPort() +
                              CONTEXT_ROOT + "/child-alias-test?" + "testName=" + testName);
            con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("GET");
            InputStream is = con.getInputStream();
            assertNotNull(is);

            String output = read(is);
            System.out.println(output);
            assertTrue(output, output.trim().startsWith("OK"));
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private static String read(InputStream in) throws IOException {
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }
        return builder.toString();
    }

}
