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
package com.ibm.ws.security.authorization;

import java.util.Set;

/**
 * A security role is a collection of users and groups which are
 * mapped to a specific role name.
 */
public interface SecurityRole {

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

    /**
     * Answers the set of special subjects assigned this role.
     * If no special subjects are assigned, an empty set shall be returned.
     * 
     * @return A Set of special subject names. Must not be {@code null}.
     */
    Set<String> getSpecialSubjects();

    /**
     * Answers the set of access ids assigned this role.
     * If no access ids are assigned, an empty set shall be returned.
     * 
     * @return A Set of access ids. Must not be {@code null}.
     */
    Set<String> getAccessIds();
}
