package com.ibm.ws.collective.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.ibm.websphere.collective.repository.AdminMetadataConstants;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.kernel.service.utils.PathUtils;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013,2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean
 */
public final class RepositoryPathUtility {

    final static String HOST_REPOSITORY_PATH = "/sys.was.collectives/local/hosts/";
    public final static String METADATA_REPOSITORY_PATH = "/sys.was.system/metadata/";

    /**
     * Determines if the character is a lower-case alphabet character.
     * 
     * @param c
     * @return
     */
    @Trivial
    private static final boolean isLowerAlpha(char c) {
        return c >= 'a' && c <= 'z';
    }

    /**
     * Determines if the character is an upper-case alphabet character.
     * 
     * @param c
     * @return
     */
    @Trivial
    private static final boolean isUpperAlpha(char c) {
        return c >= 'A' && c <= 'Z';
    }

    /**
     * Determines if the character is an alphabet character.
     * 
     * @param c
     * @return
     */
    @Trivial
    private static final boolean isAlpha(char c) {
        return isLowerAlpha(c) || isUpperAlpha(c);
    }

    /**
     * Determines if the path is a Windows path, by checking to see if it
     * starts with a Windows drive letter, e.g. C:/
     * <p>
     * Note that the slash is required and expected to be a unix-style slash.
     * 
     * @param path
     * @return
     */
    @Trivial
    private static final boolean hasWindowsDrivePrefix(String path) {
        if (path.length() < 3) {
            return false;
        } else if (isAlpha(path.charAt(0)) &&
                   (path.charAt(1) == ':') &&
                   (path.charAt(2) == '/')) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Capitalize the case of the drive letter for the given Windows path.
     * If the path is not a Windows path, no work is done.
     * 
     * @param path a path, possibly absolute. Must not be {@code null}.
     * @return the path with a normalized drive letter
     */
    @Trivial
    private static final String capitalizeDriveLetter(String path) {
        if (hasWindowsDrivePrefix(path) && isLowerAlpha(path.charAt(0))) {
            return Character.toUpperCase(path.charAt(0)) + path.substring(1);
        } else {
            return path;
        }
    }

    /**
     * Split server tuple into individual components are return as array.
     * 
     * @param serverTuple in host,userdir,server format
     * @return array of 3 elements: host|userdir|name or {@code null} if the input was not a tuple
     */
    @Trivial
    private static final String[] getServerTupleComponents(String serverTuple) {
        if (serverTuple == null) {
            return null;
        }
        int firstComma = serverTuple.indexOf(',');
        int lastComma = serverTuple.lastIndexOf(',');
        if (firstComma == lastComma) {
            return null;
        }
        String[] tupleComponents = new String[3];
        tupleComponents[0] = serverTuple.substring(0, firstComma);
        tupleComponents[1] = serverTuple.substring(firstComma + 1, lastComma);
        tupleComponents[2] = serverTuple.substring(lastComma + 1);
        return tupleComponents;
    }

    /**
     * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean#buildServerRepositoryPath(String)
     */
    public static final String buildServerRepositoryPath(String serverTuple) {
        String[] serverTupleComponents = getServerTupleComponents(serverTuple);
        if (serverTupleComponents == null) {
            throw new IllegalArgumentException("The specified input to RepositoryPathUtility.buildServerRepositoryPath was not a recognized server tuple");
        }
        return buildServerRepositoryPath(serverTupleComponents[0], getURLEncodedPath(serverTupleComponents[1]), serverTupleComponents[2], true);
    }

    /**
     * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean#normalizePath(String)
     */
    public static final String normalizePath(String path) {
        if (path == null) {
            return path;
        }

        // Path is empty, don't bother doing work
        if (path.isEmpty()) {
            return path;
        }

        // Normalize the path - this changes all \ to /
        path = PathUtils.normalize(path);
        // Remove duplicate slashes - ?
        path = path.replaceAll("//+", "/");

        // If we start with file: URL prefix, we were a URL... need to process.
        if (path.startsWith("file:")) {
            path = path.substring(5);
            // Remove leading slash if windows path
            if (path.length() > 3 && path.charAt(0) == '/' && path.charAt(2) == ':') {
                path = path.substring(1);
            }
        }
        path = capitalizeDriveLetter(path);

        // Remove any trailing slash in the right cases
        if (path.length() == 3 && hasWindowsDrivePrefix(path)) {
            // We have something like C:/, don't do anything!
        } else if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

    /**
     * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean#getURLEncodedPath(String)
     */
    public static final String getURLEncodedPath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("getURLEncodedPath: path is null");
        }

        path = normalizePath(path);
        return encodeDir(path);
    }

    /**
     * Internal method to URL encode a directory.
     * 
     * @param path
     */
    public static String encodeDir(String path) {
        try {
            return URLEncoder.encode(path, "UTF8");
        } catch (UnsupportedEncodingException e) {
            String msg = "Got a really un expected UnsupportedEncodingException. A JVM with no UTF8 support!";
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Internal method to decode a URL encoded directory. There is no
     * identified scenario where this would be needed externally, but we could
     * expose it in the future.
     * 
     * @param urlEncodedUserDir
     * @return The URL decoded String
     */
    public static final String decodeURLEncodedDir(String urlEncodedUserDir) {
        try {
            return URLDecoder.decode(urlEncodedUserDir, "UTF8");
        } catch (UnsupportedEncodingException e) {
            String msg = "Got a really un expected UnsupportedEncodingException. A JVM with no UTF8 support!";
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean#buildHostRepositoryPath(String)
     */
    public static final String buildHostRepositoryPath(String hostName) {

        final String method = "buildHostRepositoryPath";

        isValidString(method, "hostName", hostName);

        StringBuilder path = new StringBuilder(HOST_REPOSITORY_PATH);
        path.append(hostName.toLowerCase());
        path.append("/");
        return path.toString();
    }

    /**
     * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean#buildServerRepositoryPath(String, String, String)
     */
    public static final String buildServerRepositoryPath(String hostName, String urlEncodedUserDir, String serverName) {
        return buildServerRepositoryPath(hostName, urlEncodedUserDir, serverName, true);
    }

    /**
     * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean#buildServerRepositoryPath(String, String, String)
     */
    public static final String buildServerRepositoryPath(String hostName, String urlEncodedUserDir,
                                                         String serverName, boolean addTrailingSlash) {

        final String method = "buildServerRepositoryPath";

        isValidString(method, "hostName", hostName);
        isValidString(method, "urlEncodedUserDir", urlEncodedUserDir);
        isValidString(method, "serverName", serverName);

        // Build the server path for the repository using the unique tuple:  
        // hostname - userdir - servername
        StringBuilder path = new StringBuilder(HOST_REPOSITORY_PATH);
        path.append(hostName.toLowerCase());
        path.append("/userdirs/");
        path.append(urlEncodedUserDir);
        path.append("/servers/");
        path.append(serverName);
        if (addTrailingSlash) {
            path.append("/");
        }
        return path.toString();
    }

    /**
     * Builds a metadata path including the identity: "{METADATA_REPOSITORY_PATH}/resourceType/resourceIdentity/"
     */
    public static final String buildMetadataIdentityPath(String resourceType, String identity) {

        final String method = "buildMetadataIdentityPath";

        isValidString(method, "resourceType", resourceType);
        isValidString(method, "identity", identity);

        return doBuildMetadataPath(resourceType, identity);
    }

    /**
     * Builds a metadata path down to the resource type: "{METADATA_REPOSITORY_PATH}/resourceType/"
     */
    public static final String buildMetadataResourcePath(String resourceType) {

        final String method = "buildMetadataResourcePath";

        isValidString(method, "resourceType", resourceType);

        return doBuildMetadataPath(resourceType, null);
    }

    /**
     * Build a metadata repository path.
     * 
     * @param resourceType a validated type
     * @param identity a validated identity or null to ignore
     * @return the metadata repository path
     */
    private static String doBuildMetadataPath(final String resourceType, String identity) {

        // TODO: This needs to handle a non-encoded tuple!
        StringBuilder metdataPath = new StringBuilder(METADATA_REPOSITORY_PATH);
        metdataPath.append(resourceType.toLowerCase());
        metdataPath.append("/");
        if (identity != null) {
            if (identity.contains("/")) {
                // Need to handle encoding so this is transparent to the caller
                if (AdminMetadataConstants.RESOURCE_TYPE_SERVER.equals(resourceType)) {
                    // Server ID: host,wlpUserDir,server
                    identity = encodeServerTuple(identity);
                }
                if (AdminMetadataConstants.RESOURCE_TYPE_APPLICATION.equals(resourceType)) {
                    // Application ID: host,wlpUserDir,server,app
                    // Application ID: cluster,app
                    final int firstComma = identity.indexOf(',');
                    final int lastComma = identity.lastIndexOf(',');
                    if (firstComma != lastComma) {
                        // Application on Server case
                        final String serverTuple = identity.substring(0, lastComma);
                        identity = encodeServerTuple(serverTuple) + "," + identity.substring(lastComma + 1);
                    }
                }
                if (AdminMetadataConstants.RESOURCE_TYPE_RUNTIME.equals(resourceType)) {
                    // Runtime ID: host,installDir
                    final int firstComma = identity.indexOf(',');
                    final String host = identity.substring(0, firstComma);
                    final String runtime = identity.substring(firstComma + 1);
                    // runtime has a slash, so we're NOT encoded
                    identity = host + "," + getURLEncodedPath(runtime);
                }
            }
            metdataPath.append(identity.trim());
            metdataPath.append("/");
        }

        return metdataPath.toString();
    }

    /**
     * Given a repository path, extract the necessary components for computing
     * a server tuple. If the path is not server path, an IllegalArgumentException
     * will be thrown.
     * 
     * @param path The path to use as the basis of the tuple
     * @param decodeUserDir Indicate whether the userdir component of the path
     *            should be decoded. Server tuples should NOT be encoded when
     *            being exposed through externals.
     * @return
     * @throws IllegalArgumentException
     */
    private static final String getServerTuple(String path, boolean decodeUserDir) throws IllegalArgumentException {

        final String method = "getServerTuple";

        isValidString(method, "path", path);

        if (path.matches(HOST_REPOSITORY_PATH + "(.+)/userdirs/(.+)/servers/(.+)")) {
            String[] nodes = path.split("/");
            if (decodeUserDir) {
                return nodes[4] + "," + decodeURLEncodedDir(nodes[6]) + "," + nodes[8];
            } else {
                return nodes[4] + "," + nodes[6] + "," + nodes[8];
            }

        }
        throw new IllegalArgumentException("getServerTuple: path is not a server repository path");
    }

    /**
     * Extracts the server tuple from the given path. If the path is not a
     * server repository path, an IllegalArgumentException will be thrown.
     * 
     * @param path A server repository path. Must not be {@code null} or empty.
     * @return A server tuple, or {@code} null if the path is not a server
     *         repository path
     * @throws IllegalArgumentException if the path is not a server repository
     *             path
     */
    public static final String getServerTuple(String path) throws IllegalArgumentException {
        return getServerTuple(path, true);
    }

    /**
     * Extracts the server tuple from the given path. If the path is not a
     * server repository path, an IllegalArgumentException will be thrown.
     * <p>
     * An encoded server tuple is defined as (hostName,urlEncodedUserDir,serverName)
     * 
     * @see #getServerTuple(String, boolean)
     * @param path A server repository path. Must not be {@code null} or empty.
     * @param decodeUserDir decode urlEncodedUserDir
     * @return A server tuple, or {@code} null if the path is not a server
     *         repository path
     * @throws IllegalArgumentException if the path is not a server repository
     *             path
     */
    public static final String getEncodedServerTuple(String path) throws IllegalArgumentException {
        return getServerTuple(path, false);
    }

    /**
     * Convert a server tuple (which is normally passed in from an external API)
     * to its encoded version (which is normally used when stored in the repository).
     * If the tuple is already encoded, nothing is done.
     */
    public static final String encodeServerTuple(String tuple) {
        final String method = "encodeServerTuple";

        isValidString(method, "tuple", tuple);
        final String[] parts = splitServerTuple(tuple);
        if (parts[1].contains("/")) {
            String hostName = parts[0];
            String wlpUserDir = parts[1];
            String serverName = parts[2];

            String encodedWlpUserDir = RepositoryPathUtility.getURLEncodedPath(wlpUserDir);

            StringBuilder sb = new StringBuilder();
            sb.append(hostName);
            sb.append(",");
            sb.append(encodedWlpUserDir);
            sb.append(",");
            sb.append(serverName);

            return sb.toString();
        } else {
            return tuple;
        }
    }

    /**
     * Convert an encoded server tuple (which is normally stored in the repository)
     * to its decoded version (which is normally used when returned externally).
     */
    public static final String decodeServerTuple(String tuple) {
        final String method = "decodeServerTuple";

        isValidString(method, "tuple", tuple);
        String hostName = tuple.substring(0, tuple.indexOf(','));
        String encodedWlpUserDir = tuple.substring(tuple.indexOf(',') + 1, tuple.lastIndexOf(','));
        String serverName = tuple.substring(tuple.lastIndexOf(',') + 1);

        String wlpUserDir = RepositoryPathUtility.decodeURLEncodedDir(encodedWlpUserDir);

        StringBuilder sb = new StringBuilder();
        sb.append(hostName);
        sb.append(",");
        sb.append(wlpUserDir);
        sb.append(",");
        sb.append(serverName);

        return sb.toString();
    }

    /**
     * @see com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean#buildServerRepositoryPath(String, String, String)
     */
    public static final String buildServerTuple(String hostName, String wlpUserDir, String serverName) {
        final String method = "buildServerTuple";

        isValidString(method, "hostName", hostName);
        isValidString(method, "wlpUserDir", wlpUserDir);
        isValidString(method, "serverName", serverName);

        StringBuilder path = new StringBuilder(hostName.toLowerCase());
        path.append(",");
        path.append(wlpUserDir);
        path.append(",");;
        path.append(serverName);
        return path.toString();
    }

    /**
     * An encoded server tuple is defined as (hostName,urlEncodedUserDir,serverName).
     * <p>
     * Server tuples should NOT be encoded when being exposed through externals.
     * 
     * @see #buildServerTuple(String, String, String)
     */
    public static final String buildEncodedServerTuple(String hostName, String urlEncodedUserDir, String serverName) {
        return buildServerTuple(hostName, urlEncodedUserDir, serverName);
    }

    public static final String buildInstallDirPath(String hostName, String type, String wlpInstallDir) {

        // Build the install dir path for the repository 
        StringBuilder path = new StringBuilder(HOST_REPOSITORY_PATH);
        path.append(hostName.toLowerCase());
        path.append("/installdirs/");

        if (type == null || type.length() == 0)
            type = "other"; //default is other

        path.append(type);

        if (!wlpInstallDir.startsWith("/")) {
            path.append("/");
        }

        path.append(wlpInstallDir);
        path.append("/");
        return path.toString();

    }

    public static final String buildUserDirPath(String hostName, String wlpUserDir) {

        // Build the user dir path for the repository 
        StringBuilder path = new StringBuilder(HOST_REPOSITORY_PATH);
        path.append(hostName.toLowerCase());
        if (wlpUserDir.startsWith("/")) {
            path.append("/userdirs");
        } else {
            path.append("/userdirs/");
        }
        path.append(wlpUserDir);
        path.append("/");
        return path.toString();

    }

    /**
     * Validate a string.
     * 
     * @param method calling to validate the field
     * @param name of the field
     * @param value of the field
     */
    private static void isValidString(String method, String name, String value) {

        if (value == null) {
            throw new IllegalArgumentException(method + ": " + name + " is null");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException(method + ": " + name + " is empty");
        }
    }

    /**
     * Safely splits the tuple into its component parts.
     * If the input is not a tuple, then null is returned. Otherwise the return
     * is an array with 3 indices:
     * split[0] - the host name
     * split[1] - the user dir (encoded if the input was encoded)
     * split[2] - the server name
     * 
     * @param tuple The server tuple to split (in host,userdir,server format)
     * @return The server tuple split into its component parts, an array of 3 elements: host|userdir|name or {@code null} if the input was not a tuple
     */
    public static String[] splitServerTuple(String tuple) {
        return getServerTupleComponents(tuple);
    }

    /**
     * Safely splits the tuple into its component parts.
     * If the input is not a tuple, then null is returned. Otherwise the return
     * is an array with 2 indices:
     * split[0] - the host name
     * split[1] - the install dir (encoded if the input was encoded)
     * 
     * @param tuple The runtime tuple to split (in host,installdir format)
     * @return The server tuple split into its component parts, an array of 2 elements: host|installdir or {@code null} if the input was not a tuple
     */
    public static String[] splitRuntimeTuple(String tuple) {
        if (tuple == null) {
            return null;
        }

        int firstComma = tuple.indexOf(',');

        String[] tupleComponents = new String[2];
        tupleComponents[0] = tuple.substring(0, firstComma);
        tupleComponents[1] = tuple.substring(firstComma + 1);

        return tupleComponents;

    }
}
