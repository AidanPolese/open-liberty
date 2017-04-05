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

/**
 * This MBean provides routing context for MBean operations used with the
 * IBM JMX REST Connector.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * 
 * @ibm-api
 */
/*
 * Internal implementation notes:
 * While there is no class which directly implements this interface, it exists
 * to serve as external documentation on the methods supported by the JMX REST
 * connector's routing capabilities.
 */
public interface RoutingContextMBean {

    /**
     * A string representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    public final static String OBJECT_NAME = "WebSphere:feature=collectiveController,type=RoutingContext,name=RoutingContext";

    /**
     * This method assigns a server within the given host, user directory and
     * server name to the routing context. This is used when routing requests
     * to a specific target server.
     * <p>
     * Calling this method will override any previous context assignments.
     * 
     * @param hostName The host name. Must not be {@code null} or an empty string.
     *            This host name should match the host name set to the defaultHostName
     *            Must not be {@code null} or an empty string.
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. Must not be {@code null} or an empty string.
     */
    void assignServerContext(String hostName, String wlpUserDir, String serverName);

    /**
     * This method assigns a host to the routing context. This is used when routing
     * requests to a specific host without specifying any particular target servers.
     * <p>
     * Calling this method will override any previous context assignments.
     * 
     * @param hostName The host name. Must not be {@code null} or an empty string.
     */
    void assignHostContext(String hostName);

}
