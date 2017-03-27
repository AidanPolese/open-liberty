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
package com.ibm.ws.kernel.boot.commandline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.ProgramOutput;
import componenttest.common.apiservices.Bootstrap;
import componenttest.topology.impl.LibertyFileManager;
import componenttest.topology.utils.LibertyServerUtils;

/**
 *
 */
public class CreateCommandTest {

    private final static double javaLevel = Double.parseDouble(System.getProperty("java.specification.version"));

    private final static String serverName = "com.ibm.ws.kernel.boot.commandline.CreateCommandTest";
    private static Bootstrap bootstrap;
    private static Machine machine;
    private static String installPath;;
    private static String defaultServerPath;;

    @BeforeClass
    public static void setup() throws Exception {
        bootstrap = Bootstrap.getInstance();
        machine = LibertyServerUtils.createMachine(bootstrap);
        installPath = LibertyFileManager.getInstallPath(bootstrap);
        defaultServerPath = installPath + "/usr/servers/" + serverName;
    }

    @Before
    public void cleanupBeforeRun() throws Exception {
        // since we are not using the normal LibertyServer class for this server,
        // we need to make sure to explicitly clean up.  We do this before running
        // the test in order to preserve the contents on disk.
        if (LibertyFileManager.libertyFileExists(machine, defaultServerPath)) {
            LibertyFileManager.deleteLibertyDirectoryAndContents(machine, defaultServerPath);
        }
    }

    @Test
    public void testIsServerEnvCreated() throws Exception {

        ProgramOutput po = LibertyServerUtils.executeLibertyCmd(bootstrap, "server", "create", serverName);
        assertEquals("Unexpected return code from server create command", 0, po.getReturnCode());

        // check that server directory was created
        assertTrue("Expected server directory to exist at " + defaultServerPath + ", but does not", LibertyFileManager.libertyFileExists(machine, defaultServerPath));

        // check that server.xml exists
        String serverXmlPath = defaultServerPath + "/server.xml";
        assertTrue("Expected server.xml file to exist at " + serverXmlPath + ", but does not", LibertyFileManager.libertyFileExists(machine, serverXmlPath));

        // if we are running in a JVM that is version 1.8 or higher, we also need to check for the server.env file
        if (javaLevel >= 1.8) {
            String serverEnvPath = defaultServerPath + "/server.env";
            assertTrue("Expected server.env file to exist at " + serverEnvPath + ", but does not", LibertyFileManager.libertyFileExists(machine, serverEnvPath));
        }

    }
}
