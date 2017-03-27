/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package componenttest.topology.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.log.Log;

import componenttest.topology.impl.LibertyFileManager;
import componenttest.topology.impl.LibertyServer;

/**
 *
 */
public class CollectiveUtilities {
    private static final Class<?> c = CollectiveUtilities.class;

    private static boolean findMatchingLine(String output, String regex) {
        Pattern pattern = Pattern.compile(regex);
        for (String line : output.split("\\n")) {
            // Need to trim the output because pattern matcher does weird things with newlines
            if (pattern.matcher(line.trim()).matches()) {
                Log.info(c, "findMatchingLine", "Found line matching regex " + regex + ": " + line);
                return true;
            }
        }

        Log.info(c, "findMatchingLine", "Did not find line matching " + regex);
        return false;
    }

    /**
     * Creates the initial collective controller configuration.
     *
     * @param machine
     * @param server
     * @param keystorePassword
     * @throws Exception
     */
    public static void create(Machine machine, LibertyServer server, String keystorePassword) throws Exception {
        create(machine, server, keystorePassword, (String[]) null);
    }

    /**
     * Creates the initial collective controller configuration
     *
     * @param machine
     * @param server
     * @param keystorePassword
     * @param optArgs optional arguments that can be passed
     * @throws Exception
     */
    public static void create(Machine machine, LibertyServer server, String keystorePassword, String... optArgs) throws Exception {
        String serverName = server.getServerName();
        String installRoot = server.getInstallRoot();

        boolean hasConfigArg = hasCreateConfigArg(optArgs);
        cleanupStaleFiles(machine, server);

        Log.info(c, "createCollectiveController", "Running collective create " + serverName);
        Properties env = new Properties();
        String hostname = machine.getHostname();

        String[] args = buildCreateArgs(serverName, keystorePassword, hostname, optArgs);

        env.put("JAVA_HOME", server.getMachineJavaJDK());
        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           args,
                                           installRoot,
                                           env);
        checkForSuccess(po, hasConfigArg);

    }

    private static void checkForSuccess(ProgramOutput output, boolean hasConfigArg) {
        String stdout = output.getStdout();
        int rc = output.getReturnCode();
        String stderr = output.getStderr();

        Log.info(c, "createCollectiveController", "Command Result (getCommand):" + output.getCommand());
        Log.info(c, "createCollectiveController", "Command Result (getReturnCode):" + rc);
        Log.info(c, "createCollectiveController", "Command Result (stdout):\n" + stdout);
        Log.info(c, "createCollectiveController", "Command Result (stderr):\n" + stderr);

        if (hasConfigArg) {
            assertEquals("Error creating the collective configuration. A return code of '0' was expected but return code" + rc + " was received instead\n" +
                         "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                         "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                         "stdout:\n" + stdout + "\n" +
                         "stderr:\n" + stderr, 0, rc);
            assertTrue("Error creating the collective configuration. Expected to find the string 'Successfully set up collective controller configuration' \n" +
                       "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                       "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                       "stdout:\n" + stdout + "\n" +
                       "stderr:\n" + stderr, stdout.contains("Successfully set up collective controller configuration"));
        } else {
            assertTrue("Error creating the collective configuration. create should report server.xml config but none was found.\n" +
                       "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                       "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                       "stdout:\n" + stdout + "\n" +
                       "stderr:\n" + stderr,
                       findMatchingLine(stdout, ".*id=\"serverIdentity\".*"));

            assertTrue("Error creating the collective configuration. create should produce <keyStore> sample but none was found.\n" +
                       "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                       "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                       "stdout:\n" + stdout + "\n" +
                       "stderr:\n" + stderr,
                       findMatchingLine(stdout, ".*<keyStore.*"));
        }
    }

    /**
     * Checks if the Authentication Plugin has started on the controller side.
     *
     * @param controller
     * @param searchTimeOut
     */
    public static void controllerAuthenticationPluginStarted(LibertyServer controller, int searchTimeOut) {
        assertNotNull("FAIL:" + controller.getServerName() + " CollectiveAuthenticationPluginImpl reference was not set",
                      controller.waitForStringInLog("CWWKS1123I:.*CollectiveAuthenticationPluginImpl", searchTimeOut));
    }

    /**
     * Checks if the Authentication Plugin has started on the member side.
     *
     * @param controller
     * @param searchTimeOut
     */
    public static void memberAuthenticationPluginStarted(LibertyServer member, int searchTimeOut) {
        assertNotNull("FAIL:" + member.getServerName() + " MemberCollectiveAuthenticationPlugin reference was not set",
                      member.waitForStringInLog("CWWKS1123I:.*MemberCollectiveAuthenticationPlugin", searchTimeOut));

    }

    /**
     * Checks for the --createConfigFile argument
     *
     * @param args
     * @return
     */
    private static boolean hasCreateConfigArg(String[] args) {
        if (args == null)
            return false;

        for (String arg : args)
            if (arg.contains("--createConfigFile="))
                return true;

        return false;
    }

    /**
     * Delete old keystore files
     *
     * @param machine
     * @param server
     * @throws Exception
     */
    private static void cleanupStaleFiles(Machine machine, LibertyServer server) throws Exception {
        String collectiveResources = server.getServerRoot() + "/resources/collective/";
        Log.info(c, "createCollectiveController", "Removing generated resources: " + collectiveResources);
        LibertyFileManager.deleteLibertyDirectoryAndContents(machine, collectiveResources);

        String keyJKS = server.getServerRoot() + "/resources/security/key.jks";
        Log.info(c, "createCollectiveController", "Removing generated resource: " + keyJKS);
        LibertyFileManager.deleteLibertyFile(machine, keyJKS);

        String trustJKS = server.getServerRoot() + "/resources/security/trust.jks";
        Log.info(c, "createCollectiveController", "Removing generated resource: " + trustJKS);
        LibertyFileManager.deleteLibertyFile(machine, trustJKS);
    }

    /** Creates the arguments that get passed to the command line for the create cmd */
    private static String[] buildCreateArgs(String serverName, String keystorePassword, String hostname, String... optArgs) {
        //all the optional args + 'create' command + serverName
        String[] args = (optArgs == null) ? new String[4] : new String[4 + optArgs.length];
        args[0] = "create";
        args[1] = serverName;
        args[2] = "--keystorePassword=" + keystorePassword;
        args[3] = "--hostname=" + hostname;
        if (optArgs != null)
            System.arraycopy(optArgs, 0, args, 4, optArgs.length);
        return args;
    }

    /**
     * Joins the server to the collective as a member.
     *
     * @param machine
     * @param server
     * @param controllerHost
     * @param controllerPort
     * @param controllerUser
     * @param controllerPassword
     * @param keystorePassword
     * @return Returns the return code of join command. Please refer to join command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int join(Machine machine, LibertyServer server,
                           String controllerHost, int controllerPort,
                           String controllerUser, String controllerPassword,
                           String keystorePassword) throws Exception {
        return join(machine, server, controllerHost, controllerPort, controllerUser, controllerPassword, keystorePassword, false, true);
    }

    /**
     * Joins the server to the collective as a member.
     *
     * @param machine
     * @param server
     * @param controllerHost
     * @param controllerPort
     * @param controllerUser
     * @param controllerPassword
     * @param keystorePassword
     * @param shouldSucceed Boolean to indicate whether or not the join is expected to succeed. If true, will validate command output (and failed validation results in Junit
     *            failures). If false, no validation is performed.
     * @return Returns the return code of join command. Please refer to join command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int join(Machine machine, LibertyServer server,
                           String controllerHost, int controllerPort,
                           String controllerUser, String controllerPassword,
                           String keystorePassword, boolean genDeployVars, boolean shouldSucceed) throws Exception {
        String serverName = server.getServerName();
        String installRoot = server.getInstallRoot();

        String collectiveResources = server.getServerRoot() + "/resources/collective/";
        Log.info(c, "join", "Removing generated resources: " + collectiveResources);
        LibertyFileManager.deleteLibertyDirectoryAndContents(machine, collectiveResources);

        String keyJKS = server.getServerRoot() + "/resources/security/key.jks";
        Log.info(c, "join", "Removing generated resource: " + keyJKS);
        LibertyFileManager.deleteLibertyFile(machine, keyJKS);

        String trustJKS = server.getServerRoot() + "/resources/security/trust.jks";
        Log.info(c, "join", "Removing generated resource: " + trustJKS);
        LibertyFileManager.deleteLibertyFile(machine, trustJKS);

        Log.info(c, "join", "Running collective join " + serverName);
        Properties env = new Properties();
        env.put("JVM_ARGS", "-Dcom.ibm.websphere.collective.utility.autoAcceptCertificates=true");
        env.put("JAVA_HOME", server.getMachineJavaJDK());

        List<String> args = new ArrayList<String>();
        args.add("join");
        args.add(serverName);
        args.add("--host=" + controllerHost);
        args.add("--port=" + controllerPort);
        args.add("--user=" + controllerUser);
        args.add("--password=" + controllerPassword);
        args.add("--keystorePassword=" + keystorePassword);
        args.add("--hostName=" + server.getHostname());
        if (genDeployVars) {
            args.add("--genDeployVariables");
        }

        String arglist = "";
        for (String arg : args) {
            arglist += arg + " ";
        }
        String command = installRoot + "/bin/collective ";
        Log.info(c, "join", "The command line used is approximately: " + command + " " + arglist);

        int rc = -1;
        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           args.toArray(new String[] {}),
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        rc = po.getReturnCode();
        Log.info(c, "join", "Command Result (getCommand):" + po.getCommand()); // too bad it doesn't have the arguments.
        Log.info(c, "join", "Command Result (getReturnCode):" + rc);
        Log.info(c, "join", "Command Result (stdout):\n" + stdout);
        Log.info(c, "join", "Command Result (stderr):\n" + po.getStderr());

        if (stdout.contains("IOException while invoking the MBean: java.net.SocketTimeoutException: Read timed out")) {
            int retryCount = 5;
            boolean commandSuccess = false;
            for (int i = 0; i < retryCount; i++) {
                if (commandSuccess)
                    break;
                // re do it again for 5 times if keep failing then something really bad going on with the machine/network
                Log.info(c, "join", "Sleeping for half minute after getting SocketTimeoutException");
                Thread.sleep(10000);
                Log.info(c, "join", "Retry number " + i + " for join as it was failing with SocketTimeoutException");
                po = machine.execute(installRoot + "/bin/collective",
                                     args.toArray(new String[] {}),
                                     installRoot,
                                     env);

                stdout = po.getStdout();
                Log.info(c, "join", "Command Result (getCommand):" + po.getCommand()); // too bad it doesn't have the arguments.
                Log.info(c, "join", "Command Result (getReturnCode):" + po.getReturnCode());
                Log.info(c, "join", "Command Result (stdout):\n" + stdout);
                Log.info(c, "join", "Command Result (stderr):\n" + po.getStderr());

                if (!stdout.contains("IOException while invoking the MBean: java.net.SocketTimeoutException: Read timed out")) {
                    commandSuccess = true;
                }
            }

            // if already retry and not getting SocketTimeoutException but getting already member, that means join is successful
            if (stdout.contains("The specified server .* already appears to be a member.")) {
                rc = 0;
                //If the server is already a member, we're not going to get the stdout that's checked for below, so
                //return the result
                return rc;
            }
        }

        if (shouldSucceed) {
            assertTrue("Error joining the collective. join should report server.xml config but none was found.\n" +
                       "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                       "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                       "stdout:\n" + stdout + "\n" +
                       "stderr:\n" + po.getStderr(),
                       findMatchingLine(stdout, ".*id=\"serverIdentity\".*"));

            assertTrue("Error joining the collective. join should produce <keyStore> sample but none was found.\n" +
                       "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                       "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                       "stdout:\n" + stdout + "\n" +
                       "stderr:\n" + po.getStderr(),
                       findMatchingLine(stdout, ".*<keyStore.*"));
        }

        return rc;
    }

    /**
     * Replicates the collective configuration of the target controller.
     * Hardcodes the hostName to localhost.
     *
     * @param machine
     * @param server
     * @param controllerHost
     * @param controllerPort
     * @param controllerUser
     * @param controllerPassword
     * @param keystorePassword
     * @throws Exception
     */
    public static void replicate(Machine machine, LibertyServer server,
                                 String controllerHost, int controllerPort,
                                 String controllerUser, String controllerPassword,
                                 String keystorePassword) throws Exception {
        String serverName = server.getServerName();
        String installRoot = server.getInstallRoot();

        String collectiveResources = server.getServerRoot() + "/resources/collective/";
        Log.info(c, "replicate", "Removing generated resources: " + collectiveResources);
        LibertyFileManager.deleteLibertyDirectoryAndContents(machine, collectiveResources);

        String keyJKS = server.getServerRoot() + "/resources/security/key.jks";
        Log.info(c, "replicate", "Removing generated resource: " + keyJKS);
        LibertyFileManager.deleteLibertyFile(machine, keyJKS);

        String trustJKS = server.getServerRoot() + "/resources/security/trust.jks";
        Log.info(c, "replicate", "Removing generated resource: " + trustJKS);
        LibertyFileManager.deleteLibertyFile(machine, trustJKS);

        Log.info(c, "replicate", "Running collective replicate " + serverName);
        Properties env = new Properties();
        env.put("JVM_ARGS", "-Dcom.ibm.websphere.collective.utility.autoAcceptCertificates=true");
        env.put("JAVA_HOME", server.getMachineJavaJDK());
        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           new String[] { "replicate",
                                                          serverName,
                                                          "--host=" + controllerHost,
                                                          "--port=" + controllerPort,
                                                          "--user=" + controllerUser,
                                                          "--password=" + controllerPassword,
                                                          "--keystorePassword=" + keystorePassword,
                                                          "--hostName=localhost" },
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        Log.info(c, "replicate", "Command Result (getCommand):" + po.getCommand());
        Log.info(c, "replicate", "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, "replicate", "Command Result (stdout):\n" + stdout);
        Log.info(c, "replicate", "Command Result (stderr):\n" + po.getStderr());

        assertTrue("Error adding the controller to the collective. replicate should report server.xml config but none was found.\n" +
                   "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                   "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                   "stdout:\n" + stdout + "\n" +
                   "stderr:\n" + po.getStderr(),
                   findMatchingLine(stdout, ".*id=\"serverIdentity\".*"));

        assertTrue("Error adding the controller to the collective. replicate should produce <keyStore> sample but none was found.\n" +
                   "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                   "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                   "stdout:\n" + stdout + "\n" +
                   "stderr:\n" + po.getStderr(),
                   findMatchingLine(stdout, ".*<keyStore.*"));
    }

    /**
     * Removes the specified server from the collective managed by the specified controller.
     *
     * @param machine
     * @param libertyServer
     * @param controllerHost
     * @param controllerPort
     * @param controllerUser
     * @param controllerPassword
     * @param shouldUnregister set to {@code true} if the action should unregister
     * @param shouldRemoveFiles set to {@code true} if the action should remove files
     * @return Returns the return code of remove command. Please refer to remove command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int remove(Machine machine, LibertyServer server,
                             String controllerHost, int controllerPort,
                             String controllerUser, String controllerPassword,
                             boolean shouldUnregister, boolean shouldRemoveFiles) throws Exception {
        String serverName = server.getServerName();
        String installRoot = server.getInstallRoot();
        String userDir = server.getUserDir();
        String hostName = server.getHostname();

        Log.info(c, "remove", "Running collective remove " + serverName);
        Properties env = new Properties();
        env.put("JVM_ARGS", "-Dcom.ibm.websphere.collective.utility.autoAcceptCertificates=true");
        env.put("JAVA_HOME", server.getMachineJavaJDK());
        if (server.isCustomUserDir())
            env.put("WLP_USER_DIR", userDir);
        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           new String[] { "remove",
                                                          serverName,
                                                          "--host=" + controllerHost,
                                                          "--port=" + controllerPort,
                                                          "--user=" + controllerUser,
                                                          "--password=" + controllerPassword,
                                                          "--hostName=" + hostName,
                                                          "--removeDeployVariables" },
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        Log.info(c, "remove", "Command Result (getCommand):" + po.getCommand());
        Log.info(c, "remove", "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, "remove", "Command Result (stdout):\n" + stdout);
        Log.info(c, "remove", "Command Result (stderr):\n" + po.getStderr());

        if (shouldUnregister) {
            assertTrue("Error removing the server from the collective. The script did not report that the server was unregistered.\n" +
                       "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                       "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                       "stdout:\n" + stdout + "\n" +
                       "stderr:\n" + po.getStderr(),
                       findMatchingLine(stdout, ".*Server .* successfully unregistered..*"));
        }
        if (shouldRemoveFiles) {
            assertTrue("Error removing the server from the collective. The script did not report that the files were removed.\n" +
                       "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                       "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                       "stdout:\n" + stdout + "\n" +
                       "stderr:\n" + po.getStderr(),
                       findMatchingLine(stdout, ".*The resources for collective membership were successfully removed..*"));
        }
        return po.getReturnCode();
    }

    /**
     * Runs the addReplica command.
     *
     * @param machine
     * @param server
     * @param endpoint
     * @param controllerHost
     * @param controllerPort
     * @param controllerUser
     * @param controllerPassword
     * @throws Exception
     */
    public static int addReplica(Machine machine, LibertyServer server, String endpoint,
                                 String controllerHost, int controllerPort,
                                 String controllerUser, String controllerPassword) throws Exception {
        return addReplica(machine, server, endpoint, controllerHost, controllerPort, controllerUser, controllerPassword, true);
    }

    public static int addReplica(Machine machine, LibertyServer server, String endpoint,
                                 String controllerHost, int controllerPort,
                                 String controllerUser, String controllerPassword, boolean expectRC0) throws Exception {
        String serverName = server.getServerName();
        String installRoot = server.getInstallRoot();

        Log.info(c, "addReplica", "Running collective addReplica " + serverName);
        Properties env = new Properties();
        env.put("JAVA_HOME", server.getMachineJavaJDK());
        // This exercises --autoAcceptCertificates via argument rather than the property
        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           new String[] { "addReplica",
                                                          endpoint,
                                                          "--host=" + controllerHost,
                                                          "--port=" + controllerPort,
                                                          "--user=" + controllerUser,
                                                          "--password=" + controllerPassword,
                                                          "--autoAcceptCertificates"
                                           },
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        Log.info(c, "addReplica", "Command Result (getCommand):" + po.getCommand());
        Log.info(c, "addReplica", "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, "addReplica", "Command Result (stdout):\n" + stdout);
        Log.info(c, "addReplica", "Command Result (stderr):\n" + po.getStderr());

        if (expectRC0) {
            assertTrue("Error running addReplica command.\n" +
                       "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                       "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                       "stdout:\n" + stdout + "\n" +
                       "stderr:\n" + po.getStderr(),
                       findMatchingLine(stdout, ".*Successfully added replica endpoint.*"));
        }
        return po.getReturnCode();
    }

    /**
     * Runs the removeReplica command.
     *
     * @param machine
     * @param server
     * @param endpoint
     * @param controllerHost
     * @param controllerPort
     * @param controllerUser
     * @param controllerPassword
     * @throws Exception
     */
    public static int removeReplica(Machine machine, LibertyServer server, String endpoint,
                                    String controllerHost, int controllerPort,
                                    String controllerUser, String controllerPassword) throws Exception {
        return removeReplica(machine, server, endpoint, controllerHost, controllerPort, controllerUser, controllerPassword, true);
    }

    public static int removeReplica(Machine machine, LibertyServer server, String endpoint,
                                    String controllerHost, int controllerPort,
                                    String controllerUser, String controllerPassword, boolean expectRC0) throws Exception {
        String serverName = server.getServerName();
        String installRoot = server.getInstallRoot();

        Log.info(c, "removeReplica", "Running collective removeReplica " + serverName);
        Properties env = new Properties();
        env.put("JAVA_HOME", server.getMachineJavaJDK());
        // This exercises --autoAcceptCertificates via argument rather than the property
        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           new String[] { "removeReplica",
                                                          endpoint,
                                                          "--host=" + controllerHost,
                                                          "--port=" + controllerPort,
                                                          "--user=" + controllerUser,
                                                          "--password=" + controllerPassword,
                                                          "--autoAcceptCertificates"
                                           },
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        Log.info(c, "removeReplica", "Command Result (getCommand):" + po.getCommand());
        Log.info(c, "removeReplica", "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, "removeReplica", "Command Result (stdout):\n" + stdout);
        Log.info(c, "removeReplica", "Command Result (stderr):\n" + po.getStderr());

        if (expectRC0) {
            assertTrue("Error running removeReplica command.\n" +
                       "STOP. CHECK THE LOGS FOR MORE DETAILS. THIS IS A TOP-LEVEL FAILURE AND DOES NOT ACCURATELY REFLECT WHY SOMETHING FAILED.\n" +
                       "Here is some hopefully useful information from the script. But this is a best effort and investigating the server logs and result.txt may be required\n" +
                       "stdout:\n" + stdout + "\n" +
                       "stderr:\n" + po.getStderr(),
                       findMatchingLine(stdout, ".*Successfully removed replica endpoint.*"));
        }
        return po.getReturnCode();
    }

    /**
     * Register a remote host with user/password authentication. Assumes the SSL certificates can be accepted.
     *
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param hostName The host name to register
     * @param controllerHost The hostname where the controller is running
     * @param controllerPort The port the controller is listening on
     * @param controllerUser The User ID that performs Admin operations on the controller
     * @param controllerPassword The password of the User ID that performs Admin operations on the controller
     * @param remoteUser The remote User who will be used when performing remote commands to the host being registered
     * @param remotePassword The password of the remote User who will be used when performing remote commands to the host being registered
     * @return Returns the return code of registerHost command. Please refer to registerHost command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int registerHost(Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                   String controllerPassword, String remoteUser, String remotePassword) throws Exception {
        return hostCommand("registerHost", machine, server, hostName, controllerHost, controllerPort, controllerUser, controllerPassword, remoteUser, remotePassword, null);

    }

    /**
     * Register a remote host with SSH authentication. Assumes the SSL certificates can be accepted.
     *
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param hostName The host name to register
     * @param controllerHost The hostname where the controller is running
     * @param controllerPort The port the controller is listening on
     * @param controllerUser The User ID that performs Admin operations on the controller
     * @param controllerPassword The password of the User ID that performs Admin operations on the controller
     * @param sshPrivateKey The path of the controller's private key file.
     * @return Returns the return code of registerHost command. Please refer to registerHost command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int registerHostWithSSH(Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                          String controllerPassword, String sshPrivateKey) throws Exception {
        return hostCommand("registerHost", machine, server, hostName, controllerHost, controllerPort, controllerUser, controllerPassword, null, null, sshPrivateKey);

    }

    /**
     * Unregister a remote host. Assumes the SSL certificates can be accepted.
     *
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param hostName The host name to register
     * @param controllerHost The hostname where the controller is running
     * @param controllerPort The port the controller is listening on
     * @param controllerUser The User ID that performs Admin operations on the controller
     * @param controllerPassword The password of the User ID that performs Admin operations on the controller
     * @param remoteUser The remote User who will be used when performing remote commands to the host being registered
     * @param remotePassword The password of the remote User who will be used when performing remote commands to the host being registered
     * @return Returns the return code of unregisterHost command. Please refer to unregisterHost command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int unregisterHost(Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                     String controllerPassword) throws Exception {
        return hostCommand("unregisterHost", machine, server, hostName, controllerHost, controllerPort, controllerUser, controllerPassword, null, null, null);

    }

    /**
     * Update a remote host information. Assumes the SSL certificates can be accepted.
     *
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param hostName The host name to register
     * @param controllerHost The hostname where the controller is running
     * @param controllerPort The port the controller is listening on
     * @param controllerUser The User ID that performs Admin operations on the controller
     * @param controllerPassword The password of the User ID that performs Admin operations on the controller
     * @param remoteUser The remote User who will be used when performing remote commands to the host being registered
     * @param remotePassword The password of the remote User who will be used when performing remote commands to the host being registered
     * @return Returns the return code of updateHost command. Please refer to updateHost command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int updateHost(Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                 String controllerPassword, String remoteUser, String remotePassword) throws Exception {
        return hostCommand("updateHost", machine, server, hostName, controllerHost, controllerPort, controllerUser, controllerPassword, remoteUser, remotePassword, null);

    }

    /**
     * Update a remote host information. Assumes the SSL certificates can be accepted.
     *
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param hostName The host name to register
     * @param controllerHost The hostname where the controller is running
     * @param controllerPort The port the controller is listening on
     * @param controllerUser The User ID that performs Admin operations on the controller
     * @param controllerPassword The password of the User ID that performs Admin operations on the controller
     * @param sshPrivateKey The new SSH private key of the user used to authenticate to the remote host
     * @return Returns the return code of updateHost command. Please refer to updateHost command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int updateHostSSHPrivateKey(Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                              String controllerPassword, String sshPrivateKey) throws Exception {
        return hostCommand("updateHost", machine, server, hostName, controllerHost, controllerPort, controllerUser, controllerPassword, null, null, sshPrivateKey);

    }

    /**
     * Update a remote host information. Assumes the SSL certificates can be accepted.
     *
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param hostName The host name to register
     * @param controllerHost The hostname where the controller is running
     * @param controllerPort The port the controller is listening on
     * @param controllerUser The User ID that performs Admin operations on the controller
     * @param controllerPassword The password of the User ID that performs Admin operations on the controller
     * @param sshPrivateKey The new SSH private key of the user used to authenticate to the remote host
     * @param hostWritePath The list of hostWritePaths to set for the host
     * @param hostReadPath The list of hostReadPaths to set for the host
     * @param javaHome The value of the system property java.home
     * @return Returns the return code of updateHost command. Please refer to updateHost command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int updateHostSSHPrivateKey(Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                              String controllerPassword, String sshPrivateKey, List<String> hostWritePaths, List<String> hostReadPaths,
                                              String javaHome) throws Exception {
        return hostCommand("updateHost", machine, server, hostName, controllerHost, controllerPort, controllerUser, controllerPassword, null, null, sshPrivateKey, hostWritePaths,
                           hostReadPaths, javaHome);

    }

    public static int updateHostRPC(Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                    String controllerPassword, String remoteUser, String remotePassword, List<String> hostWritePaths, List<String> hostReadPaths,
                                    String javaHome) throws Exception {
        return hostCommand("updateHost", machine, server, hostName, controllerHost, controllerPort, controllerUser, controllerPassword, remoteUser, remotePassword, null,
                           hostWritePaths, hostReadPaths, javaHome);

    }

    /**
     * Registers host level host (without servers) via MBean invocation
     *
     * @param connection MBean server connection to the collective controller.
     * @param hostAuthMap The host authorization map with credentials.
     * @return Object The return Object of MBean operation "registerHost".
     * @throws Exception
     */
    public static Object registerHost(MBeanServerConnection connection, String controllerHostName, Map<String, Object> hostAuthMap) throws Exception {

        //Call registerHost MBean to register our host
        ObjectName registrationName = new ObjectName("WebSphere:feature=collectiveController,type=CollectiveRegistration,name=CollectiveRegistration");
        return connection.invoke(registrationName, "registerHost",
                                 new Object[] { controllerHostName, hostAuthMap },
                                 new String[] { "java.lang.String", "java.util.Map" });

    }

    /**
     * Register a remote host. Assumes the SSL certificates can be accepted.
     *
     * @param command The host related command to run. Currently, registerHost, unregisterHost and updateHost are supported.
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param hostName The host name to register
     * @param controllerHost The hostname where the controller is running
     * @param controllerPort The port the controller is listening on
     * @param controllerUser The User ID that performs Admin operations on the controller
     * @param controllerPassword The password of the User ID that performs Admin operations on the controller
     * @param remoteUser The remote User who will be used when performing remote commands to the host being registered
     * @param remotePassword The password of the remote User who will be used when performing remote commands to the host being registered
     * @param sshPrivateKey The path of the controller's private key file.
     * @return Returns the return code of specified command. Please refer to the command's return code for details
     * @throws Exception If the command being executed throws an Exception
     */
    protected static int hostCommand(String command, Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                     String controllerPassword, String remoteUser, String remotePassword, String sshPrivateKey) throws Exception {
        return hostCommand(command, machine, server, hostName, controllerHost, controllerPort, controllerUser, controllerPassword, remoteUser, remotePassword, sshPrivateKey, null,
                           null, null);
    }

    /**
     * Register a remote host. Assumes the SSL certificates can be accepted.
     *
     * @param command The host related command to run. Currently, registerHost, unregisterHost and updateHost are supported.
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param hostName The host name to register
     * @param controllerHost The hostname where the controller is running
     * @param controllerPort The port the controller is listening on
     * @param controllerUser The User ID that performs Admin operations on the controller
     * @param controllerPassword The password of the User ID that performs Admin operations on the controller
     * @param remoteUser The remote User who will be used when performing remote commands to the host being registered
     * @param remotePassword The password of the remote User who will be used when performing remote commands to the host being registered
     * @param sshPrivateKey The path of the controller's private key file.
     * @param hostWritePath The list of hostWritePaths to set for the host (empty lists or list entries will be ignored)
     * @param hostReadPath The list of hostReadPaths to set for the host (empty lists or list entries will be ignored)
     * @param javaHome The value of the system property java.home
     * @return Returns the return code of specified command. Please refer to the command's return code for details
     * @throws Exception If the command being executed throws an Exception
     */
    protected static int hostCommand(String command, Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                     String controllerPassword, String remoteUser, String remotePassword, String sshPrivateKey, List<String> hostWritePaths,
                                     List<String> hostReadPaths, String javaHome) throws Exception {
        String installRoot = server.getInstallRoot();
        Properties env = new Properties();

        List<String> alCmdArgs = new ArrayList<String>();
        alCmdArgs.add(command);
        alCmdArgs.add(hostName);
        alCmdArgs.add("--host=" + controllerHost);
        alCmdArgs.add("--port=" + controllerPort);
        alCmdArgs.add("--user=" + controllerUser);
        alCmdArgs.add("--password=" + controllerPassword);
        if (command.equals("unregisterHost") == false) {
            if (sshPrivateKey != null)
                alCmdArgs.add("--sshPrivateKey=" + sshPrivateKey);
            else {
                alCmdArgs.add("--rpcUser=" + remoteUser);
                alCmdArgs.add("--rpcUserPassword=" + remotePassword);
            }
        }
        if (hostWritePaths != null && !hostWritePaths.isEmpty()) {
            for (String hostWritePath : hostWritePaths) {
                if (!hostWritePath.isEmpty()) {
                    alCmdArgs.add("--hostWritePath=" + hostWritePath);
                }
            }
        }
        if (hostReadPaths != null && !hostReadPaths.isEmpty()) {
            for (String hostReadPath : hostReadPaths) {
                if (!hostReadPath.isEmpty()) {
                    alCmdArgs.add("--hostReadPath=" + hostReadPath);
                }
            }
        }
        if (javaHome != null) {
            alCmdArgs.add("--hostJavaHome=" + javaHome);
        }

        String[] cmdArgs = new String[alCmdArgs.size()];
        alCmdArgs.toArray(cmdArgs);
        Log.info(c, "hostCommand", "Command: " + cmdArgs);

        env.put("JVM_ARGS", "-Dcom.ibm.websphere.collective.utility.autoAcceptCertificates=true");
        //    env.put("JAVA_HOME", server.getMachineJavaJDK());

        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           cmdArgs,
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        Log.info(c, command, "Command Result (getCommand):" + po.getCommand());
        Log.info(c, command, "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, command, "Command Result (stdout):\n" + stdout);
        Log.info(c, command, "Command Result (stderr):\n" + po.getStderr());
        return po.getReturnCode();
    }

    /**
     * Joins the server to the collective as a member.
     *
     * @param machine
     * @param server The Liberty server from which to get the install. Is not a target of the command execution.
     * @param controllerHost
     * @param controllerPort
     * @param controllerUser
     * @param controllerPassword
     * @param keystorePassword
     * @return Returns the return code of remove command. Please refer to remove command for return codes.
     * @throws Exception If the command being executed throws an Exception
     */
    public static int genKey(Machine machine, LibertyServer server,
                             String controllerHost, int controllerPort,
                             String controllerUser, String controllerPassword,
                             String keystorePassword, String keystoreFile) throws Exception {
        String installRoot = server.getInstallRoot();

        Log.info(c, "genKey", "Running genKey with target file " + keystoreFile);
        Properties env = new Properties();
        env.put("JVM_ARGS", "-Dcom.ibm.websphere.collective.utility.autoAcceptCertificates=true");
        env.put("JAVA_HOME", server.getMachineJavaJDK());
        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           new String[] { "genKey",
                                                          "--host=" + controllerHost,
                                                          "--port=" + controllerPort,
                                                          "--user=" + controllerUser,
                                                          "--password=" + controllerPassword,
                                                          "--keystorePassword=" + keystorePassword,
                                                          "--keystoreFile=" + keystoreFile },
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        Log.info(c, "genKey", "Command Result (getCommand):" + po.getCommand());
        Log.info(c, "genKey", "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, "genKey", "Command Result (stdout):\n" + stdout);
        Log.info(c, "genKey", "Command Result (stderr):\n" + po.getStderr());

        return po.getReturnCode();
    }

    /**
     * Sets a host and its servers or a single server into maintenance mode.
     *
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param targetHost The target host name
     * @param targetUserDir The target WLP user directory
     * @param targetServer The target server name
     * @param breakAffinity Break session affinity
     * @param force Set maintenance mode even if it causes autoScaling policy to be violated
     * @param controllerHost The hostname where the controller is located
     * @param controllerPort The port that the controller is listening on
     * @param controllerUser The User ID required to perform admin commands on the controller
     * @param controllerPassword The password of the User ID required to perform admin commands on the controller
     * @param expectedResults A map of host/server names to expected results.
     * @throws Exception
     */
    public static void enterMaintenanceMode(Machine machine, LibertyServer server,
                                            String targetHost, String targetUserDir, String targetServer,
                                            boolean breakAffinity, boolean force,
                                            String controllerHost, int controllerPort,
                                            String controllerUser, String controllerPassword,
                                            Map<String, MaintenanceModeExpectedResult> expectedResults) throws Exception {
        String controllerName = server.getServerName();
        String installRoot = server.getInstallRoot();

        Log.info(c, "enterMaintenanceMode", "Running collective enterMaintenanceMode command on controller " + controllerName);
        Properties env = new Properties();
        env.put("JVM_ARGS", "-Dcom.ibm.websphere.collective.utility.autoAcceptCertificates=true");
        env.put("JAVA_HOME", server.getMachineJavaJDK());

        List<String> cmdArgs = new ArrayList<String>();
        cmdArgs.add("enterMaintenanceMode");
        cmdArgs.add("--hostName=" + targetHost);
        if (targetUserDir != null)
            cmdArgs.add("--usrDir=" + targetUserDir);
        if (targetServer != null)
            cmdArgs.add("--server=" + targetServer);
        if (breakAffinity)
            cmdArgs.add("--break");
        if (force)
            cmdArgs.add("--force");
        cmdArgs.add("--host=" + controllerHost);
        cmdArgs.add("--port=" + controllerPort);
        cmdArgs.add("--user=" + controllerUser);
        cmdArgs.add("--password=" + controllerPassword);

        Log.info(c, "enterMaintenanceMode", "Command: " + cmdArgs);

        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           cmdArgs.toArray(new String[0]),
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        String stderr = po.getStderr();
        Log.info(c, "enterMaintenanceMode", "Command Result (getCommand):" + po.getCommand());
        Log.info(c, "enterMaintenanceMode", "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, "enterMaintenanceMode", "Command Result (stdout):\n" + stdout);
        Log.info(c, "enterMaintenanceMode", "Command Result (stderr):\n" + stderr);

        for (Map.Entry<String, MaintenanceModeExpectedResult> entry : expectedResults.entrySet()) {
            String name = entry.getKey();
            MaintenanceModeExpectedResult expectedResult = entry.getValue();
            switch (expectedResult) {
                case STATUS_IN_MAINTENANCE_MODE:
                    assertTrue("enterMaintenanceMode was not successful",
                               findMatchingLine(stdout, ".*Successfully set maintenance mode for " + name + ".*"));
                    break;
                case STATUS_NOT_IN_MAINTENANCE_MODE:
                    fail("Test case is checking for a result that is not applicable to this operation.");
                    break;
                case STATUS_ALTERNATE_SERVER_IS_STARTING:
                    assertTrue("enterMaintenanceMode did not start an alternate server for " + name,
                               findMatchingLine(stdout, ".*An alternate server must be started. Maintenance mode will be set for " +
                                                        name + " when the alternate server is started.*"));
                    break;
                case STATUS_ALTERNATE_SERVER_IS_NOT_AVAILABLE:
                    boolean foundInStdErr = false,
                                    foundInStdOut = false;
                    foundInStdErr = findMatchingLine(stderr, ".*Could not set maintenance mode for " + name + " because an alternate server is not available.*");
                    if (!foundInStdErr)
                        // Look in stdOut in case of redirected output
                        foundInStdOut = findMatchingLine(stdout, ".*Could not set maintenance mode for " + name + " because an alternate server is not available.*");
                    assertTrue("enterMaintenanceMode expected result STATUS_ALTERNATE_SERVER_IS_NOT_AVAILABLE did not occur for " + name,
                               foundInStdErr || foundInStdOut);
            }
        }
    }

    /**
     * Resets a host and its servers or a single server from maintenance mode.
     *
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param targetHost The target host name
     * @param targetUserDir The target WLP user directory
     * @param targetServer The target server name
     * @param controllerHost The hostname where the controller is located
     * @param controllerPort The port that the controller is listening on
     * @param controllerUser The User ID required to perform admin commands on the controller
     * @param controllerPassword The password of the User ID required to perform admin commands on the controller
     * @param expectedResults A map of host/server names to expected status results.
     * @throws Exception
     */
    public static void exitMaintenanceMode(Machine machine, LibertyServer server,
                                           String targetHost, String targetUserDir, String targetServer,
                                           String controllerHost, int controllerPort,
                                           String controllerUser, String controllerPassword,
                                           Map<String, MaintenanceModeExpectedResult> expectedResults) throws Exception {
        String controllerName = server.getServerName();
        String installRoot = server.getInstallRoot();

        Log.info(c, "exitMaintenanceMode", "Running collective exitMaintenanceMode command on controller " + controllerName);
        Properties env = new Properties();
        env.put("JVM_ARGS", "-Dcom.ibm.websphere.collective.utility.autoAcceptCertificates=true");
        env.put("JAVA_HOME", server.getMachineJavaJDK());

        List<String> cmdArgs = new ArrayList<String>();
        cmdArgs.add("exitMaintenanceMode");
        cmdArgs.add("--hostName=" + targetHost);
        if (targetUserDir != null)
            cmdArgs.add("--usrDir=" + targetUserDir);
        if (targetServer != null)
            cmdArgs.add("--server=" + targetServer);
        cmdArgs.add("--host=" + controllerHost);
        cmdArgs.add("--port=" + controllerPort);
        cmdArgs.add("--user=" + controllerUser);
        cmdArgs.add("--password=" + controllerPassword);

        Log.info(c, "exitMaintenanceMode", "Command: " + cmdArgs);

        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           cmdArgs.toArray(new String[0]),
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        String stderr = po.getStderr();
        Log.info(c, "exitMaintenanceMode", "Command Result (getCommand):" + po.getCommand());
        Log.info(c, "exitMaintenanceMode", "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, "exitMaintenanceMode", "Command Result (stdout):\n" + stdout);
        Log.info(c, "exitMaintenanceMode", "Command Result (stderr):\n" + stderr);

        for (Map.Entry<String, MaintenanceModeExpectedResult> entry : expectedResults.entrySet()) {
            String name = entry.getKey();
            MaintenanceModeExpectedResult expectedResult = entry.getValue();
            switch (expectedResult) {
                case STATUS_NOT_IN_MAINTENANCE_MODE:
                    assertTrue("exitMaintenanceMode was not successful",
                               findMatchingLine(stdout, ".*Successfully unset maintenance mode for " + name + ".*"));
                    break;
                default:
                    fail("Test case is checking for a result that is not applicable to this operation.");
                    break;
            }
        }
    }

    /**
     * Gets the maintenance mode status of a host and its servers or a single server.
     *
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param targetHost The target host name
     * @param targetUserDir The target WLP user directory
     * @param targetServer The target server name
     * @param controllerHost The hostname where the controller is located
     * @param controllerPort The port that the controller is listening on
     * @param controllerUser The User ID required to perform admin commands on the controller
     * @param controllerPassword The password of the User ID required to perform admin commands on the controller
     * @param expectedResults A map of host/server names to expected results.
     * @throws Exception
     */
    public static void getMaintenanceMode(Machine machine, LibertyServer server,
                                          String targetHost, String targetUserDir, String targetServer,
                                          String controllerHost, int controllerPort,
                                          String controllerUser, String controllerPassword,
                                          Map<String, MaintenanceModeExpectedResult> expectedResults) throws Exception {
        String controllerName = server.getServerName();
        String installRoot = server.getInstallRoot();

        Log.info(c, "getMaintenanceMode", "Running collective getMaintenanceMode command on controller " + controllerName);
        Properties env = new Properties();
        env.put("JVM_ARGS", "-Dcom.ibm.websphere.collective.utility.autoAcceptCertificates=true");
        env.put("JAVA_HOME", server.getMachineJavaJDK());

        List<String> cmdArgs = new ArrayList<String>();
        cmdArgs.add("getMaintenanceMode");
        cmdArgs.add("--hostName=" + targetHost);
        if (targetUserDir != null)
            cmdArgs.add("--usrDir=" + targetUserDir);
        if (targetServer != null)
            cmdArgs.add("--server=" + targetServer);
        cmdArgs.add("--host=" + controllerHost);
        cmdArgs.add("--port=" + controllerPort);
        cmdArgs.add("--user=" + controllerUser);
        cmdArgs.add("--password=" + controllerPassword);

        Log.info(c, "getMaintenanceMode", "Command: " + cmdArgs);

        ProgramOutput po = machine.execute(installRoot + "/bin/collective",
                                           cmdArgs.toArray(new String[0]),
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        String stderr = po.getStderr();
        Log.info(c, "getMaintenanceMode", "Command Result (getCommand):" + po.getCommand());
        Log.info(c, "getMaintenanceMode", "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, "getMaintenanceMode", "Command Result (stdout):\n" + stdout);
        Log.info(c, "getMaintenanceMode", "Command Result (stderr):\n" + stderr);

        for (Map.Entry<String, MaintenanceModeExpectedResult> entry : expectedResults.entrySet()) {
            String name = entry.getKey();
            MaintenanceModeExpectedResult expectedResult = entry.getValue();
            switch (expectedResult) {
                case STATUS_IN_MAINTENANCE_MODE:
                    assertTrue("Server is not in maintenance mode as expected",
                               findMatchingLine(stdout, name + " is in maintenance mode.*"));
                    break;
                case STATUS_NOT_IN_MAINTENANCE_MODE:
                    assertTrue("Server is in maintenance mode which is not expected",
                               findMatchingLine(stdout, name + " is not in maintenance mode.*"));
                    break;
                default:
                    // Alternate server starting is a timing window and hard to test.
                    fail("Test case is checking for a result that is not handled.");
                    break;
            }
        }
    }

    public enum MaintenanceModeExpectedResult {
        STATUS_IN_MAINTENANCE_MODE,
        STATUS_NOT_IN_MAINTENANCE_MODE,
        STATUS_ALTERNATE_SERVER_IS_STARTING,
        STATUS_ALTERNATE_SERVER_IS_NOT_AVAILABLE
    }

    /**
     * Sets maintenance mode using the MBean rather than the command line interface.
     * The MBean supports multiple targets unlike the command line interface.
     *
     * @param connection MBeanServerConnection
     * @param targets List of targets
     * @param breakAffinity Break session affinity
     * @param force Set maintenance mode even if it causes autoScaling policy to be violated
     * @param expectedResultsList Expected results
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void enterMaintenanceModeUsingMBean(MBeanServerConnection connection,
                                                      List<String> targets,
                                                      MaintenanceModeTargetType targetType,
                                                      boolean breakAffinity, boolean force,
                                                      List<Map<String, String>> expectedResultsList) throws Exception {

        Log.info(c, "enterMaintenanceModeUsingMBean", targets.toString());

        String operationName = null;
        switch (targetType) {
            case SERVER:
                operationName = "enterServerMaintenanceMode";
                break;
            case HOST:
                operationName = "enterHostMaintenanceMode";
        }

        ObjectName on = new ObjectName("WebSphere:feature=collectiveController,type=MaintenanceMode,name=MaintenanceMode");
        List<Map<String, String>> resultsList = (List<Map<String, String>>) connection.invoke(on, operationName,
                                                                                              new Object[] { targets, !breakAffinity, force },
                                                                                              new String[] { "java.util.List", "boolean", "boolean" });

        for (int targetNumber = 0; targetNumber < targets.size(); targetNumber++) {
            Map<String, String> results = resultsList.get(targetNumber);
            Map<String, String> expectedResults = expectedResultsList.get(targetNumber);
            for (Map.Entry<String, String> entry : expectedResults.entrySet()) {
                String name = entry.getKey();
                String expectedResult = entry.getValue();
                String actualResult = results.get(name);
                assertEquals("Unexpected result for " + name,
                             expectedResult,
                             actualResult);
            }
        }
    }

    /**
     * Resets maintenance mode using the MBean rather than the command line interface.
     * The MBean supports multiple targets unlike the command line interface.
     *
     * @param connection MBeanServerConnection
     * @param targets List of targets
     * @param expectedResultsList Expected results
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void exitMaintenanceModeUsingMBean(MBeanServerConnection connection,
                                                     List<String> targets,
                                                     MaintenanceModeTargetType targetType,
                                                     List<Map<String, String>> expectedResultsList) throws Exception {

        Log.info(c, "exitMaintenanceModeUsingMBean", targets.toString());

        String operationName = null;
        switch (targetType) {
            case SERVER:
                operationName = "exitServerMaintenanceMode";
                break;
            case HOST:
                operationName = "exitHostMaintenanceMode";
        }

        ObjectName on = new ObjectName("WebSphere:feature=collectiveController,type=MaintenanceMode,name=MaintenanceMode");
        List<Map<String, String>> resultsList = (List<Map<String, String>>) connection.invoke(on, operationName,
                                                                                              new Object[] { targets },
                                                                                              new String[] { "java.util.List" });

        for (int targetNumber = 0; targetNumber < targets.size(); targetNumber++) {
            Map<String, String> results = resultsList.get(targetNumber);
            Map<String, String> expectedResults = expectedResultsList.get(targetNumber);
            for (Map.Entry<String, String> entry : expectedResults.entrySet()) {
                String name = entry.getKey();
                String expectedResult = entry.getValue();
                String actualResult = results.get(name);
                assertEquals("Unexpected result for " + name,
                             expectedResult,
                             actualResult);
            }
        }
    }

    /**
     * Gets maintenance mode using the MBean rather than the command line interface.
     * The MBean supports multiple targets unlike the command line interface.
     *
     * @param connection MBeanServerConnection
     * @param targets List of targets
     * @param expectedResultsList Expected results
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void getMaintenanceModeUsingMBean(MBeanServerConnection connection,
                                                    List<String> targets,
                                                    MaintenanceModeTargetType targetType,
                                                    List<Map<String, String>> expectedResultsList) throws Exception {

        Log.info(c, "getMaintenanceModeUsingMBean", targets.toString());

        String operationName = null;
        switch (targetType) {
            case SERVER:
                operationName = "getServerMaintenanceMode";
                break;
            case HOST:
                operationName = "getHostMaintenanceMode";
        }

        ObjectName on = new ObjectName("WebSphere:feature=collectiveController,type=MaintenanceMode,name=MaintenanceMode");
        List<Map<String, String>> resultsList = (List<Map<String, String>>) connection.invoke(on, operationName,
                                                                                              new Object[] { targets },
                                                                                              new String[] { "java.util.List" });

        for (int targetNumber = 0; targetNumber < targets.size(); targetNumber++) {
            Map<String, String> results = resultsList.get(targetNumber);
            Map<String, String> expectedResults = expectedResultsList.get(targetNumber);
            for (Map.Entry<String, String> entry : expectedResults.entrySet()) {
                String name = entry.getKey();
                String expectedResult = entry.getValue();
                String actualResult = results.get(name);
                assertEquals("Unexpected result for " + name,
                             expectedResult,
                             actualResult);
            }
        }
    }

    public enum MaintenanceModeTargetType {
        HOST,
        SERVER
    }

    /**
     * Set tags for the given resource type and identity.
     *
     * @param resourceType
     * @param identity
     * @param tags
     */
    public static void setAdminTags(MBeanServerConnection connection, String resourceType, String identity, String[] tags) throws Exception {

        Log.info(c, "setAdminTags", "Setting tags " + tags + " on " + resourceType + " " + identity);

        ObjectName on = new ObjectName("WebSphere:feature=collectiveController,type=AdminMetadataManager,name=AdminMetadataManager");

        connection.invoke(on, "setAdminTags",
                          new Object[] { resourceType, identity, tags },
                          new String[] { "java.lang.String", "java.lang.String", "[Ljava.lang.String;" });

    }

    /**
     * Register a remote host. Assumes the SSL certificates can be accepted.
     *
     * @param command The host related command to run. Currently, updateHost is supported.
     * @param machine The Machine to run the command on
     * @param server The LibertyServer to run the command from
     * @param hostName The host name to register
     * @param controllerHost The hostname where the controller is running
     * @param controllerPort The port the controller is listening on
     * @param controllerUser The User ID that performs Admin operations on the controller
     * @param controllerPassword The password of the User ID that performs Admin operations on the controller
     * @param remoteUser The remote User who will be used when performing remote commands to the host being registered
     * @param remotePassword The password of the remote User who will be used when performing remote commands to the host being registered
     * @param sshPrivateKey The path of the controller's private key file.
     * @param autoAcceptCerts True if the autoAcceptCertificates flag should be set to automatically trust SSL certificates during this command
     * @return Returns the return code of specified command. Please refer to the command's return code for details
     * @throws Exception If the command being executed throws an Exception
     */
    public static int hostwlpnCommand(String command, Machine machine, LibertyServer server, String hostName, String controllerHost, int controllerPort, String controllerUser,
                                      String controllerPassword, String remoteUser, String remotePassword, String sshPrivateKey, Boolean autoAcceptCerts) throws Exception {
        String installRoot = server.getInstallRoot();
        Properties env = new Properties();

        List<String> alCmdArgs = new ArrayList<String>();
        alCmdArgs.add(command);
        alCmdArgs.add(hostName);
        alCmdArgs.add("--host=" + controllerHost);
        alCmdArgs.add("--port=" + controllerPort);
        alCmdArgs.add("--user=" + controllerUser);
        alCmdArgs.add("--password=" + controllerPassword);

        if (sshPrivateKey != null)
            alCmdArgs.add("--sshPrivateKey=" + sshPrivateKey);
        else {
            alCmdArgs.add("--rpcUser=" + remoteUser);
            alCmdArgs.add("--rpcUserPassword=" + remotePassword);
        }

        if (autoAcceptCerts) {
            alCmdArgs.add(" --autoAcceptCertificates");
        }

        String[] cmdArgs = new String[alCmdArgs.size()];
        alCmdArgs.toArray(cmdArgs);
        Log.info(c, "hostCommand", "Command: " + cmdArgs);

        env.put("JVM_ARGS", "-Dcom.ibm.websphere.collective.utility.autoAcceptCertificates=true");
        env.put("JAVA_HOME", server.getMachineJavaJDK());

        ProgramOutput po = machine.execute(installRoot + "/bin/wlpn-collective",
                                           cmdArgs,
                                           installRoot,
                                           env);

        String stdout = po.getStdout();
        Log.info(c, command, "Command Result (getCommand):" + po.getCommand());
        Log.info(c, command, "Command Result (getReturnCode):" + po.getReturnCode());
        Log.info(c, command, "Command Result (stdout):\n" + stdout);
        Log.info(c, command, "Command Result (stderr):\n" + po.getStderr());
        return po.getReturnCode();
    }

}
