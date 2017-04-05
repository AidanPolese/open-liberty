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
 * Collective repository access authorizer.
 * <p>
 * As a general rule, if the Subject on the thread is any other than a member,
 * authorization will be granted to any node. If the Subject on the thread is
 * a collective member, then the authorization will be rejected if the node
 * does not 'belong' to the member; that is to say that the node must be within
 * the member's repository namespace:
 * /sys.was.collectives/local/hosts/hostName/userdirs/userDir/servers/serverName
 * <p>
 * Collective members are not authorized to modify any nodes within the repository
 * which they do not own.
 */
public interface CollectiveRepositoryAuthorizer {

    /**
     * Determine if a caller on the thread is authorized to access the
     * specified node for the given operation.
     * 
     * @param operation
     * @param nodeName
     * @throws AccessControlException if the caller on the thread is not authorized to
     *             access the node
     */
    void isAuthorized(String operation, String nodeName) throws AccessControlException;

    /**
     * Determine if a caller is an authorized member.
     * 
     * @throws AccessControlException if the caller is not a member
     */
    void isAuthorizedMember() throws AccessControlException;
}
