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

import java.util.List;
import java.util.Map;

import javax.management.NotificationEmitter;

import com.ibm.websphere.collective.repository.RepositoryPathUtilityMBean;

/**
 * The MaintenanceModeMBean defines the management interface for maintenance mode operations
 * for servers and hosts within a collective.
 * <p>
 * Set maintenance mode before you perform diagnostic tests, maintenance, or tuning on a host
 * or server. Maintenance mode can prevent the disruption of client requests by routing client
 * traffic that is targeted for a server or host that is in maintenance mode to another server
 * or host.
 * <p>
 * Setting a server into maintenance mode is a persistent change. A server remains in
 * maintenance mode even if the server is restarted until the mode is explicitly changed.
 * <p>
 * When a server that has the scaling member feature is in maintenance mode, the scaling
 * controller cannot control that server. The server does not count toward the minimum or
 * maximum running instances setting for the cluster. When the server is placed into
 * maintenance mode, the scaling controller will start an alternate server if necessary
 * to meet the required minimum number of running instances or to meet current workload demand.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * 
 * @ibm-api
 */
public interface MaintenanceModeMBean extends NotificationEmitter {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=collectiveController,type=MaintenanceMode,name=MaintenanceMode";

    /**
     * Status value indicating a host or server is in maintenance mode.
     */
    String STATUS_IN_MAINTENANCE_MODE = "inMaintenanceMode";

    /**
     * Status value indicating a host or server is not in maintenance mode.
     */
    String STATUS_NOT_IN_MAINTENANCE_MODE = "notInMaintenanceMode";

    /**
     * Status value indicating that an alternate server is starting to replace
     * this server. Maintenance mode is set when the alternate server is started.
     */
    String STATUS_ALTERNATE_SERVER_IS_STARTING = "alternateServerStarting";

    /**
     * Status value indicating maintenance mode was not set because an alternate
     * server was not found.
     */
    String STATUS_ALTERNATE_SERVER_IS_NOT_AVAILABLE = "alternateServerUnavailable";

    /**
     * Status value indicating the host or server was not found.
     */
    String STATUS_NOT_FOUND = "notFound";

    /**
     * Status value indicating the requested operation could not be completed because of an
     * unexpected error. The server logs should have more information about the error.
     */
    String STATUS_FAILURE = "error";

    /**
     * Sets one or more registered hosts in a collective into maintenance mode.
     * 
     * Setting a host into maintenance mode prevents the scaling controller from provisioning
     * new servers on the host. It also places all servers on the host into maintenance mode.
     * 
     * @param hostNames
     *            A list of host names.
     * @param maintainAffinity
     *            If {@code true} session affinity is kept (requests that have session affinity
     *            to a server in maintenance mode are still routed to the server).
     *            If {@code false} session affinity is broken immediately.
     * @param force
     *            If {@code true} maintenance mode is set regardless of the auto-scaling policy.
     *            If {@code false} maintenance mode is not set if it causes a violation of the
     *            auto-scaling policy because the minimum instance requirement can't be met
     *            or resource usage goes beyond the threshold specified in the auto-scaling policy.
     *            Note that this evaluation is done independently for each host in the hostName list,
     *            so if {@code force} is {@code false}, some hosts in the list may be placed
     *            in maintenance mode while others may not due to policy violation.
     * @return A list of maps which describe the outcome of the request.
     *         The list entry order corresponds with the hostNames list.
     *         Each map has an entry for the host name and entries for the server tuples on the host.
     *         The map values are status values such as STATUS_IN_MAINTENANCE_MODE,
     *         STATUS_ALTERNATE_SERVER_IS_STARTING, STATUS_ALTERNATE_SERVER_IS_NOT_AVAILABLE,
     *         STATUS_NOT_FOUND, or STATUS_FAILURE.
     *         If an error occurred, the map contains status values only for the host and/or
     *         server tuple(s) causing the error.
     */
    List<Map<String, String>> enterHostMaintenanceMode(List<String> hostNames, boolean maintainAffinity, boolean force);

    /**
     * Sets one or more registered servers in a collective into maintenance mode.
     * 
     * @param serverTuples
     *            A list of server tuples. A server tuple is defined as
     *            (hostName,wlpUserDir,serverName). See {@link RepositoryPathUtilityMBean} for
     *            more information on a server tuples.
     * @param maintainAffinity
     *            If {@code true} session affinity is kept (requests that have
     *            session affinity to a server in maintenance mode are still routed to the server).
     *            If {@code false} session affinity is broken immediately.
     * @param force
     *            If {@code true} maintenance mode is set regardless of the auto-scaling policy.
     *            If {@code false} maintenance mode is not set if it causes a violation of the
     *            auto-scaling policy because the minimum instance requirement can't be met
     *            or resource usage goes beyond the threshold specified in the auto-scaling policy.
     *            Note that this evaluation is done independently for each server in the serverTuples list,
     *            so if {@code force} is {@code false}, some servers in the list may be placed
     *            in maintenance mode while others may not due to policy violation.
     * @return A list of maps which describe the outcome of the request.
     *         The list entry order corresponds with the serverTuples list.
     *         Each map has an entry for the server tuple.
     *         The map values are status values such as STATUS_IN_MAINTENANCE_MODE,
     *         STATUS_ALTERNATE_SERVER_IS_STARTING, STATUS_ALTERNATE_SERVER_IS_NOT_AVAILABLE,
     *         STATUS_NOT_FOUND, or STATUS_FAILURE.
     */
    List<Map<String, String>> enterServerMaintenanceMode(List<String> serverTuples, boolean maintainAffinity, boolean force);

    /**
     * Resets one or more registered hosts in a collective from maintenance mode.
     * 
     * Resetting a host from maintenance mode allows the scaling controller to provision
     * new servers on the host (if it is configured to do so). It also resets all servers
     * on the host from maintenance mode.
     * 
     * @param hostNames A list of host names.
     * @return A list of maps which describe the outcome of the request.
     *         The list entry order corresponds with the hostNames list.
     *         Each map has an entry for the host name and entries for the server tuples on the host.
     *         The map values are status values such as STATUS_NOT_IN_MAINTENANCE_MODE,
     *         STATUS_NOT_FOUND, or STATUS_FAILURE.
     *         If an error occurred, the map contains status values only for the host and/or
     *         server tuple(s) causing the error.
     */
    List<Map<String, String>> exitHostMaintenanceMode(List<String> hostNames);

    /**
     * Resets one or more registered servers in a collective from maintenance mode.
     * 
     * @param serverTuples A list of server tuples. A server tuple is defined as
     *            (hostName,wlpUserDir,serverName). See {@link RepositoryPathUtilityMBean} for
     *            more information on a server tuples.
     * @return A list of maps which describe the outcome of the request.
     *         The list entry order corresponds with the serverTuples list.
     *         Each map has an entry for the server tuple.
     *         The map values are status values such as STATUS_NOT_IN_MAINTENANCE_MODE,
     *         STATUS_NOT_FOUND, or STATUS_FAILURE.
     */
    List<Map<String, String>> exitServerMaintenanceMode(List<String> serverTuples);

    /**
     * Returns whether one or more registered hosts are in maintenance mode.
     * 
     * @param hostNames A list of host names.
     * @return A list of maps which describe the outcome of the request.
     *         The list entry order corresponds with the hostNames list.
     *         Each map has an entry for the host name and entries for the server tuples on the host.
     *         The map values are status values such as STATUS_IN_MAINTENANCE_MODE,
     *         STATUS_NOT_IN_MAINTENANCE_MODE, STATUS_ALTERNATE_SERVER_IS_STARTING,
     *         STATUS_NOT_FOUND, or STATUS_FAILURE.
     */
    List<Map<String, String>> getHostMaintenanceMode(List<String> hostNames);

    /**
     * Returns whether one or more registered servers are in maintenance mode.
     * 
     * @param serverTuples A list of server tuples. A server tuple is defined as
     *            (hostName,wlpUserDir,serverName). See {@link RepositoryPathUtilityMBean} for
     *            more information on a server tuples.
     * @return A list of maps which describe the outcome of the request.
     *         The list entry order corresponds with the serverTuples list.
     *         Each map has an entry for the server tuple.
     *         The map values are status values such as STATUS_IN_MAINTENANCE_MODE,
     *         STATUS_NOT_IN_MAINTENANCE_MODE, STATUS_ALTERNATE_SERVER_IS_STARTING,
     *         STATUS_NOT_FOUND, or STATUS_FAILURE.
     */
    List<Map<String, String>> getServerMaintenanceMode(List<String> serverTuples);
}
