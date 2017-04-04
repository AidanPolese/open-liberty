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
package com.ibm.websphere.collective.repository;

/**
 * The RepositoryPathUtilityMBean provides utility methods to construct
 * repository node paths and server tuples.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * <p>
 * A server tuple is defined as (hostName,wlpUserDir,serverName).
 * The host name is always in lower case. The wlpUserDir is not encoded.
 * The elements of the tuple can always be safely parsed as follows:
 * <p>
 * 
 * <code>
 * String hostName = tuple.substring(0, tuple.indexOf(<Q>,</Q>));<br>
 * String wlpUserDir = tuple.substring(tuple.indexOf(<Q>,</Q>) + 1, tuple.lastIndexOf(<Q>,</Q>));<br>
 * String serverName = tuple.substring(tuple.lastIndexOf(<Q>,</Q>) + 1);<br>
 * </code>
 * 
 * @ibm-api
 */
public interface RepositoryPathUtilityMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=collectiveController,type=RepositoryPathUtility,name=RepositoryPathUtility";

    /**
     * Normalizes a file system path to be consistent when used within the collective.
     * Normalization consists of converting all \ to /, and collapsing relative paths,
     * as well as removing duplicate slashes.
     * <p>
     * Windows drive-letters are always converted to their upper-case form
     * to ensure consistency.
     * <p>
     * e.g. {@code RepositoryPathUtility.getURLEncodedPath("c:\\wlp\\usr\\") } will result in: {@code C%3A%2Fwlp%2Fusr} <p>
     * <p>
     * Note that URL-style prefixing, such as file:// is stripped.
     * <p>
     * e.g. {@code RepositoryPathUtility.getURLEncodedPath("file:/c:/wlp/usr") } will result in: {@code C%3A%2Fwlp%2Fusr} <p>
     * 
     * @param path The file system path to be normalized. May be {@code null}.
     * @return The normalized path
     */
    String normalizePath(String path);

    /**
     * Encodes an OS file system path with a UTF-8 URL encoding.
     * <p>
     * The encoded form can be used in the repository as a path element.
     * See {@link #normalizePath(String)} for details on the resulting path.
     * 
     * @param path The OS filesystem path to encode. Must not be {@code null}.
     * @return The URL encoded path, without a trailing slash
     */
    String getURLEncodedPath(String path);

    /**
     * Builds the host's path in the repository.
     * <p>
     * The host name will automatically converted to lower-case.
     * 
     * @param hostName The host name. Must not be {@code null} or empty.
     * @return The path to the host in the repository, with the trailing slash.
     */
    String buildHostRepositoryPath(String hostName);

    /**
     * Builds the server's path in the repository.
     * <p>
     * A server is uniquely identified by its host name, its user dir, and
     * its server name.
     * <p>
     * The host name will automatically converted to lower-case.
     * 
     * @param hostName The host name for the server. Must not be {@code null} or empty.
     * @param urlEncodedUserDir The URL encoded canonical path for the user directory of server. Must not be {@code null} or empty.
     * @param serverName The name of the server. Must not be {@code null} or empty.
     * @return The path to the server in the repository, with the trailing slash.
     * @see #getURLEncodedPath(String)
     */
    String buildServerRepositoryPath(String hostName, String urlEncodedUserDir, String serverName);

    /**
     * Builds the server's path in the repository from a server tuple.
     * <p>
     * The host name will automatically be converted to lower-case.
     * 
     * @param server tuple
     * @return The path to the server in the repository, with the trailing slash.
     * @see #getServerTuple(String)
     */
    String buildServerRepositoryPath(String serverTuple);

    /**
     * Extracts the server tuple from the given path. If the path is not a
     * server repository path, an IllegalArgumentException will be thrown.
     * 
     * @param path A server repository path. Must not be {@code null} or empty.
     * @return A server tuple, or {@code} null if the path is not a server
     *         repository path
     * @throws IllegalArgumentException If the path is not a server repository
     *             path
     */
    String getServerTuple(String path) throws IllegalArgumentException;

    /**
     * Builds the server tuple from the given host name, wlp user directory and
     * server name.
     * 
     * @param hostName The host name for the server. Must not be {@code null} or empty.
     * @param wlpUserDir The canonical path for the user directory of server. Must not be {@code null} or empty.
     * @param serverName The name of the server. Must not be {@code null} or empty.
     * @return The server tuple
     * @see #getURLEncodedPath(String)
     */
    String buildServerTuple(String hostName, String wlpUserDir, String serverName);

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
    String[] splitServerTuple(String tuple);

}
