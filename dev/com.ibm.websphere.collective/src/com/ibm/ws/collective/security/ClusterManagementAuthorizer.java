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
package com.ibm.ws.collective.security;

import java.security.AccessControlException;

/**
 * Cluster manager authorizer.
 * <p>
 * As a general rule, if the Subject on the thread is any other than a member,
 * authorization will be granted. If the Subject on the thread is a collective
 * member, then the authorization will be rejected.
 */
public interface ClusterManagementAuthorizer {

    /**
     * Determine if a caller on the thread is authorized to perform the
     * given cluster management operation.
     * 
     * @param operation
     * @throws AccessControlException if the caller on the thread is not authorized to
     *             perform the given operation
     */
    void isAuthorized(String operation) throws AccessControlException;

}
