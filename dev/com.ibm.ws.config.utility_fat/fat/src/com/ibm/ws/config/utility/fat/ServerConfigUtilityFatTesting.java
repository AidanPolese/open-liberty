package com.ibm.ws.config.utility.fat;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.log.Log;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;

public class ServerConfigUtilityFatTesting extends TestRepository {

    private static final Class<?> c = ServerConfigUtilityFatTesting.class;

    static Machine machine;
    String installRoot = server.getInstallRoot();
    String controllerPort = System.getProperty("HTTP_default.secure");
    String adminUser = "user001";
    String adminPassword = "pass001";
    String keystorePassword = "passwordKeystore";
    String encodingType = "aes";
    String configFilePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "configFile.txt";

    private static final String SLASH = System.getProperty("file.separator");
    public static LibertyServer server = TestRepository.server;

    @BeforeClass
    public static void setUp() throws Exception {
        Assume.assumeTrue(new TestRepository().testRepositoryConnection());
        final String methodName = "setUp";
        Log.entering(c, methodName);

        machine = server.getMachine();
        Log.info(c, methodName, "Starting Server");
        server.startServer();

        // Wait for the smarter planet message
        assertNotNull("The smarter planet message did not get printed", server.waitForStringInLog("CWWKF0011I"));
        Log.exiting(c, methodName);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (server.isStarted()) {
            server.stopServer();
        }
    }

    public static String readFile(String path) throws IOException {
        return FileUtils.readFileToString(new File(path)).trim();
    }

    @Mode(TestMode.LITE)
    @Test
    public void testConfigUtilityUseLocalFile() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        final String methodName = "testConfigUtilityUseLocalFile";

        Log.entering(c, methodName);
        Properties env = new Properties();

        String p = server.getServerRoot() + SLASH + "files" + SLASH + "remoteJMXConnection.xml";
        Log.info(c, methodName, "Running configUtility --useLocalFile");
        Log.info(c, methodName, "Path to --useLocalFile" + p);
        ProgramOutput po = machine.execute(server.getInstallRoot() + "/bin/configUtility",
                                           new String[] {
                                                         "install",
                                                         "--useLocalFile=" + p,
                                                         "--VadminUser=" + adminUser,
                                                         "--VwritePath=path1",
                                                         "--encoding=" + encodingType,
                                                         "--key=" + "123",
                                                         "--VhttpPort=8081"
                                           },
                                           installRoot,
                                           env);

        Log.info(c, methodName, "Executed configUtility command:" + po.getCommand());
        Log.info(c, methodName, "--useLocalFile result:\n" + po.getStdout());
        assertEquals("ConfigUtility task should complete with return code as 0.", 0, po.getReturnCode());

        assertTrue("Fail: did not match expected username:", po.getStdout().contains("userName=\"" + adminUser + "\""));
        assertTrue("Fail: did not match expected writePath snippet:<writeDir>path1</writeDir>", po.getStdout().contains("<writeDir>path1</writeDir>"));
        assertTrue("Fail: did not match expected httpPort snippet:", po.getStdout().toString().contains("httpPort=\"8081\""));

        Log.exiting(c, methodName);
    }

    @Mode(TestMode.LITE)
    @Test
    public void testConfigUtilityCheckErrorCodes() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        final String methodName = "testConfigUtilityCheckErrorCodes";

        Log.entering(c, methodName);
        Properties env = new Properties();

        String p = server.getServerRoot() + SLASH + "files" + SLASH + "remoteJMXConnection.xml";
        Log.info(c, methodName, "Running configUtility --useLocalFile");
        Log.info(c, methodName, "Path to --useLocalFile" + p);

        ProgramOutput po = machine.execute(server.getInstallRoot() + "/bin/configUtility",
                                           new String[] {
                                                         "install",
                                                         "--useLocalFile=" + p + "abc",
                                                         "--VadminUser=" + adminUser,
                                                         "--VwritePath=path1",
                                                         "--encoding=" + encodingType,
                                                         "--key=" + "123",
                                                         "--VhttpPort=8081"
                                           },
                                           installRoot,
                                           env);

        Log.info(c, methodName, "Executed configUtility command:" + po.getCommand());
        Log.info(c, methodName, "--useLocalFile result:\n" + po.getStdout());
        assertEquals("ConfigUtility task should complete with return code as 255.", 255, po.getReturnCode());

        Log.exiting(c, methodName);
    }

    @Mode(TestMode.LITE)
    @Test
    public void testInvalidValArgsConfigUtility() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        final String methodName = "testInvalidValArgsConfigUtility";

        Log.entering(c, methodName);
        Properties env = new Properties();

        String p = server.getServerRoot() + SLASH + "files" + SLASH + "remoteJMXConnection.xml";
        Log.info(c, methodName, "Running configUtility --useLocalFile");
        Log.info(c, methodName, "Path to --useLocalFile" + p);

        ProgramOutput po = machine.execute(server.getInstallRoot() + "/bin/configUtility",
                                           new String[] {
                                                         "install",
                                                         "--useLocalFile=" + p,
                                                         "--Vinvalid=" + adminUser,
                                                         "--VwritePath=path1",
                                                         "--encoding=" + encodingType,
                                                         "--key=" + "123",
                                                         "--VhttpPort=8081"
                                           },
                                           installRoot,
                                           env);

        Log.info(c, methodName, "Executed configUtility command:" + po.getCommand());
        Log.info(c, methodName, "--useLocalFile result:\n" + po.getStdout());
        assertEquals("ConfigUtility task should complete with return code as 20.", 20, po.getReturnCode());

        Log.exiting(c, methodName);
    }

    @Mode(TestMode.LITE)
    @Test
    public void testEncodingConfigUtilityUseLocalFile() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        final String methodName = "testEncodingConfigUtilityUseLocalFile";

        Log.entering(c, methodName);
        Properties env = new Properties();

        Log.info(c, methodName, "Running configUtility --useLocalFile");

        String p = server.getServerRoot() + SLASH + "files" + SLASH + "remoteJMXConnection.xml";
        ProgramOutput po = machine.execute(server.getInstallRoot() + "/bin/configUtility",
                                           new String[] {
                                                         "install",
                                                         "--useLocalFile=" + p,
                                                         "--VadminUser=" + adminUser,
                                                         "--VwritePath=" + "path1",
                                                         "--VadminPassword=" + adminPassword,
                                                         "--VkeystorePassword=" + keystorePassword,
                                                         "--encoding=" + encodingType,
                                                         "--key=" + "123"
                                           },
                                           installRoot,
                                           env);

        Log.info(c, methodName, "Executed configUtility command:" + po.getCommand());
        Log.info(c, methodName, "ConfigUtility result:\n" + po.getStdout());
        assertEquals("ConfigUtility task should complete with return code as 0.", 0, po.getReturnCode());

        assertTrue("Fail: did not match expected username:", po.getStdout().contains("userName=\"" + adminUser + "\""));
        assertTrue("Fail: did not match expected writePath snippet:<writeDir>path1</writeDir>", po.getStdout().contains("<writeDir>path1</writeDir>"));
        assertFalse("Fail: adminPassword should not match generated password:",
                    po.getStdout().contains(adminPassword));
        assertTrue("Fail: did not match expected encodingType 'aes':",
                   po.getStdout().contains("userPassword=\"{aes}"));

        Log.exiting(c, methodName);
    }

    @Mode(TestMode.LITE)
    @Test
    public void testDefaultEncodingConfigUtility() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        final String methodName = "testDefaultEncodingConfigUtility";

        Log.entering(c, methodName);
        Properties env = new Properties();

        Log.info(c, methodName, "Running configUtility --useLocalFile");
        String p = server.getServerRoot() + SLASH + "files" + SLASH + "remoteJMXConnection.xml";
        ProgramOutput po = machine.execute(server.getInstallRoot() + "/bin/configUtility",
                                           new String[] {
                                                         "install",
                                                         "--useLocalFile=" + p,
                                                         "--VadminUser=" + adminUser,
                                                         "--VwritePath=" + "path1",
                                                         "--VadminPassword=" + adminPassword,
                                                         "--VkeystorePassword=" + keystorePassword
                                           },
                                           installRoot,
                                           env);

        Log.info(c, methodName, "Executed configUtility command:" + po.getCommand());
        Log.info(c, methodName, "ConfigUtility result:\n" + po.getStdout());
        assertEquals("ConfigUtility task should complete with return code as 0.", 0, po.getReturnCode());

        assertTrue("Fail: did not match expected username:", po.getStdout().contains("userName=\"" + adminUser + "\""));
        assertTrue("Fail: did not match expected writePath snippet:<writeDir>path1</writeDir>", po.getStdout().contains("<writeDir>path1</writeDir>"));
        assertFalse("Fail: adminPassword should not match generated password:",
                    po.getStdout().contains(adminPassword));
        assertTrue("Fail: did not match expected encodingType 'xor':userPassword=\"{xor}",
                   po.getStdout().contains("userPassword=\"{xor}"));

        Log.exiting(c, methodName);
    }

    @Mode(TestMode.LITE)
    @Test
    public void testFindAllConfigSnippets() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        final String methodName = "testFindAllConfigSnippets";

        Log.entering(c, methodName);
        Properties env = new Properties();

        Log.info(c, methodName, "Running configUtility find");

        ProgramOutput po = machine.execute(server.getInstallRoot() + "/bin/configUtility",
                                           new String[] { "find" },
                                           installRoot,
                                           env);

        Log.info(c, methodName, "Executed configUtility command:" + po.getCommand());

        assertFalse("FAIL: configUtility find should return all snippets:", po.getStdout().isEmpty());
        Log.exiting(c, methodName);
    }

    @Mode(TestMode.LITE)
    @Test
    public void testFindRelatedConfigSnippets() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        final String methodName = "testFindRelatedConfigSnippets";

        Log.entering(c, methodName);
        Properties env = new Properties();
        String arg = "security";

        Log.info(c, methodName, "Running configUtility find " + arg);

        ProgramOutput po = machine.execute(server.getInstallRoot() + "/bin/configUtility",
                                           new String[] { "find", arg },
                                           installRoot,
                                           env);

        Log.info(c, methodName, "Executed configUtility command:" + po.getCommand());

        assertFalse("FAIL: configUtility info should return snippets related to " + arg, po.getStdout().isEmpty());
        Log.exiting(c, methodName);
    }

    @Mode(TestMode.LITE)
    @Test
    public void testCreateConfigFileSnippets() throws Exception {
        Assume.assumeTrue(testRepositoryConnection());
        final String methodName = "testCreateConfigFileSnippets";

        Log.entering(c, methodName);
        Properties env = new Properties();

        Log.info(c, methodName, "Running configUtility -list");
        Log.info(c, methodName, "Create Config File in " + configFilePath);
        String p = server.getServerRoot() + SLASH + "files" + SLASH + "remoteJMXConnection.xml";
        ProgramOutput po = machine.execute(server.getInstallRoot() + "/bin/configUtility",
                                           new String[] {
                                                         "install",
                                                         "--useLocalFile=" + p,
                                                         "--VadminUser=" + adminUser,
                                                         "--VwritePath=" + "path1",
                                                         "--VadminPassword=" + adminPassword,
                                                         "--VkeystorePassword=" + keystorePassword,
                                                         "--createConfigFile=" + configFilePath
                                           },
                                           installRoot,
                                           env);

        Log.info(c, methodName, "Executed configUtility command:" + po.getCommand());
        Log.info(c, methodName, "ConfigFile result:\n" + po.getStdout());

        File configFile = new File(configFilePath);
        assertTrue("The configFile " + configFilePath + " was created.", configFile.exists());
        configFile.deleteOnExit();

        Log.exiting(c, methodName);
    }
}