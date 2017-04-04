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
package com.ibm.ws.collective.member.security;

import java.security.AccessControlException;

/**
 * Singleton access authorizer.
 * <p>
 * As a general rule, if the Subject on the thread is any other than a member,
 * authorization will be granted. If the Subject on the thread is
 * a collective member, then the authorization will be rejected if the host of
 * that member differs from the current member being accessed.
 * <p>
 */
public interface SingletonAuthorizer {

    /**
     * Determine if a caller on the thread is authorized to access the
     * member methods.
     * 
     * @param operation
     * @throws AccessControlException if the caller on the thread is not authorized
     */
    void isAuthorized(String operation) throws AccessControlException;
}
