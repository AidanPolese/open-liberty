/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.management.security;

import java.util.Set;

/**
 * A management role is a collection of users and groups which are
 * mapped to a specific role name.
 */
public interface ManagementRole {
    /**
     * {@link #MANAGEMENT_ROLE_NAME} expected value type of String, unique for each
     * type of ManagementRole implementation.
     */
    final static String MANAGEMENT_ROLE_NAME = "com.ibm.ws.management.security.role.name";

    /**
     * Answers the name of the role.
     * 
     * @return The role name. Must not be {@code null}.
     */
    String getRoleName();

    /**
     * Answers the set of users assigned this role.
     * If no users are assigned, an empty set shall be returned.
     * 
     * @return A Set of group names. Must not be {@code null}.
     */
    Set<String> getUsers();

    /**
     * Answers the set of groups assigned this role.
     * If no groups are assigned, an empty set shall be returned.
     * 
     * @return A Set of group names. Must not be {@code null}.
     */
    Set<String> getGroups();
}
