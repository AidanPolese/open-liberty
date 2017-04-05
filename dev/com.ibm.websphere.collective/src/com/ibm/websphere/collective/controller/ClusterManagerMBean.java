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

import java.util.Collection;
import java.util.Map;

import com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean;

/**
 * The ClusterManagerMBean defines the management interface for clusters.
 * <p>
 * The ClusterManager MBean provides operations to manage servers at the
 * cluster level. It is used to stop and start a cluster as well as get
 * member, status, and cluster name information. It is also used to generate
 * the web server plugin-cfg.xml.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * 
 * @ibm-api
 */
public interface ClusterManagerMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=collectiveController,type=ClusterManager,name=ClusterManager";

    /**
     * Exception key for the operation result map.
     * <p>
     * The Exception object thrown by the operation.
     */
    String OPERATION_EXCEPTION = "Exception";

    /**
     * Exception message key for the operation result map.
     * <p>
     * The value of the Exception message thrown by the operation.
     */
    String OPERATION_EXCEPTION_MESSAGE = "ExceptionMessage";

    /**
     * Status value for a fully started cluster (all members are started).
     * <p>
     * Returned by the getStatus() method.
     */
    String STATUS_STARTED = "STARTED";

    /**
     * Status value for a cluster which is starting, as initiated from this MBean.
     * <p>
     * Returned by the getStatus() method.
     */
    String STATUS_STARTING = "STARTING";

    /**
     * Status value for a partially started cluster (some members are started).
     * <p>
     * Returned by the getStatus() method.
     */
    String STATUS_PARTIALLY_STARTED = "PARTIALLY STARTED";

    /**
     * Status value for a cluster that is stopping, as initiated by this MBean.
     * <p>
     * Returned by the getStatus() method.
     */
    String STATUS_STOPPING = "STOPPING";

    /**
     * Status value for a fully stopped cluster (no members are started).
     * <p>
     * Returned by the getStatus() method.
     */
    String STATUS_NOT_STARTED = "NOT STARTED";

    /**
     * Get a list of all cluster names known by this collective controller.
     * 
     * @return A list of all cluster names known by this collectiveController. {@code null} is not returned.
     */
    Collection<String> listClusterNames();

    /**
     * Get a list of the server tuples which are members of the specified cluster.
     * <p>
     * A server tuple is defined as (hostName,wlpUserDir,serverName).
     * See {@link RepositoryPathUtilityMBean} for more on a server tuples.
     * 
     * @param clusterName The name of the cluster for which the member list will be generated.
     *            Must not be {@code null} or an empty String.
     * @return A list of the server tuples which are members of the specified cluster,
     *         or {@code null} if no such cluster exists with that name.
     * @throws IllegalArgumentException If the clusterName is {@code null} or empty.
     */
    Collection<String> listMembers(String clusterName)
                    throws IllegalArgumentException;

    /**
     * Get the status of the cluster as reported by the collective controller.
     * The status can be one of:
     * <ul>
     * <li>{@value #STATUS_STARTED}, indicating all cluster members are started</li>
     * <li>{@value #STATUS_PARTIALLY_STARTED}, indicating some cluster members are started</li>
     * <li>{@value #STATUS_NOT_STARTED}, indicating no cluster members are started</li>
     * </ul>
     * 
     * @param clusterName The name of the cluster for which the status will be obtained.
     *            Must not be {@code null} or an empty String.
     * @return The status of the cluster. One of {@value #STATUS_STARTED}, {@value #STATUS_PARTIALLY_STARTED},
     *         or {@value #STATUS_NOT_STARTED} will be returned. If the cluster does not
     *         exist, {@code null} is returned.
     * @throws IllegalArgumentException If the clusterName is {@code null} or empty
     */
    String getStatus(String clusterName)
                    throws IllegalArgumentException;

    /**
     * Get the name of the cluster that the specified server belongs to.
     * 
     * @param hostName The host name of the server. Must not be {@code null} or an empty string.
     *            This host name should match the host name set to the defaultHostName
     *            variable for the server's server.xml
     * @param wlpUserDir The canonical path for the user directory of server.
     *            This should match the WLP_USER_DIR environment variable for the server.
     *            Must not be {@code null} or an empty string. Must not have a trailing slash.
     *            Must not be encoded.
     * @param serverName The server name. Must not be {@code null} or an empty string.
     * @return The name of the cluster which the specified server belongs to, or {@code null} if the
     *         specified server does not belong to a cluster.
     * @throws IllegalArgumentException Thrown if any supplied parameter is null or empty,
     *             or if the specified server is not known to the collective controller.
     */
    String getClusterName(String hostName, String wlpUserDir, String serverName)
                    throws IllegalArgumentException;

    /**
     * Start all of the servers in the specified cluster.
     * 
     * @param clusterName The name of the cluster which should be started.
     *            Must not be {@code null} or an empty String.
     * @param options Options supported by the wlp/bin/server script "start" action. May be {@code null} or an empty string.
     * @return A map of cluster member names mapped to the status of the start
     *         operation as reported by the ServerCommandsMBean. If the operation
     *         caused an Exception, the Exception is stored in the map under the
     *         key {@link #OPERATION_EXCEPTION} and the Exception message is
     *         stored in the map under key {@link #OPERATION_EXCEPTION_MESSAGE}.
     * @see ServerCommandsMBean
     * @throws IllegalArgumentException If the clusterName was {@code null}, empty,
     *             if the cluster is not defined or if the cluster has no members.
     */
    Map<String, Map<String, Object>> startCluster(String clusterName, String options)
                    throws IllegalArgumentException;

    /**
     * Stops all of the servers in the specified cluster.
     * 
     * @param clusterName The name of the cluster which should be stopped.
     *            Must not be {@code null} or an empty String.
     * @param options Options supported by the wlp/bin/server script "stop" action. May be {@code null} or an empty string.
     * @return A map of cluster member names mapped to the status of the stop operation,
     *         as reported by the ServerCommandsMBean. If the operation
     *         caused an Exception, the Exception is stored in the map under the
     *         key {@link #OPERATION_EXCEPTION} and the Exception message is
     *         stored in the map under key {@link #OPERATION_EXCEPTION_MESSAGE}.
     * @see ServerCommandsMBean
     * @throws IllegalArgumentException If the clusterName was {@code null}, empty,
     *             if the cluster is not defined or if the cluster has no members.
     */
    Map<String, Map<String, Object>> stopCluster(String clusterName, String options)
                    throws IllegalArgumentException;

    /**
     * Merges web server plugin-cfg.xml files from cluster members in a given
     * cluster.
     * 
     * This operation only merges plugins files from started cluster members. {@link #getStatus(String)} operation can be called to
     * determine whether all members are started in a given cluster.
     * 
     * The merged plugin file is stored in controller's output directory.
     * The file can be transferred to client using FileTransferMBean.
     * 
     * @param clusterName The name of the cluster.
     *            Must not be {@code null} or an empty String.
     * @return The path to the merged plugin-cfg.xml on collective controller.
     * @throws IllegalArgumentException If the clusterName was {@code null}, empty,
     *             if the cluster is not defined or if the cluster has no members.
     */
    String generateClusterPluginConfig(String clusterName)
                    throws IllegalArgumentException;

}
