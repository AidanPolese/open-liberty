package componenttest.common.apiservices.cmdline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ibm.tivoli.remoteaccess.FileInfo;
import com.ibm.tivoli.remoteaccess.OSResourceType;
import com.ibm.tivoli.remoteaccess.ProtocolSelector;
import com.ibm.tivoli.remoteaccess.RemoteAccess;
import com.ibm.tivoli.remoteaccess.SSHProtocol;
import com.ibm.tivoli.remoteaccess.UNIXProtocol;
import com.ibm.websphere.simplicity.ConnectionInfo;
import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.ProgramOutput;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.log.Log;

public class RXAProvider {

    public static String LOCAL_HOSTNAME;
    public static String LOCAL_IP_ADDRESS;
    public static String LOCAL_IP_ADDRESS2 = "127.0.0.1";
    public static final String LOCALHOST = "localhost";
    public static final String CYGWIN_DRIVE = "/cygdrive/";
    private static final Class<RXAProvider> c = RXAProvider.class;
    private static final OSResourceType[] POSIX = { OSResourceType.HPUX,
                                                   OSResourceType.IBMAIX, OSResourceType.OS400,
                                                   OSResourceType.OtherLinux, OSResourceType.RedHatLinux,
                                                   OSResourceType.SunSolaris, OSResourceType.SUSELinux,
                                                   OSResourceType.zOS };

    private static Map<ConnectionInfo, SSHProtocol> cache = new HashMap<ConnectionInfo, SSHProtocol>();
    private static Map<String, ConnectionInfo> dataCache = new HashMap<String, ConnectionInfo>();

    static {
        try {
            LOCAL_HOSTNAME = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (Exception e) {
            LOCAL_HOSTNAME = "localhost";
        }
        try {
            LOCAL_IP_ADDRESS = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            LOCAL_IP_ADDRESS = LOCAL_IP_ADDRESS2;
        }
    }

    public static RemoteAccess getRemoteAccess(ConnectionInfo connInfo)
                    throws Exception {
        return getRemoteAccess(connInfo, true, true);
    }

    public static boolean isLocal(String hostname) throws Exception {
        boolean local = ((LOCAL_HOSTNAME.equalsIgnoreCase(hostname))
                         || (LOCALHOST.equalsIgnoreCase(hostname))
                         || (LOCAL_IP_ADDRESS.equals(hostname)) || (LOCAL_IP_ADDRESS2
                        .equals(hostname)));
        return local;
    }

    public static SSHProtocol getRemoteAccess(ConnectionInfo connInfo,
                                              boolean connect, boolean useCached) throws Exception {
        final String method = "getRemoteAccess";
        Log.entering(c, method, new Object[] { connInfo, connect, useCached });
        String key = connInfo.getHost() + connInfo.getUser()
                     + connInfo.getPassword() + connInfo.getKeystore();
        Log.finer(c, method, "key: " + key);
        ConnectionInfo connData = dataCache.get(key);
        if (connData == null) {
            if (connInfo.getKeystore() != null) {
                connData = new ConnectionInfo(connInfo.getHost(),
                                connInfo.getKeystore(), connInfo.getUser(), connInfo
                                                .getPassword());
            } else {
                connData = new ConnectionInfo(connInfo.getHost(),
                                connInfo.getUser(), connInfo.getPassword());
            }
            Log.finer(c, method, "Adding new RXAConnectionData to cache.");
            dataCache.put(key, connData);
        }
        SSHProtocol ra = null;
        if (useCached) {
            Log.finer(c, method, "Getting cached RemoteAccess.");
            ra = cache.get(connData);
        }
        if (ra == null) {
            Log.finer(c, method,
                      "RemoteAccess is null. Obtaining a new instance.");
            RemoteAccess[] protocols = getProtocols(connData);
            ra = (SSHProtocol) ProtocolSelector.selectProtocol(protocols);
            if (ra == null) {
                throw new Exception(
                                "Unable to make remote connection with specified credentials: hostname="
                                                + connInfo.getHost() + ",username="
                                                + connInfo.getUser() + ",password="
                                                + connInfo.getPassword() + ",keystore="
                                                + connInfo.getKeystore());
            }
            if (useCached) {
                Log.finer(c, method, "Caching new RemoteAccess.");
                cache.put(connData, ra);
            }
            if (!connect && ra.inSession()) {
                Log
                                .finer(
                                       c,
                                       method,
                                       "RemoteAccess is connected and we want an unconnected session. Closing connection.");
                ra.endSession();
            }
        }
        if (connect && !ra.inSession()) {
            Log.finer(c, method,
                      "RemoteAccess is not connected. Establishing connection.");
            ra.beginSession();
        }
        Log.exiting(c, method, ra);
        return ra;
    }

    private static RemoteAccess[] getProtocols(ConnectionInfo connData) {
        final String method = "getProtocols";
        Log.entering(c, method, connData);
        RemoteAccess[] accessProtocols = new RemoteAccess[4];

        // instantiate various protocols we want to attempt
        String username = "";
        String password = "";
        File keyStore = null;
        String hostname = connData.getHost();
        if (connData.getUser() != null) {
            username = connData.getUser();
        }
        if (connData.getPassword() != null) {
            password = connData.getPassword();
        }
        if (connData.getKeystore() != null) {
            keyStore = connData.getKeystore();
        }
        SSHProtocol ssh = null;
        if (keyStore != null) {
            ssh = new SSHProtocol(keyStore, username, password.getBytes(),
                            hostname);
        } else {
            ssh = new SSHProtocol(username, password.getBytes(), hostname);
        }
        accessProtocols[0] = ssh;

        Log.exiting(c, method, accessProtocols);
        return accessProtocols;
    }

    /**
     * @param absolutePath
     * @param newPath
     * @return
     */
    public static boolean rename(RemoteFile oldPath, RemoteFile newPath) throws Exception {
        final String method = "rename";
        Log.entering(c, method, new Object[] { oldPath, newPath });
        String srcHost = oldPath.getMachine().getHostname();
        String destHost = newPath.getMachine().getHostname();

        boolean ret = false;
        if (srcHost.equals(destHost)) {
            RemoteAccess ra = getRemoteAccess(oldPath.getMachine().getConnInfo());
            ra.rename(fixOutgoingPath(ra, oldPath.getAbsolutePath()), fixOutgoingPath(ra, newPath.getAbsolutePath()));
            ret = ra.exists(fixOutgoingPath(ra, newPath.getAbsolutePath()));
        }
        Log.exiting(c, method, ret);
        return ret;
    }

    public static boolean copy(RemoteFile sourceFile, RemoteFile destFile, boolean binary) throws Exception {
        final String method = "copy";
        Log.entering(c, method, new Object[] { sourceFile, destFile });
        RemoteAccess source = null;
        RemoteAccess dest = null;
        boolean srcLocal = isLocal(sourceFile.getMachine().getHostname());
        boolean destLocal = isLocal(destFile.getMachine().getHostname());

        boolean ret;
        if (srcLocal && destLocal) {
            if (destFile.isDirectory())
                destFile = new RemoteFile(destFile.getMachine(), destFile, sourceFile.getName());
            InputStream is = sourceFile.openForReading();
            OutputStream os = null;
            byte[] buf = new byte[8096];
            int len = 0;
            try {
                os = destFile.openForWriting(false);

                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
            } finally {
                try {
                    if (os != null)
                        os.close();
                } catch (Exception e) {
                    throw e;
                } finally {
                    is.close();
                }
            }
            ret = destFile.exists();
        } else if (!srcLocal && !destLocal) {
            Log.finer(c, method, "Both source and destination files are remote.");
            source = getRemoteAccess(sourceFile.getMachine().getConnInfo());
            dest = getRemoteAccess(destFile.getMachine().getConnInfo());
            File tempFile = new File(System.getProperty("java.io.tmpdir"), new File(sourceFile.getAbsolutePath()).getName());
            tempFile.getParentFile().mkdirs();
            source.getFile(fixOutgoingPath(source, sourceFile.getAbsolutePath()), tempFile);
            dest.mkDirs(fixOutgoingPath(dest, destFile.getParent()));

            if (binary) {
                dest.putFile(tempFile, fixOutgoingPath(dest, destFile.getAbsolutePath()));
            } else {
                dest.putTextFile(tempFile, fixOutgoingPath(dest, destFile.getAbsolutePath()));
            }

            tempFile.delete();
            ret = dest.exists(fixOutgoingPath(dest, destFile.getAbsolutePath()));
        } else if (srcLocal) {
            Log.finer(c, method, "Source file is local. Destination file is remote.");
            File srcFile = new File(sourceFile.getAbsolutePath());
            dest = getRemoteAccess(destFile.getMachine().getConnInfo());
            dest.mkDirs(fixOutgoingPath(dest, destFile.getParent()));
            if (binary) {
                dest.putFile(srcFile, fixOutgoingPath(dest, destFile.getAbsolutePath()));
            } else {
                dest.putTextFile(srcFile, fixOutgoingPath(dest, destFile.getAbsolutePath()));
            }

            ret = dest.exists(fixOutgoingPath(dest, destFile.getAbsolutePath()));
        } else {
            // destLocal
            Log.finer(c, method, "Source file is remote. Destination file is local.");
            source = getRemoteAccess(sourceFile.getMachine().getConnInfo());
            File localDestFile = new File(destFile.getAbsolutePath());
            localDestFile.getParentFile().mkdirs();
            if (binary) {
                source.getFile(fixOutgoingPath(source, sourceFile.getAbsolutePath()), localDestFile);
            } else {
                source.getTextFile(fixOutgoingPath(source, sourceFile.getAbsolutePath()), localDestFile);
            }

            ret = localDestFile.exists();
        }
        Log.exiting(c, method, ret);
        return ret;
    }

    public static boolean delete(RemoteFile file) throws Exception {
        final String method = "delete";
        Log.entering(c, method, file);
        RemoteAccess ra = getRemoteAccess(file.getMachine().getConnInfo());
        ra.rm(fixOutgoingPath(ra, file.getAbsolutePath()), true, true);
        boolean ret = !ra.exists(fixOutgoingPath(ra, file.getAbsolutePath()));
        Log.exiting(c, method, ret);
        return ret;
    }

    public static ProgramOutput executeCommand(Machine machine, String cmd,
                                               String[] parameters, String workDir, Properties envVars)
                    throws Exception {
        return executeCommand(machine, cmd, parameters, workDir, envVars, 0);
    }

    public static ProgramOutput executeCommand(Machine machine, String cmd,
                                               String[] parameters, String workDir, Properties envVars, int timeout)
                    throws Exception {
        final String method = "executeCommand";
        Log.entering(c, method, new Object[] { machine, cmd, parameters,
                                              workDir, envVars });

        long start = System.currentTimeMillis();

        ProgramOutput ret = null;
        SSHProtocol ra = getRemoteAccess(machine.getConnInfo(), true,
                                         true);
        if (!ra.inSession())
            ra.beginSession();
        try {
            // first change the work directory
            if (workDir != null) {
                workDir = workDir.replace('\\', '/');
                workDir = fixOutgoingPath(ra, workDir);
                ra.setCurrentDirectory(workDir);
            }

            Hashtable<String, String> envBackup = new Hashtable<String, String>();
            // figure out if this is Unix env or Windows and use appropriate
            // command
            OperatingSystem os = OperatingSystem.getOperatingSystem(getOSName(machine));

            // next set the environment variables
            if (envVars != null) {
                String setEnvCmd = null;
                Enumeration<Object> keys = envVars.keys();
                while (keys.hasMoreElements()) {
                    String var = (String) keys.nextElement();
                    // backup the original value
                    String orig = ra.getEnvValue(var);
                    if (orig != null)
                        envBackup.put(var, orig);
                    // set the new value
                    if (ra instanceof UNIXProtocol) {
                        setEnvCmd = "export " + var + "="
                                    + (String) envVars.get(var);
                    } else {
                        setEnvCmd = os.getEnvVarSet() + " " + var + "="
                                    + (String) envVars.get(var);
                    }
                    ra.run(setEnvCmd);
                }
            }

            // now run the requested command
            String command = cmd;
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    command += (" " + parameters[i]);
                }
            }
            Log.finer(c, method, "Final command: " + command);
            com.ibm.tivoli.remoteaccess.ProgramOutput po = null;
            if (timeout > 0) {
                po = ra.run(command, timeout);
            } else {
                po = ra.run(command);
            }
            Log.finer(c, method, "Stdout", po.getStdout());
            Log.finer(c, method, "Stderr", po.getStderr());
            ret = new ProgramOutput(command, po.getReturnCode(),
                            po.getStdout(), po.getStderr());

            // Restore any backed-up env variables
            for (Map.Entry<String, String> e : envBackup.entrySet()) {
                String setEnvCmd = null;
                if (ra instanceof UNIXProtocol) {
                    setEnvCmd = "export " + e.getKey() + "=" + e.getValue();
                } else {
                    setEnvCmd = os.getEnvVarSet() + " " + e.getKey() + "="
                                + e.getValue();
                }
                ra.run(setEnvCmd);
            }
        } finally {
            // ra.endSession();
        }

        long dur = (System.currentTimeMillis() - start) / 1000;
        Log.finer(c, method, "Performance audit", new String[] { "Command: " + cmd, "Duration: " + dur });
        Log.exiting(c, method, ret);
        return ret;
    }

    public static ProgramOutput executeAsync(Machine machine, String cmd, String[] parameters,
                                             String workDir, Properties envVars) throws Exception {
        final String method = "executeAsync";
        Log.entering(c, method, new Object[] { machine, cmd, parameters, workDir,
                                              envVars });

        String newcmd = null;
        if (!machine.getOperatingSystem().equals(OperatingSystem.WINDOWS)) {
            newcmd = "nohup ";
        }
        newcmd += cmd + " ";
        for (int i = 0; i < parameters.length; i++)
            newcmd += parameters[i] + " ";
        newcmd += "&";

        ProgramOutput ret = executeCommand(machine, newcmd, null, workDir,
                                           envVars);
        Log.exiting(c, method, ret);
        return ret;
    }

    public static boolean exists(RemoteFile file) throws Exception {
        final String method = "exists";
        Log.entering(c, method, file);
        RemoteAccess ra = getRemoteAccess(file.getMachine().getConnInfo());
        boolean ret = ra.exists(fixOutgoingPath(ra, file.getAbsolutePath()));
        Log.exiting(c, method, ret);
        return ret;
    }

    public static String getOSName(Machine machine) throws Exception {
        final String method = "getOSName";
        Log.entering(c, method, machine);
        RemoteAccess ra = getRemoteAccess(machine.getConnInfo());
        //We do this as the second part of the argument returns a unknown when ran against a remote Mac.
        //By getting two versions of the OS name and appending to the front when it determines the OS type
        //It will look at the freeform before looking at the rest.
        String osName = ra.getOS().getFreeformOSName() + " " + ra.getOS().getOSResourceType().toString();
        Log.exiting(c, method, osName);
        return osName;
    }

    public static boolean isDirectory(RemoteFile dir) throws Exception {
        final String method = "isDirectory";
        Log.entering(c, method, dir);
        File file = new File(dir.getAbsolutePath());
        if (file.getParentFile() == null) {
            // root
            Log.finer(c, method, "Directory is root.");
            Log.exiting(c, method, true);
            return true;
        }
        RemoteAccess ra = getRemoteAccess(dir.getMachine().getConnInfo());
        String parent = fixOutgoingPath(ra, dir.getParent().replace('\\', '/'));
        boolean ret = (getFileType(ra, parent, fixOutgoingPath(ra, dir
                        .getAbsolutePath())) == FileInfo.isDirectory);
        Log.exiting(c, method, ret);
        return ret;
    }

    public static boolean isFile(RemoteFile file) throws Exception {
        final String method = "isFile";
        Log.entering(c, method, file);
        File f = new File(file.getAbsolutePath());
        if (f.getParentFile() == null) {
            // root
            Log.finer(c, method, "Directory is root.");
            Log.exiting(c, method, false);
            return false;
        }
        RemoteAccess ra = getRemoteAccess(file.getMachine().getConnInfo());
        String parent = fixOutgoingPath(ra, file.getParent().replace('\\', '/'));
        boolean ret = (getFileType(ra, parent, fixOutgoingPath(ra, file
                        .getAbsolutePath())) == FileInfo.isFile);
        Log.exiting(c, method, ret);
        return ret;
    }

    private static int getFileType(RemoteAccess ra, String parent, String file)
                    throws Exception {
        final String method = "getFileType";
        Log.entering(c, method, new Object[] { ra, parent, file });
        FileInfo[] files = ra.listFiles(parent);
        int type = FileInfo.unknown;
        for (int i = 0; i < files.length; ++i) {
            FileInfo f = files[i];
            String filePath = null;
            if (!parent.equals("/")) {
                filePath = parent + "/" + f.getFilename();
            } else {
                filePath = parent + f.getFilename();
            }
            if (filePath.equals(file)) {
                Log.finer(c, method, "File match found.");
                type = f.getFileType();
                break;
            }
        }
        Log.exiting(c, method, type);
        return type;
    }

    public static String[] list(RemoteFile file, boolean recursive)
                    throws Exception {
        final String method = "list";
        Log.entering(c, method, new Object[] { file, recursive });
        RemoteAccess ra = getRemoteAccess(file.getMachine().getConnInfo());
        String fixedFilePath = fixOutgoingPath(ra, file.getAbsolutePath());
        System.out.println("Fixed file path: " + fixedFilePath);
        FileInfo[] files = ra.listFiles(fixedFilePath);
        List<String> returnList = new ArrayList<String>();
        for (int i = 0; i < files.length; ++i) {
            if (!(files[i].getFilename().equals(".") || files[i].getFilename()
                            .equals(".."))) {
                String child = null;
                if (fixedFilePath.endsWith("/")) {
                    child = fixIncomingPath(file.getAbsolutePath()
                                            + files[i].getFilename());
                } else {
                    child = fixIncomingPath(file.getAbsolutePath() + "/"
                                            + files[i].getFilename());
                }
                returnList.add(child);
                RemoteFile childKey = new RemoteFile(file.getMachine(), child);
                if (recursive && isDirectory(childKey)) {
                    String[] grandChildren = list(childKey, recursive);
                    for (int k = 0; k < grandChildren.length; ++k) {
                        returnList.add(grandChildren[k]);
                    }
                }
            }
        }

        String[] ret = returnList.toArray(new String[0]);
        Log.exiting(c, method, ret);
        return ret;
    }

    public static boolean mkdir(RemoteFile dir) throws Exception {
        final String method = "mkdir";
        Log.entering(c, method, dir);
        RemoteAccess ra = getRemoteAccess(dir.getMachine().getConnInfo());
        try {
            ra.mkDir(fixOutgoingPath(ra, dir.getAbsolutePath()));
        } catch (IOException e) {
            // Swallow the failure since it probably means the folder already
            // exists
        }
        boolean ret = ra.exists(fixOutgoingPath(ra, dir.getAbsolutePath()));
        Log.exiting(c, method, ret);
        return ret;

    }

    public static boolean mkdirs(RemoteFile dir) throws Exception {
        final String method = "mkdirs";
        Log.entering(c, method, dir);
        RemoteAccess ra = getRemoteAccess(dir.getMachine().getConnInfo());
        // javadoc for mkdirs says it succeeds if the file exists... but mkdir
        // fails in the same case...
        ra.mkDirs(fixOutgoingPath(ra, dir.getAbsolutePath()));
        boolean ret = ra.exists(fixOutgoingPath(ra, dir.getAbsolutePath()));
        Log.exiting(c, method, ret);
        return ret;
    }

    public static void connect(ConnectionInfo connInfo) throws Exception {
        final String method = "connect";
        Log.entering(c, method, connInfo);
        getRemoteAccess(connInfo);
        Log.exiting(c, method);
    }

    public static void disconnect(ConnectionInfo connInfo)
                    throws Exception {
        final String method = "disconnect";
        Log.entering(c, method, connInfo);
        getRemoteAccess(connInfo).endSession();
        Log.exiting(c, method);
    }

    public static boolean isConnected(ConnectionInfo connInfo)
                    throws Exception {
        final String method = "isConnected";
        Log.entering(c, method, connInfo);
        boolean ret = getRemoteAccess(connInfo, false, true).inSession();
        Log.exiting(c, method, ret);
        return ret;
    }

    public static String getTempDir(ConnectionInfo connInfo)
                    throws Exception {
        final String method = "getTempDir";
        Log.entering(c, method, connInfo);
        String ret = fixIncomingPath(getRemoteAccess(connInfo).getTempDir());
        Log.exiting(c, method, ret);
        return ret;
    }

    public static ProgramOutput killProcess(Machine machine, int processId)
                    throws Exception {
        final String method = "killProcess";
        Log.entering(c, method, new Object[] { machine, processId });
        String cmd = null;
        String[] parameters = null;
        if (machine.getOperatingSystem() != OperatingSystem.WINDOWS) {
            cmd = "kill";
            parameters = new String[] { "-9", "" + processId };
        } else {
            cmd = "taskkill";
            parameters = new String[] { "/F", "/PID", "" + processId };
        }
        Log.finer(c, method, cmd, parameters);
        return executeCommand(machine, cmd, parameters, null, null);
    }

    public static InputStream openFileForReading(RemoteFile file)
                    throws Exception {
        final String method = "openFileForReading";
        Log.entering(c, method, file);
        RemoteAccess ra = getRemoteAccess(file.getMachine().getConnInfo());
        InputStream is = ra.getRemoteInputStream(fixOutgoingPath(file
                        .getMachine(), file.getAbsolutePath()));
        Log.exiting(c, method, is);
        return is;
    }

    public static OutputStream openFileForWriting(RemoteFile file,
                                                  boolean append) throws Exception {
        final String method = "openFileForWriting";
        Log.entering(c, method, new Object[] { file, append });
        RemoteAccess ra = getRemoteAccess(file.getMachine().getConnInfo());
        OutputStream os = ra.getRemoteOutputStream(fixOutgoingPath(file
                        .getMachine(), file.getAbsolutePath()), append);
        Log.exiting(c, method, os);
        return os;
    }

    protected static String fixOutgoingPath(Machine machine, String path)
                    throws Exception {
        final String method = "fixOutGoingPath";
        Log.entering(c, method, new Object[] { machine, path });
        RemoteAccess ra = getRemoteAccess(machine.getConnInfo());
        String ret = fixOutgoingPath(ra, path);
        Log.entering(c, method, ret);
        return ret;
    }

    /**
     * Makes necessary changes to the path for compatibility with the target
     * system. Windows paths are converted to Posix and vice- versa.
     * 
     * @param path
     *            The path to convert
     * @return The converted path
     */
    public static String fixOutgoingPath(RemoteAccess ra, String path)
                    throws Exception {
        Log.entering(c, "fixOutgoingPath", path);
        if (path == null)
            return null;
        boolean isPosixOS = true;
        OperatingSystem os = OperatingSystem.getOperatingSystem(ra.getOS().getFreeformOSName());
        //Makes the isPosixOS if it is not a unix machine (at least that's what i think it means)
        if (os.equals(OperatingSystem.WINDOWS))
            isPosixOS = false;

        if (isPosixOS && !isPosixPath(path)) {
            // Convert from Windows to posix (NOT cygwin)
            path = path.replace("\\", "/").replace("/cygdrive", "").replace(":", "");
        } else if (!isPosixOS) {
            // Convert from posix to Windows (cygwin)
            if (isPosixPath(path))
                path = "/cygdrive/c/" + path;
            else {
                if (path.indexOf(":") != -1)
                    path = "/cygdrive/"
                           + path.replace(":", "").replace("\\", "/");
                else if (path.indexOf("cygdrive") == -1)
                    path = "/cygdrive/c/" + path;
            }
        }

        Log.exiting(c, "fixOutgoingPath", path);
        return path;
    }

    /**
     * Converts a Cygwin path to a standard Windows path. This is independent of
     * the local system path type.
     * 
     * @param path
     *            The path to fix
     * @return The converted path String
     */
    protected static String fixIncomingPath(String path) {
        String result = path;
        if (path != null) {
            if (path.indexOf(CYGWIN_DRIVE) != -1) {
                result = path.replace('\\', '/').substring(
                                                           CYGWIN_DRIVE.length());
                result = ("" + result.charAt(0)).toUpperCase() + ":"
                         + result.substring(result.indexOf("/"));
            }
        }
        return result;
    }

    private static boolean isPosixOS(OSResourceType os) {
        for (OSResourceType res : POSIX)
            if (res.equals(os))
                return true;
        return false;
    }

    private static boolean isPosixPath(String path) {
        return (path.indexOf("cygdrive") == -1 && path.indexOf(":") == -1 && path
                        .indexOf("\\") == -1);
    }

    public static RemoteFile ensureFileIsOnMachine(Machine target,
                                                   RemoteFile file) throws Exception {
        final String method = "ensureFileIsOnMachine";
        Log.entering(c, method, new Object[] { target, file });
        if (file.getMachine().equals(target)) {
            Log.finer(c, method, "The file is already on the target machine.");
            Log.exiting(c, method, file);
            return file;
        }

        RemoteFile remoteFile = target.getFile(target.getTempDir(), file
                        .getName());
        try {
            remoteFile.getParentFile().mkdirs();
        } catch (IOException ie) {
        }

        // Catch this because it *is* possible the two files are on the same
        // machine, and
        // you can't read & write to the file at the same time
        try {
            remoteFile.copyFromSource(file);
        } catch (IOException ie) {
        }

        Log.exiting(c, method, remoteFile);
        return remoteFile;
    }

    public static Date getDate(Machine machine) throws Exception {
        final String method = "getTime";
        Log.entering(c, method, machine);
        RemoteAccess ra = getRemoteAccess(machine.getConnInfo());
        long milliseconds = ra.getEpochTime() * 1000;
        Log.finer(c, method, "milliseconds: " + milliseconds);
        Date date = new Date(milliseconds);
        Log.exiting(c, method, date);
        return date;

    }
}
