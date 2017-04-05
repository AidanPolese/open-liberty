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

import java.io.IOException;
import java.security.AccessControlException;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The CollectiveRepositoryMBean defines basic CRUD and membership operations
 * to the Collective Repository.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * <p>
 * The Collective Repository stores data in a tree structure of "Nodes".
 * Each Node has path like name, starting from the root Node "/".
 * Nodes may hold data and may have child Nodes.
 * <p>
 * Example Node tree:
 * <p>
 * <ul>
 * <li>/a</li>
 * <li>/a/b1</li>
 * <li>/a/b1/c1</li>
 * <li>/a/b2</li>
 * </ul>
 * <p>
 * All Node paths are normalized such that additional delimiter slashes are
 * collapsed into a single slash. e.g. "////" resolves to "/" and "/a//b/"
 * resolve to "/a/b". The slash "/" character is the Node path delimiter, and
 * the slash "\" is treated as a normal character (not a delimiter), but its
 * use is highly discouraged. Any trailing whitespace is removed. Any leading
 * whitespace is not valid. Embedded whitespace is preserved. e.g. "/a/b c/d"
 * is a valid path, "/a/b/c " will resolve to "/a/b/c" and " /a/b/c" is not
 * valid.
 */
public interface CollectiveRepositoryMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=collectiveController,type=CollectiveRepository,name=CollectiveRepository";

    /**
     * The create operation creates a new Node with the specified nodeName
     * in the repository. The repository automatically creates all
     * intermediary Nodes necessary to reach the leaf name of the specified
     * Node if they do not already exist.
     * <p>
     * If the Node already exists, no action is taken (the data is not altered).
     * <p>
     * The root "/" Node always exists and therefore can not be created.
     * 
     * @param nodeName The fully qualified Node name, starting from the root "/".
     * @param data The data to store in the Node, {@code null} is supported.
     * @return {@code true} if the creation was successful, {@code false} if the node already existed.
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the nodeName is not valid
     * @throws IllegalStateException If the service is deactivated
     */
    boolean create(String nodeName, Object data)
                    throws IOException, IllegalArgumentException;

    /**
     * The delete operation deletes the specified Node and all nodes under it.
     * <p>
     * The root "/" Node can not be deleted.
     * 
     * @param nodeName The fully qualified Node name, starting from the root "/".
     * @return {@code true} if the Node was deleted, {@code false} if the Node did not exist.
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the nodeName is not valid
     */
    boolean delete(String nodeName)
                    throws IOException, IllegalArgumentException;

    /**
     * The exists operation indicates whether or not the specified Node exists.
     * <p>
     * The root "/" Node always exists.
     * 
     * @param nodeName The fully qualified Node name, starting from the root "/".
     * @return {@code true} if the specified Node exists, {@code false} otherwise.
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the nodeName is not valid
     */
    boolean exists(String nodeName)
                    throws IOException, IllegalArgumentException;

    /**
     * The getData operation retrieves the data stored in the specified Node.
     * <p>
     * The root "/" Node never has any data.
     * 
     * @param nodeName The fully qualified Node name, starting from the root "/".
     * @return Object stored in the specified Node, {@code null} may be returned.
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the nodeName is not valid
     * @throws NoSuchElementException If the node does not exist
     */
    Object getData(String nodeName)
                    throws IOException, IllegalArgumentException, NoSuchElementException;

    /**
     * The getDescendantData operation retrieves the data stored in the
     * specified Node and the data of all of its descendants.
     * <p>
     * The root "/" Node never has any data.
     * 
     * @param nodeName The fully qualified Node name, starting from the root "/".
     * @return A map containing the data of the specified node and all of its descendants.
     *         The keys of the map are the paths.
     *         The value of an entry in the map will be {@code null} if the node has no value.
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the nodeName is not valid
     * @throws NoSuchElementException If the node does not exist
     */
    Map<String, Object> getDescendantData(String nodeName)
                    throws IOException, IllegalArgumentException, NoSuchElementException;

    /**
     * The setData operation stores data in the specified Node.
     * <p>
     * The data replaces any data already stored in the specified Node.
     * This will not create the Node if it does note exist.
     * <p>
     * The root "/" Node can never store data.
     * 
     * @param nodeName The fully qualified Node name, starting from the root "/".
     * @param data The data to store in the Node, {@code null} is supported.
     * @return {@code true} if the Node was updated, {@code false} if the Node did not exist.
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the nodeName is not valid
     */
    boolean setData(String nodeName, Object data)
                    throws IOException, IllegalArgumentException;

    /**
     * The getChildren operation returns a collection of the names of the
     * children Nodes of the specified Node. This operation is not recursive.
     * 
     * @param nodeName The fully qualified Node name, starting from the root "/".
     * @param absolutePath True if the returned collection should contain fully qualified node names.
     * @return String array of Node names. {@code null} is returned if the Node does not exist.
     *         If the Node exists and no children exist, the array will be empty.
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the nodeName is not valid
     */
    Collection<String> getChildren(String nodeName, boolean absolutePath)
                    throws IOException, IllegalArgumentException;

    /**
     * The registerMember operation registers this member with the repository and
     * starts the repository monitoring of this member. The repository
     * expects a heart beat from the member at least once within every
     * specified heart beat interval. If the repository detects three missing
     * heart beats in a row, the member is considered terminated, is
     * unregistered and any active repository services associated with
     * that member are discarded.
     * <p>
     * <b>NOTE:</b> when the liberty.userdir is provided in the memberData for a member, it must be UTF8 encoded with no trailing slashes to match the joined member.
     * <p>
     * 
     * @param heartBeatInterval The heart beat interval, in seconds, for this member.
     * @param memberData Provides data unique to this member; {@code null} may be provided if no data is required
     * @return the ID assigned to this member; this ID must be provided in other member operations
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the heartBeatInterval is not valid
     */
    String registerMember(int heartBeatInterval, Map<String, Object> memberData)
                    throws IOException, IllegalArgumentException;

    /**
     * The deregsiterMember operation instructs the repository to unregister the
     * specified member and discard any active repository services
     * presently associated with the member.
     * <p>
     * If the member is already disconnected, this operation has no effect.
     * 
     * @param memberId The member unregistering with the repository.
     * @throws IOException If there was any problem completing the request
     */
    void deregisterMember(String memberId)
                    throws IOException;

    /**
     * The sendHeartBeat operation sends a heart beat for the specified member
     * to the repository. This operation must be invoked at at least as
     * frequently as the heart beat interval to ensure the member remains
     * considered an active member.
     * 
     * @param memberId The member identifier to which the heart beat belongs.
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the memberId is not valid
     */
    void sendHeartBeat(String memberId)
                    throws IOException, IllegalArgumentException;

    /**
     * This form of the sendHeartBeat operation allows the member to specify
     * a new heart beat interval. Otherwise, this operation behaves like {@link #sendHeartBeat(String)}.
     * 
     * @param memberId The member identifier to which the heart beat belongs.
     * @param newHeartBeatInterval The new heart beat interval, in seconds,
     *            for this member.
     * @throws IOException If there was any problem completing the request
     * @throws IllegalArgumentException If the memberId or newHeartBeatInterval is not valid
     */
    void sendHeartBeat(String memberId, int newHeartBeatInterval)
                    throws IOException, IllegalArgumentException;

    /**
     * The dump operation writes the content of the specified Node to a file or
     * the server log. The Node, its data and all its children and their data
     * will be dumped if the node exists. If the Node does not exist, there will
     * be no operation.
     * 
     * @param nodeName The fully qualified Node name, starting from the root "/".
     * @param fileName The file to dump the content to. Default to server log if {@code null} or empty.
     *            WebSphere Application Server symbols in the fileName will be resolved.
     * @param correlator An optional string to identify the dump. It will be
     *            the first line of the dump if not {@code null} or empty.
     * @throws IOException If there was any problem completing the request.
     * @throws IllegalArgumentException If the nodeName is invalid.
     */
    void dump(String nodeName, String fileName, String correlator)
                    throws IOException, IllegalArgumentException;

    /**
     * Retrieve the member root certificate from the controller. Only
     * members are permitted to invoke this operation.
     * 
     * @throws IOException If there was any problem completing the request.
     * @throws KeyStoreException If there was a problem accessing the certificate.
     * @throws AccessControlException If the requester is not a member.
     */
    Certificate retrieveMemberRootCertificate() throws IOException, KeyStoreException, AccessControlException;

    final static String DEPLOYVAR_NAME_KEY = "name";
    final static String DEPLOYVAR_INCREMENT_KEY = "increment";
    final static String DEPLOYVAR_INITIAL_KEY = "initialValue";

    /**
     * Allocates a list of named deployment variables for a host. These variables must already
     * be stored in the collective repository based on a controller configuration.
     * 
     * @param host the host that will use the deployment variables
     * @param names the deployement variable names to allocate
     * @return a Map that has the name of each allocated deployment variable as the key and the value is the allocated value for the variable
     * @throws IOException If there was any problem completing the request.
     */
    Map<String, Integer> allocateDeployVariables(String host, String names[]) throws IOException;

    /**
     * Releases values for a set of deployment variables for a host. Previously allocated values may eventually
     * cease to be used and can be released so that they can be used again on the host. These variables must already
     * be stored in the collective repository and have been allocated.
     * 
     * @param host the host that will use the deployment variables
     * @param deployVars a Map that has the name of each allocated deployment variable to release. The key is the name of the variable and the value is the allocated value to be
     *            released
     * @throws IOException If there was any problem completing the request.
     */
    void releaseDeployVariables(String host, Map<String, Integer> deployVars) throws IOException;
}
