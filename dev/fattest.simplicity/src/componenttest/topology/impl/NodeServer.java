/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package componenttest.topology.impl;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.ibm.websphere.simplicity.ConnectionInfo;
import com.ibm.websphere.simplicity.LocalFile;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.log.Log;
import componenttest.common.apiservices.Bootstrap;
import componenttest.exception.TopologyException;
import componenttest.topology.utils.LibertyServerUtils;

/**
 *
 */
public class NodeServer {
    public static final Class<?> c = NodeServer.class;
    public static final String CLASS_NAME = c.getName();
    public static Logger LOG = Logger.getLogger(CLASS_NAME);

    protected static final String SERVER_LOG = "server.log";
    protected static final String ERROR_LOG = "error.log";
    protected static final String DEFAULT_SERVER = "defaultServer";
    protected static final String STOPPED = "is not running";
    protected static final String STARTED = "is running";

    protected String serverName;
    protected String hostName;
    protected String password;
    protected String user;
    protected String installRoot; // The root of the install
    protected String serverRoot; // The root of the node server
    protected String logsRoot; // The root of the Logs Files

    protected int serverLogMark = 0;
    protected int errorLogMark = 0;

    private final Machine machine; // Machine the server is on
    protected String serverToUse; // the server to use
    protected OperatingSystem machineOS;

    protected String pathToAutoFVTOutputServersFolder = "output/servers";
    public String pathToAutoFVTTestFiles = "lib/LibertyFATTestFiles/";
    protected String packageFilePath = null;

    // How frequently we poll the logs when waiting for something to happen 
    protected static final int WAIT_INCREMENT = 300;

    // Increasing this from 50 seconds to 120 seconds to account for poorly performing code;
    // this timeout should only pop in the event of an unexpected failure of apps to start.
    protected static final int LOG_SEARCH_TIMEOUT = 120 * 1000;

    //Used for keeping track of offset positions of log files
    protected final HashMap<String, Long> logOffsets = new HashMap<String, Long>();

    //Used for keeping track of mark positions of log files
    protected final HashMap<String, Long> logMarks = new HashMap<String, Long>();

    /**
     * @return the installRoot
     */
    public String getInstallRoot() {
        return installRoot;
    }

    /**
     * @return the machine
     */
    public Machine getMachine() {
        return machine;
    }

    /**
     * @return the server name
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @return whether or not the server is started
     */
    public boolean isServerStarted() {
        return (getServerStatus().equals(STARTED));
    }

    /**
     * @return whether or not the server is stopped
     */
    public boolean isServerStopped() {
        return (getServerStatus().equals(STOPPED));
    }

    public NodeServer(String name, Bootstrap b, String nodePackageFile) throws UnknownHostException, Exception {
        final String method = "NodeServer constructor";
        Log.info(c, method, "Entering " + method);

        this.serverName = name;

        if (b == null) {
            Log.info(c, "NodeServer()", "using default bootstrapping.properties");
            b = Bootstrap.getInstance();
        }

        hostName = b.getValue("hostName");

        if (serverName != null) {
            serverToUse = serverName;
        } else {
            serverToUse = b.getValue("serverName");
            if (serverToUse == null || serverToUse.trim().equals("")) {
                serverToUse = DEFAULT_SERVER;
            }
        }
        //Set install root as where we operate from; this will be where the unpacked server resides under
        installRoot = "./";

        user = b.getValue(hostName + ".user");
        password = b.getValue(hostName + ".password");
        String keystore = b.getValue("keystore");

        Log.info(c, method, "Creating Machine");
        Log.info(c, method, "Connecting to machine " + hostName + " with User " + user + ".");
        ConnectionInfo machineDetails = new ConnectionInfo(hostName, user, password);

        if ((password == null || password.length() == 0) && keystore != null && keystore.length() != 0) {
            File keyfile = new File(keystore);
            machineDetails = new ConnectionInfo(hostName, keyfile, user, password);
        }

        machine = Machine.getMachine(machineDetails);
        setup(nodePackageFile);

        Log.info(c, method, "Exiting " + method);
    }

    /**
     * Node server setup
     * 
     * Unpack the given node server and set variables
     */
    public void setup(String nodePackageFile) throws Exception {
        final String method = "setup()";
        Log.info(c, method, "Entering " + method);

        machine.connect();
        machine.setWorkDir(installRoot);
        if (this.serverToUse == null) {
            this.serverToUse = DEFAULT_SERVER;
        }

        machineOS = machine.getOperatingSystem();
        this.installRoot = LibertyServerUtils.makeJavaCompatible(installRoot, machine);

        /*
         * The following steps assume the PATH to node has been set, a la
         * "export PATH=$PATH:/home/ibmadmin/node-v4.3.1-linux-x64/bin"
         */

        //Copy over node package file
        LibertyFileManager.copyFileIntoLiberty(machine, "./", pathToAutoFVTTestFiles + nodePackageFile);
        /*
         * Run command wlpn-server unpack server_name tgz_file_name
         * The command expands the .tgz file at ${wlpn.usr.dir}/server_name, which defaults to the /home/user_name/wlpn directory.
         * If the wlpn directory does not already exist, the command creates the wlpn directory.
         */

        String cmd = "wlpn-server";
        List<String> args = new ArrayList<String>();
        args.add("unpack");
        args.add(serverName);
        args.add(nodePackageFile);

        final String[] parameters = args.toArray(new String[] {});

        ProgramOutput po = machine.execute(cmd, parameters);
        Log.info(c, method, "Output results are (stdout): " + po.getStdout());
        Log.info(c, method, "Output results are (stderr): " + po.getStderr());

        this.serverRoot = "wlpn/" + serverName;
        this.logsRoot = serverRoot + "/log";
        this.packageFilePath = "./" + nodePackageFile;

        Log.info(c, method, "Exiting " + method);
    }

    /**
     * Delete the package and its exploded directory
     */
    public void deleteNodeServerDir() {
        String dirPath = "./" + serverRoot;
        try {
            LibertyFileManager.deleteLibertyDirectoryAndContents(machine, dirPath);
            //Delete package file
            if (packageFilePath != null) {
                LibertyFileManager.deleteLibertyFile(machine, packageFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Join node server to collective. Command takes the following parameters; most controller values will
     * be found in the server.xml of the collective controller:
     * 
     * @param controllerHost: host to join the server to
     * @param controllerPort: Controller port
     * @param controllerUser: Controller user
     * @param controllerPassword: Controller password
     * @param keystorePassword: Keystore password
     * @param rpcUserPassword: Password for the machine
     * @throws Exception
     */
    public void joinCollective(String controllerHost, int controllerPort, String controllerUser, String controllerPassword, String memberKSPassword) throws Exception {
        final String method = "joinCollective()";
        Log.info(c, method, "Entering " + method);

        String cmd = "wlpn-collective";
        List<String> args = new ArrayList<String>();
        args.add("join");
        args.add(serverName);
        args.add("--host=" + controllerHost);
        args.add("--port=" + controllerPort);
        args.add("--user=" + controllerUser);
        args.add("--password=" + controllerPassword);
        args.add("--keystorePassword=" + memberKSPassword);
        args.add("--autoAcceptCertificates");
        args.add("--hostName=" + this.hostName);

        final String[] parameters = args.toArray(new String[] {});

        ProgramOutput po = machine.execute(cmd, parameters);
        Log.info(c, method, "PO stdout: " + po.getStdout());
        Log.info(c, method, "PO stderr: " + po.getStderr());
        Log.info(c, method, "Exiting " + method);
    }

    /**
     * Remove a node server from a collective.
     * Command takes the following parameters; most controller values will
     * be found in the server.xml of the collective controller:
     * 
     * @param controllerHost: Controller host
     * @param controllerPort: Controller port
     * @param controllerUser: Controller user
     * @param controllerPassword: Controller password
     * @throws Exception
     */
    public void removeFromCollective(String controllerHost, String controllerPort, String controllerUser,
                                     String controllerPassword) throws Exception {
        final String method = "removeFromCollective()";
        Log.info(c, method, "Entering " + method);

        String cmd = "wlpn-collective";
        List<String> args = new ArrayList<String>();
        args.add("remove");
        args.add(serverName);
        args.add("--host=" + controllerHost);
        args.add("--port=" + controllerPort);
        args.add("--user=" + controllerUser);
        args.add("--password=" + controllerPassword);
        args.add("--autoAcceptCertificates");

        final String[] parameters = args.toArray(new String[] {});

        ProgramOutput po = machine.execute(cmd, parameters);
        Log.info(c, method, "PO stdout: " + po.getStdout());
        Log.info(c, method, "PO stderr: " + po.getStderr());
        Log.info(c, method, "Exiting " + method);
    }

    /**
     * Update a host to a collective.
     * Command takes the following parameters; most controller values will
     * be found in the server.xml of the collective controller:
     * 
     * @param controllerHost: Controller host
     * @param controllerPort: Controller port
     * @param controllerUser: Controller user
     * @param controllerPassword: Controller password
     * @param rpcUser: rpc User of the host
     * @param rpcUserPassword: rpc password of the host
     * @throws Exception
     */
    public void updateHost(String controllerHost, int controllerPort, String controllerUser, String controllerPassword,
                           String rpcUser, String rpcUserPassword) throws Exception {
        final String method = "updateHost()";
        Log.info(c, method, "Entering " + method);

        String cmd = "wlpn-collective";
        List<String> args = new ArrayList<String>();
        args.add("updateHost");
        args.add(this.hostName);
        args.add("--host=" + controllerHost);
        args.add("--port=" + controllerPort);
        args.add("--user=" + controllerUser);
        args.add("--password=" + controllerPassword);
        args.add("--rpcUser=" + rpcUser);
        args.add("--rpcUserPassword=" + rpcUserPassword);
        args.add("--autoAcceptCertificates");

        final String[] parameters = args.toArray(new String[] {});

        ProgramOutput po = machine.execute(cmd, parameters);
        Log.info(c, method, "PO stdout: " + po.getStdout());
        Log.info(c, method, "PO stderr: " + po.getStderr());
        Log.info(c, method, "Exiting " + method);
    }

    /**
     * Start server
     * Command is: wlpn-server start <server name>
     * 
     * @return Returns ProgramOutpu of the wlpn-server start command
     * @throws Exception
     */
    public ProgramOutput startServer() throws Exception {
        final String method = "startServer()";
        Log.info(c, method, "Entering " + method);

        String cmd = "wlpn-server";

        List<String> args = new ArrayList<String>();
        args.add("start");
        args.add(serverName);
        final String[] parameters = args.toArray(new String[] {});

        ProgramOutput output;
        output = machine.execute(cmd, parameters);
        Log.info(c, method, "Output results are (stdout): " + output.getStdout());
        Log.info(c, method, "Output results are (stderr): " + output.getStderr());
        Thread.sleep(1000);

        Log.info(c, method, "Exiting " + method);
        return output;
    }

    /**
     * Stop server
     * Command is: wlpn-server stop <server name>
     * 
     * @return Returns ProgramOutpu of the wlpn-server stop command
     * @throws Exception
     */
    public ProgramOutput stopServer() throws Exception {
        final String method = "stopServer()";
        Log.info(c, method, "Entering " + method);

        String cmd = "wlpn-server";

        List<String> args = new ArrayList<String>();
        args.add("stop");
        args.add(serverName);
        final String[] parameters = args.toArray(new String[] {});

        ProgramOutput output;
        output = machine.execute(cmd, parameters);
        Log.info(c, method, "Output results are (stdout): " + output.getStdout());
        Log.info(c, method, "Output results are (stderr): " + output.getStderr());
        Thread.sleep(1000);

        Log.info(c, method, "Exiting " + method);
        return null;
    }

    /**
     * Restart the server, using the stop and start methods
     * 
     * @throws Exception
     */
    public void restartServer() throws Exception {
        final String method = "restartServer()";
        Log.info(c, method, "Entering " + method);
        stopServer();
        startServer();

        Log.info(c, method, "Exiting " + method);
    }

    /**
     * Query and return server status
     * Command is: wlpn-server status <servername>
     * 
     * @return String representing the server status
     */
    public String getServerStatus() {
        final String method = "getServerStatus()";
        Log.info(c, method, "Entering " + method);

        String cmd = "wlpn-server";

        List<String> args = new ArrayList<String>();
        args.add("status");
        args.add(serverName);
        final String[] parameters = args.toArray(new String[] {});

        ProgramOutput output;
        String serverState = "";
        try {
            output = machine.execute(cmd, parameters);

            Log.info(c, method, output.getStdout());
            //"Server <servername> is not running."        
            String result = output.getStdout();
            if (result.contains(STOPPED)) {
                serverState = STOPPED;
            }
            //"Server <servername> is running with process ID 14443"
            if (result.contains(STARTED)) {
                serverState = STARTED;
            }
        } catch (Exception e) {
            //server does not exist in this case
            Log.info(c, method, "Failed to get server state, error message follows");
            Log.error(c, method, e);
        }

        Log.info(c, method, "Exiting " + method);
        return serverState;
    }

    /**
     * Copy log files over after a run into a folder for the node server
     * 
     * @throws Exception
     */
    public void postStopServerArchive() throws Exception {
        final String method = "postStopServerArchive()";
        Log.info(c, method, "Entering " + method);

        //zip and copy over files post run
        Log.info(c, method, "Moving logs to the output folder");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        Date d = new Date(System.currentTimeMillis());

        String logDirectoryName = pathToAutoFVTOutputServersFolder + "/" + serverToUse + "-" + sdf.format(d);
        LocalFile logFolder = new LocalFile(logDirectoryName);
        RemoteFile serverFolder = new RemoteFile(machine, logsRoot);

        // Copy the log files: try to move them instead if we can
        try {
            copyNodeLogs(serverFolder, logFolder, true, true);
        } catch (Exception e) {
            Log.info(c, method, "Failed to copy node logs, likely because directory does not exist, error message follows");
            Log.error(c, method, e);
        }

        Log.info(c, method, "Exiting " + method);
    }

    /**
     * Copy node logs over
     * 
     * @param remoteDirectory - Remote server log directory
     * @param destination - Destination to copy files to
     * @param ignoreFailures - Boolean flag to ignore failures
     * @param moveFile - Boolean to move files instead of copy
     * @throws Exception
     */
    protected void copyNodeLogs(RemoteFile remoteDirectory, LocalFile destination, boolean ignoreFailures, boolean moveFile) throws Exception {
        String method = "copyNodeLogs";
        Log.entering(c, method);
        destination.mkdirs();

        ArrayList<String> logs = new ArrayList<String>();
        logs = listDirectoryContents(remoteDirectory);
        for (String l : logs) {
            if (remoteDirectory.getName().equals("workarea")) {
                if (l.equals("org.eclipse.osgi") || l.startsWith(".s")) {
                    // skip the osgi framework cache, and runtime artifacts: too big / too racy
                    Log.info(c, "recursivelyCopyDirectory", "Skipping workarea element " + l);
                    continue;
                }
            }

            RemoteFile toCopy = new RemoteFile(machine, remoteDirectory, l);
            LocalFile toReceive = new LocalFile(destination, l);
            Log.info(c, "recursivelyCopyDirectory", "Getting: " + toCopy.getAbsolutePath());

            try {
                if (moveFile) {
                    boolean copied = false;

                    // If we're local, try to rename the file instead..
                    if (machine.isLocal() && toCopy.rename(toReceive)) {
                        copied = true; // well, we moved it, but it counts.
                        Log.info(c, "recursivelyCopyDirectory", "MOVE: " + l + " to " + toReceive.getAbsolutePath());
                    }

                    if (!copied && toReceive.copyFromSource(toCopy)) {
                        // copy was successful, clean up the source log
                        toCopy.delete();
                        Log.info(c, "recursivelyCopyDirectory", "MOVE: " + l + " to " + toReceive.getAbsolutePath());
                    }
                } else {
                    toReceive.copyFromSource(toCopy);
                    Log.info(c, "recursivelyCopyDirectory", "COPY: " + l + " to " + toReceive.getAbsolutePath());
                }
            } catch (Exception e) {
                Log.info(c, "recursivelyCopyDirectory", "unable to copy or move " + l + " to " + toReceive.getAbsolutePath());
                // Ignore on request and carry on copying the rest of the files
                if (!ignoreFailures) {
                    throw e;
                }
            }
        }

        Log.exiting(c, method);
    }

    /**
     * Method for returning the directory contents as a list of Strings representing first level file/dir names
     * 
     * @return ArrayList of File/Directory names
     *         that exist at the first level i.e. it's not recursive. If it's a directory the String in the list is prefixed with a /
     * @throws TopologyException
     */
    protected ArrayList<String> listDirectoryContents(RemoteFile serverDir) throws Exception {
        return listDirectoryContents(serverDir, null);

    }

    protected ArrayList<String> listDirectoryContents(String path, String fileName) throws Exception {

        RemoteFile serverDir = new RemoteFile(machine, path);
        return listDirectoryContents(serverDir, fileName);

    }

    protected ArrayList<String> listDirectoryContents(RemoteFile serverDir, String fileName) throws Exception {

        final String method = "serverDirectoryContents";
        Log.entering(c, method);
        if (!serverDir.isDirectory() || !serverDir.exists())
            throw new TopologyException("The specified directoryPath \'"
                                        + serverDir.getAbsolutePath() + "\' was not a directory");

        RemoteFile[] firstLevelFiles = serverDir.list(false);
        ArrayList<String> firstLevelFileNames = new ArrayList<String>();

        for (RemoteFile f : firstLevelFiles) {

            if (fileName == null) {
                firstLevelFileNames.add(f.getName());
            } else if (f.getName().contains(fileName)) {
                firstLevelFileNames.add(f.getName());

            }
        }

        return firstLevelFileNames;
    }

    /**
     * Update the last log mark for a file
     * 
     * @param logName Name of the log file
     */
    protected void updateLogMark(String logName) {
        final String method = "updateLogMark()";
        Log.info(c, method, "Entering " + method);
        //To update #lines in a file use sed -n '$=' error.log
        //This will give us the # of lines in the file

        String cmd = "sed";
        String logLocation = logsRoot + "/" + logName;

        List<String> args = new ArrayList<String>();
        args.add("-n");
        args.add("'$='");
        args.add(logLocation);
        final String[] parameters = args.toArray(new String[] {});

        ProgramOutput output;
        try {
            output = machine.execute(cmd, parameters);
            Log.info(c, method, "Output is: " + output.getStdout());
            //Convert stdout to number, save as last log mark
            if (logName.equals(SERVER_LOG)) {
                try {
                    serverLogMark = Integer.parseInt(output.getStdout().trim());
                } catch (NumberFormatException e) {
                    //Number format error; file is empty. Do nothing, leave mark at 0
                }
            }
            else if (logName.equals(ERROR_LOG)) {
                try {
                    errorLogMark = Integer.parseInt(output.getStdout().trim());
                } catch (NumberFormatException e) {
                    //Number format error; file is empty. Do nothing, leave mark at 0
                }
            }
        } catch (Exception e) {
            Log.error(c, method, e);
        }
        Log.info(c, method, "Exiting " + method);
    }

    /**
     * Wait for a search string in the server log
     * 
     * @param regexp - String to match
     * @param timeout - Time to wait for a string match
     * @return String log line that matched the regex
     */
    public String waitForStringInServerLog(String regexp, long timeout) {
        final String method = "waitForStringInLogGrep()";
        Log.info(c, method, "Entering " + method);
        long initialTime = System.currentTimeMillis();
        long timeElapsed = 0;

        String result = null;
        try {
            while (result == null && timeElapsed < timeout) {
                result = grepNodeLog(regexp, timeout, ERROR_LOG);
                timeElapsed = System.currentTimeMillis() - initialTime;
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Log.warning(c, "Could not find string in default log file due to exception " + e);
        }

        //Update log mark
        updateLogMark(SERVER_LOG);
        Log.info(c, method, "Exiting " + method);
        return result;
    }

    /**
     * Wait for a search string in the error log
     * 
     * @param regexp - String to match
     * @param timeout - Time to wait for a string match in ms
     * @return String log line that matched the regex
     */
    public String waitForStringInErrorLog(String regexp, long timeout) {
        final String method = "waitForStringInLogGrep()";
        Log.info(c, method, "Entering " + method);
        long initialTime = System.currentTimeMillis();
        long timeElapsed = 0;

        String result = null;
        try {
            while (result == null && timeElapsed < timeout) {
                result = grepNodeLog(regexp, timeout, ERROR_LOG);
                timeElapsed = System.currentTimeMillis() - initialTime;
            }
        } catch (Exception e) {
            Log.warning(c, "Could not find string in default log file due to exception " + e);
        }

        //Update log mark
        updateLogMark(ERROR_LOG);
        Log.info(c, method, "Exiting " + method);
        return result;
    }

    /**
     * Use grep to search for the regexp in the log file
     * 
     * @param regexp - string to match
     * @param timeout - time to wait for the string in the log
     * @param fileName - log to be searched
     * @return String matching the regexp, or null if not found
     */
    protected String grepNodeLog(String regexp, long timeout, String logName) {
        final String method = "grepNodeLog()";
        Log.info(c, method, "Entering " + method);
        //Use grep -nr to find the search string in the file

        String cmd = "grep";
        String logLocation = logsRoot + "/" + logName;

        List<String> args = new ArrayList<String>();
        args.add("-nr");
        args.add("'" + regexp + "'");
        args.add(logLocation);
        final String[] parameters = args.toArray(new String[] {});

        ProgramOutput output;
        try {
            output = machine.execute(cmd, parameters);
            //If anything was found, the return code will be 0
            if (output.getReturnCode() == 0) {
                //Stdout will contain the results of grep along with line numbers
                String grepResults = output.getStdout();

                //Get last mark line
                int lastMark = 0;
                if (logName.equals(SERVER_LOG)) {
                    lastMark = serverLogMark;
                }
                else if (logName.equals(ERROR_LOG)) {
                    lastMark = errorLogMark;
                }

                //Go through the grep results to see if we have a match with a line number 
                //greater than the last log mark for this file
                String lines[] = grepResults.split("\\r?\\n");
                for (String line : lines) {
                    //Delimit on the first space and turn that token into a number
                    String tokens[] = line.split(":");
                    int number = Integer.parseInt(tokens[0]);

                    if (number > lastMark) {
                        Log.info(c, method, "Matched search string " + regexp + " with line " + line);
                        Log.info(c, method, "Exiting " + method);
                        return line;
                    }
                }

            } else {
                Log.info(c, method, "Search string " + regexp + " not found in log file " + logName);
            }
        } catch (Exception e) {
            //Ignore; we didn't find the string
        }
        Log.info(c, method, "Exiting " + method);
        return null;
    }

}
