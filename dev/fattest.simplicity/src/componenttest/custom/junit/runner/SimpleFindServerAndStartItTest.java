/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package componenttest.custom.junit.runner;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * This test is designed to be a way of accessing the
 * #FeatureDoesNotStartOnLowVersionsTestStub without instrumentation or JUnit magic. It's useful for projects which are compiled with
 * Java 7 (or higher) syntax.
 */
public class SimpleFindServerAndStartItTest extends FeatureDoesNotStartOnLowJavaVersionsTestStub {

    private static final String SYNTHETIC_SERVER = "syntheticServer";

    private static final String SYNTHETIC_SERVER_XML = "<server>" +
                                                       "<featureManager>" +
                                                       "  <feature>FEATURE_UNDER_TEST</feature>" +
                                                       "  <feature>componenttest-1.0</feature>" +
                                                       "</featureManager>" +
                                                       "<include location=\"../fatTestPorts.xml\"/>" +
                                                       "</server>";

    private static final String BOOTSTRAP_PROPERTIES = "bootstrap.include=../testports.properties";

    public SimpleFindServerAndStartItTest() {
        super(SimpleFindServerAndStartItTest.class.getName());
    }

    @BeforeClass
    public static void setUp() throws Exception {
        String feature = FeatureFilter.FEATURE_UNDER_TEST;
        File serverFolder = new File("publish/servers/" + SYNTHETIC_SERVER);
        if (!serverFolder.exists()) {
            serverFolder.mkdir();
        }
        File serverXML = new File(serverFolder, "server.xml");
        if (!serverXML.exists()) {
            String xml = SYNTHETIC_SERVER_XML.replace("FEATURE_UNDER_TEST", feature);
            FileOutputStream fos = new FileOutputStream(serverXML);
            byte[] bytes = xml.getBytes("UTF-8");
            try {
                fos.write(bytes, 0, bytes.length);
                fos.flush();
            } finally {
                fos.close();
            }
        }
        File bootstrap = new File(serverFolder, "bootstrap.properties");
        if (!bootstrap.exists()) {
            FileOutputStream fos = new FileOutputStream(bootstrap);
            byte[] bytes = BOOTSTRAP_PROPERTIES.getBytes("UTF-8");
            try {
                fos.write(bytes, 0, bytes.length);
                fos.flush();
            } finally {
                fos.close();
            }
        }

        LibertyServer server = LibertyServerFactory.getLibertyServer(SYNTHETIC_SERVER);

        server.startServer();

    }

    @AfterClass
    public static void tearDown() throws Exception {
        LibertyServer server = LibertyServerFactory.getExistingLibertyServer(SYNTHETIC_SERVER);
        server.stopServer(".*CWWKG0059E.*");
    }

    @Override
    protected String getWhereThisCameFromMessage() {
        return "(If you're wondering where this test came from, it is being run instead of the usual test suite, probably because the usual tests require a level of Java higher than the current one .\n";
    }

}
