package com.ibm.websphere.collective.repository;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

/**
 * RepositoryConfigurationMBean defines the administrative interface for replica
 * set configuration.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * <p>
 * Replicas may be added and removed from a live replica set. Additionally, the
 * entire replica set may be redefined, as long as one of the replicas in the
 * previous set exists in the new set.
 * <p>
 * All endpoints specified to this MBean expect the replicaHost and replicaPort
 * values defined in the server.xml of each replica. For example:
 * <p>
 * 
 * <pre>
 * &lt;collectiveController replicaHost="localhost" replicaPort="10011" /&gt;
 * </pre>
 * 
 * The endpoint would be "localhost:10011".
 * 
 * @ibm-api
 */
public interface RepositoryConfigurationMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this
     * MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=collectiveController,type=RepositoryConfiguration,name=RepositoryConfiguration";

    /**
     * This method returns the configured replicas as Set<String>
     * <p>
     * A replica gets into this set:
     * <ul>
     * <li>Automatically when it is in the initial replica set in the configuration</li>
     * <li>When the "addReplica" command is invoked after the replica has been started</li>
     * </ul>
     * A replica gets out this set when:
     * <ul>
     * <li>"removeReplica" command is invoked</li>
     * </ul>
     * This set includes the active set and stopped/unreachable added replicas
     * 
     * @return A copy of configured in the form of a set of Strings of the endpoints "replica_hostname:replicaPort". Returns an empty list if there are no configured endpoints.
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    Set<String> getConfiguredReplicas() throws IOException,
                    IllegalArgumentException, IllegalStateException;

    /**
     * This method returns the active replicas as Set<String>
     * <p>
     * A replica gets into this set:
     * <ul>
     * <li>Automatically when it is in the initial replica set in the configuration</li>
     * <li>When the "addReplica" command is invoked after the replica has been started</li>
     * </ul>
     * A replica gets out this set when:
     * <ul>
     * <li>"removeReplica" command is invoked</li>
     * <li>The server is stopped or unreachable</li>
     * </ul>
     * This set is also part of the configured set
     * 
     * @return A copy of the active in the form of a set of Strings of the endpoints in the form "replica_hostname:replicaPort". Returns an empty list if there are no active
     *         endpoints.
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    Set<String> getActiveReplicas() throws IOException,
                    IllegalArgumentException, IllegalStateException;

    /**
     * This method returns the standby replicas as Set<String>
     * <p>
     * A replica gets into this set when:
     * <ul>
     * <li>It is started out of the configuration</li>
     * <li>It was removed (while running) using "removeReplica</li>
     * </ul>
     * A replica gets out this set when:
     * <ul>
     * <li>"addReplica" command is invoked</li>
     * <li> The server is stopped or unreachable</li>
     * </ul>
     * 
     * @return list of endpoint Strings of the form "replica_hostname:replicaPort". Returns an empty list if there are no standby endpoints.
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    Set<String> getStandbyReplicas() throws IOException,
                    IllegalArgumentException, IllegalStateException;

    /**
     * This method returns the union of the active set, the standby set, and the configured set as set of Map<String, String>
     * <p>
     * This method assigns values of endpoint, status(STARTED/STOPPED), standby(TRUE/FALSE) to all of the replicas
     * 
     * @return a set of the union of the active set, the standby set, and the configured set
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    Set<Map<String, String>> getAllReplicas() throws IOException,
                    IllegalArgumentException, IllegalStateException;

    /**
     * Adds a replica endpoint to the active replica set.
     * <p>
     * Only endpoints that are part of the stand-by set can be added. If the
     * endpoint is not part of the active or stand-by replica sets, then an
     * IllegalStateException will be thrown. If the endpoint is already part
     * of the active replica set, then no change will be made and {@code false} will be returned.
     * <p>
     * If the endpoint is malformed, an IllegalArgumentException will be thrown.
     * 
     * @param endpoint
     *            The identifier for a replica in the form "host:port".
     * @return {@code true} if the replica endpoint was added, {@code false} if
     *         the replica endpoint was already part of the set
     * @throws IOException
     *             If there was a problem completing the request.
     * @throws IllegalArgumentException
     *             If the endpoint is not properly formatted. The input must be in the format "host:port".
     * @throws UnknownHostException
     *             If the host specified in the endpoint cannot be resolved via DNS.
     * @throws IllegalStateException
     *             If the endpoint is properly formatted but it is not part of the stand-by or active replica set.
     */
    boolean addReplica(String endpoint) throws IOException,
                    IllegalArgumentException, UnknownHostException, IllegalStateException;

    /**
     * Redefines the active replica set.
     * <p>
     * The new active replica set will be comprised solely of the supplied
     * endpoints. The replicas for reconfiguration are represented as a space
     * delimited list endpoints. Only endpoints that are part of the active or
     * stand-by replica sets can be specified. If the endpoints are not part of
     * the active or stand-by replica sets, then an IllegalStateException will
     * be thrown.
     * <p>
     * If any of endpoints are malformed, an IllegalArgumentException will be
     * thrown.
     * 
     * @param endpoints
     *            A space delimited list of the replicas of the reconfigured
     *            replica set in the form "host:port host:port".
     * @return {@code true} if the replica reconfiguration was successful, {@code false} if the operation was not successful.
     * @throws IOException
     *             If there was a problem completing the request.
     * @throws IllegalArgumentException
     *             If the endpoint is not properly formatted. The input must be in the format "host:port".
     * @throws UnknownHostException
     *             If any of the hosts specified in the endpoints cannot be resolved via DNS.
     * @throws IllegalStateException
     *             If the endpoints are properly formatted but at least one is not part of the stand-by or active replica set.
     */
    boolean reconfigureReplicas(String endpoints) throws IOException,
                    IllegalArgumentException, UnknownHostException;

    /**
     * Removes a replica endpoint from the existing active replica set.
     * <p>
     * Only endpoints that are part of the configured replica set can be
     * removed. If the endpoint is not part of the active replica set,
     * then no change will be made and {@code false} will be returned.
     * <p>
     * If the endpoint is malformed, an IllegalArgumentException will be thrown.
     * 
     * @param endpoint
     *            The identifier for a replica in the form "host:port".
     * @return {@code true} if the replica endpoint was removed, {@code false} if the endpoint was not part of the active replica set.
     * @throws IOException
     *             If there was a problem completing the request.
     * @throws IllegalArgumentException
     *             If the endpoint is not properly formatted. The input must be in the format "host:port"
     * @throws UnknownHostException
     *             If the host specified in the endpoint cannot be resolved via DNS.
     */
    public boolean removeReplica(String endpoint) throws IOException,
                    IllegalArgumentException, UnknownHostException;

    /**
     * This method returns the ID of the current replica
     * 
     * @return the current replica ID as a String in the format HOST:PORT
     */
    String getReplicaId();

}
