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
package com.ibm.ws.webcontainer.security.metadata;

import java.util.List;

/**
 * Represents a collection of security constraint objects.
 * An instance of this interface is the main object to determine what constraints match
 * the given resource access, where the constraints are represented by the MatchResponse
 * object and it contains the roles, if SSL is required, and if access is precluded for such access.
 */
public interface SecurityConstraintCollection {

    /**
     * Gets the match response object for the resource access.
     * 
     * @param resourceName The resource name.
     * @param method The HTTP method.
     * @return The MatchResponse object.
     */
    public abstract MatchResponse getMatchResponse(String resourceName, String method);

    /**
     * Gets the list of security constraints in this collection
     * 
     * @return a list of SecurityConstraint objects
     */
    public List<SecurityConstraint> getSecurityConstraints();

    /**
     * Adds the given security constraints to the collection
     * 
     * @param securityConstraints the security constraints to add
     */
    public void addSecurityConstraints(List<SecurityConstraint> securityConstraints);

}