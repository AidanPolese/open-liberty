/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.delegation;

import javax.security.auth.Subject;

import com.ibm.ws.security.authentication.AuthenticationException;

/**
 * Interface for creating the run-as subject during delegation.
 */
public interface DelegationProvider {

    /**
     * Create a subject for the user mapped to the given role of a given application.
     *
     * @param roleName the name of the role, used to look up the corresponding user.
     * @param appName the name of the application, used to look up the corresponding user.
     * @return subject a subject representing the user that is mapped to the given run-as role.
     * @throws AuthenticationException if the identity cannot be authenticated
     */
    Subject getRunAsSubject(String roleName, String appName) throws AuthenticationException;

    /**
     * @return
     */
    String getDelegationUser();

}