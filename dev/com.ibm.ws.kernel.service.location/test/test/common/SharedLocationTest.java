/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change activity:
 *
 * Issue       Date        Name      Description
 * ----------- ----------- --------- ------------------------------------
 *                                   Initial version
 */
package test.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.utils.Utils;

import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.kernel.service.location.WsResource;

/**
 *
 */
@RunWith(JMock.class)
public class SharedLocationTest {
    /**
     * Test data directory: note the space! always test paths with spaces. Dratted
     * windows.
     */
    public static final String TEST_DATA_DIR = "../com.ibm.ws.kernel.service_test/unittest/test data";

    static SharedOutputManager outputMgr;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // make stdout/stderr "quiet"-- no output will show up for test
        // unless one of the copy methods or documentThrowable is called
        outputMgr = SharedOutputManager.getInstance();
        outputMgr.logTo(Utils.TEST_DATA);
        outputMgr.captureStreams();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // Make stdout and stderr "normal"
        outputMgr.restoreStreams();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        SharedLocationManager.resetWsLocationAdmin();
    }

    /**
     * Test method for {@link test.common.SharedLocationManager#createImageLocations(java.lang.String)} .
     */
    @Test
    public void testCreateImageLocationsString() {
        final String m = "testCreateImageLocationsString";

        try {
            SharedLocationManager.createImageLocations("com.ibm.ws.kernel.service.location/bin_test");
            WsLocationAdmin locSvc = (WsLocationAdmin) SharedLocationManager.getLocationInstance();

            String installDir = System.getProperty("install.dir");
            if (installDir == null || installDir.length() <= 0)
                throw new Exception("installDir system property not set by environment or shared location manager");
            installDir = new File(installDir).getName();

            WsResource cfgRoot = locSvc.resolveResource("server.xml");
            assertTrue(cfgRoot.exists());

            URI uri = cfgRoot.toExternalURI();
            WsResource serverCfgRes = locSvc.getServerResource("server.xml");
            File serverCfg = new File(serverCfgRes.toExternalURI());

            assertEquals("server.xml parent should be the test profile", uri, serverCfg.toURI().normalize());

            WsResource bootRes = locSvc.getServerResource("bootstrap.properties");
            File bootProps = new File(bootRes.toExternalURI());
            assertTrue("bootstrap.properties should be found in server directory", bootProps.exists());

            WsResource server = locSvc.getServerResource(null);
            File serverDir = new File(server.toExternalURI());

            File serversDir = serverDir.getParentFile();
            File usrDir = serversDir.getParentFile();
            File libertyDir = usrDir.getParentFile();

            assertEquals("bootstrap.properties parent should be the test profile", "com.ibm.ws.kernel.service_test", bootProps.getParentFile().getName());
            assertEquals("parent of bootstrap.properties should be server directory", serverDir, bootProps.getParentFile());
            assertEquals("server parent should be servers", "servers", serversDir.getName());
            assertEquals("parent of servers dir should be usr", "usr", usrDir.getName());
            assertEquals("parent of usr dir should be '" + installDir + "'", installDir, libertyDir.getName());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }

    final Mockery context = new org.jmock.integration.junit4.JUnit4Mockery();

    @Test
    public void testCreateDefaultLocations() {
        final String m = "testCreateDefaultLocations";

        File tmpRoot = new File(TEST_DATA_DIR, "fullDir");

        try {
            tmpRoot.mkdirs();
            SharedLocationManager.createDefaultLocations(tmpRoot.getAbsolutePath());
            System.out.println(SharedLocationManager.debugConfiguredLocations());
        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        } finally {
            Utils.recursiveClean(tmpRoot);
        }
    }
}
