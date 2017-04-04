/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.stackManager;

import java.io.IOException;
import java.util.Map;

/**
 * The StackManager is the main service to access {@link StackGroup} and {@link Stack} objects
 * that correspond to the user-defined stacks under the WLP_STACK_GROUPS_DIR. It reads the file
 * system for all the packages and installables as well as any configuration enhancements
 * in the server.xml.
 *
 * @author ricogala
 *
 */
public interface StackManager {

    // topic root for wild card subscriptions - e.g. PUBLISH_TOPIC_ROOT*
    static final String STACK_MANAGER_TOPIC_ROOT = "com/ibm/wsspi/stackManager/";

    // event topics
    static final String STACK_CREATED = STACK_MANAGER_TOPIC_ROOT + "StackCreated";
    static final String STACK_MODIFIED = STACK_MANAGER_TOPIC_ROOT + "StackModified";
    static final String STACK_DELETED = STACK_MANAGER_TOPIC_ROOT + "StackDeleted";
    static final String STACK_CLUSTER_NAME_CHANGE = STACK_MANAGER_TOPIC_ROOT + "StackClusterModified";

    /**
     * Scans all the StackGroups for a {@link StackGroup} that matches the input name.
     * Returns the object if found, {@code null} if not.
     *
     * @param stackGroupName
     * @return StackGroup
     */
    public abstract StackGroup getStackGroup(String stackGroupName);

    /**
     * Gets the {@link Map} of all the StackGroups for this server.
     *
     * @return Map<String, StackGroup>
     */
    public abstract Map<String, StackGroup> getStackGroups();

    /**
     * Scans all the StackGroups for a {@link Stack} that matches the input name.
     * Returns the object if found, {@code null} if not.
     *
     * @param stackName
     * @return Stack
     */
    public abstract Stack getStack(String stackName);

    /**
     * Gets the {@link Map} for all the {@link Stack} under the specified StackGroup
     *
     * @param stackGroupName
     * @return Map<String, Stack>
     */
    public abstract Map<String, Stack> getStacks(String stackGroupName);

    /**
     * @param stackName
     * @param hostName
     * @param callback
     */
    public abstract void deployStack(String stackName, String hostName, DeployStackCallBack callback) throws NoSuchStackNameException, IOException;

    /**
     * @param clustrName
     * @param stackName
     * @param pack
     */
    public abstract void addClusterMapping(String clusterName, String stackName, Package pack);

    /**
     * @param clustrName
     * @return boolean
     */
    public abstract boolean containsCluster(String clusterName);

    /**
     * Scans all map entries to find the stack associated with the cluster name given in input.
     * Returns a string representing a stackName
     *
     * @param clusterName
     * @return StackName
     */
    public abstract String getStackNameByCluster(String stackName);

}
