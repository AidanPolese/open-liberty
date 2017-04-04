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
package com.ibm.ws.security.authorization;

import java.util.Collection;

import javax.security.auth.Subject;

/**
 * The AccessDecisionService defines the service interface for performing the
 * ultimate decision to determine if a user has access to a resource.
 * <p>
 * This will be called by the AuthorizationService.
 * 
 * @see AuthorizationService
 */
public interface AccessDecisionService {

    /**
     * Check if the Subject is allowed to access the specified resource. The
     * exact criteria used to make this determination depends on the implementation.
     * 
     * @param resourceName the name of the resource being accessed. Must not be {@code null}.
     * @param requiredRoles the roles required to be granted access to the resource.
     *            Must not be {@code null}.
     * @param assignedRoles the roles mapped to the given subject, {@code null} is tolerated.
     * @param subject the Subject which is trying to access the resource, {@code null} is tolerated.
     * @return {@code true} if the Subhject is granted access, {@code false} otherwise.
     */
    boolean isGranted(String resourceName, Collection<String> requiredRoles,
                      Collection<String> assignedRoles, Subject subject);
}