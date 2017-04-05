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

package com.ibm.websphere.collective.controller;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;

import javax.management.NotificationEmitter;

/**
 * The ServerCommandsMBean defines the management interface for servers within a collective.
 * <p>
 * The ServerCommands MBean provides operations to start and stop servers in a collective.
 * It can also be used to get the status of a server.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * <p>
 * All operations performed return a Map of the results.
 * <h1>Operation Result Map</h1>
 * <table border="1">
 * <tr>
 * <th>Property Name</th>
 * <th>Description</th>
 * <th>Data Type / Format</th>
 * </tr>
 * <tr>
 * <td>{@value #OPERATION_RETURN_CODE}</td>
 * <td>The exit code / return code from the execution of the operation</td>
 * <td>Integer</td>
 * </tr>
 * <tr>
 * <td>{@value #OPERATION_STDOUT}</td>
 * <td>The standard output (stdout) from the execution of the operation</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>{@value #OPERATION_STDERR}</td>
 * <td>The standard error (stderr) from the execution of the operation</td>
 * <td>String</td>
 * </tr>
 * </table>
 * 
 * @ibm-api
 */
public interface ServerCommandsMBean extends NotificationEmitter {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=collectiveController,type=ServerCommands,name=ServerCommands";

    /**
     * Return code for the operation result map.
     * <p>
     * This is the return code reported by the operation.
     */
    String OPERATION_RETURN_CODE = "returnCode";

    /**
     * The contents of stdout for the operation result map.
     * <p>
     * This is the contents of standard output stream for the operation.
     */
    String OPERATION_STDOUT = "stdout";

    /**
     * The contents of stderr for the operation result map.
     * <p>
     * This is the contents of standard error stream for the operation.
     */
    String OPERATION_STDERR = "stderr";

    /**
     * Status value for a started server.
     * <p>
     * This is equivalent to a status return code of 0.
     */
    String STATUS_STARTED = "STARTED";

    /**
     * Status value for a server which is starting, as initiated from this MBean.
     * <p>
     * Returned by the getStatus() method.
     */
    String STATUS_STARTING = "STARTING";

    /**
     * Status value for a server that is stopping, as initiated by this MBean.
     * <p>
     * Returned by the getStatus() method.
     */
    String STATUS_STOPPING = "STOPPING";

    /**
     * Status value of a stopped server.
     * <p>
     * This is equivalent to a status return code of 1.
     */
    String STATUS_STOPPED = "STOPPED";

    /**
     * Status value for a server which does not exist.
     * <p>
     * This is equivalent to a status return code of 2.
     */
    String STATUS_NOT_FOUND = "NOT_FOUND";

    /**
     * Start a registered server collective member.
     * 
     * @param hostName The host name on which the target server resides.
     *            Must not be {@code null} or an empty string. This host name should
     *            match the host name set to the defaultHostName variable for the
     *            server's server.xml
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. If serverName is {@code null}, the defaultServer
     *            is assumed. Must not be an empty string.
     * @param options Options supported by the wlp/bin/server script "start" action. May be {@code null} or an empty string.
     * @return An Operation Result Map.
     * @throws ConnectException Signals there are problems connecting to the target machine.
     * @throws IOException Signals there are problems during the remote operation.
     * @throws IllegalArgumentException If an input parameter has a value that is not valid
     */
    Map<String, Object> startServer(String hostName, String wlpUserDir, String serverName, String options)
                    throws ConnectException, IOException, IllegalArgumentException;

    /**
     * Stop a registered server collective member.
     * 
     * @param hostName The host name on which the target server resides.
     *            Must not be {@code null} or an empty string. This host name should
     *            match the host name set to the defaultHostName variable for the
     *            server's server.xml
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The name of the server to be stopped. If serverName is null, defaultServer will be used.
     * @param options Options supported by the wlp/bin/server script "stop" action. May be {@code null} or an empty string.
     * @return An Operation Result Map.
     * @throws ConnectException Signals there are problems connecting to the target machine.
     * @throws IOException Signals there are problems during the remote operation.
     * @throws IllegalArgumentException If an input parameter has a value that is not valid
     */
    Map<String, Object> stopServer(String hostName, String wlpUserDir, String serverName, String options)
                    throws ConnectException, IOException, IllegalArgumentException;

    /**
     * Get the status of a registered server collective member.
     * 
     * @param hostName The host name on which the target server resides.
     *            Must not be {@code null} or an empty string. This host name should
     *            match the host name set to the defaultHostName variable for the
     *            server's server.xml
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. If serverName is {@code null}, the defaultServer
     *            is assumed. Must not be an empty string.
     * @return {@value #STATUS_STARTED} when the server is started, {@value #STATUS_STOPPED} when the server is stopped,
     *         or {@value #STATUS_NOT_FOUND} when the server is not found.
     * @throws IOException If there is a problem accessing the collective repository
     * @throws IllegalArgumentException If an input parameter has a value that is not valid
     */
    String getServerStatus(String hostName, String wlpUserDir, String serverName)
                    throws IOException, IllegalArgumentException;

    /**
     * Generate a Java dump of the current process.
     * 
     * @param hostName The host name on which the target server resides.
     *            Must not be {@code null} or an empty string. This host name should
     *            match the host name set to the defaultHostName variable for the
     *            server's server.xml
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. If serverName is {@code null}, the defaultServer
     *            is assumed. Must not be an empty string.
     * @param options Options supported by the wlp/bin/server script "javadump" action. May be {@code null} or an empty string.
     * @return An Operation Result Map.
     * @throws ConnectException Signals there are problems connecting to the target machine.
     * @throws IOException Signals there are problems during the remote operation.
     * @throws IllegalArgumentException If an input parameter has a value that is not valid
     */
    Map<String, Object> javadumpServer(String hostName, String wlpUserDir, String serverName, String options)
                    throws ConnectException, IOException, IllegalArgumentException;

}
