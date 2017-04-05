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
package com.ibm.ws.security.authorization;

/**
 * This class extends AuthorizationTableService for feature authz tables
 * 
 * @see AuthorizationTableService
 */
public interface FeatureAuthorizationTableService extends AuthorizationTableService {

    /**
     * Add an authorization table
     * 
     * @param resource the name of the authz table, i.e. the IBM-Feature-Authorization header value
     * @param authzTable the authorization table
     */
    void addAuthorizationTable(String resourceName, AuthorizationTableService authzTable);

    /**
     * Remove an authorization table
     * 
     * @param resource the name of the authz table, i.e. the IBM-Feature-Authorization header value
     */
    void removeAuthorizationTable(String resourceName);

    /**
     * Return the value of the IBM-Authorization-Roles header from the web module
     * of the web request currently on the thread context
     * 
     * @return IBM-Authorization-Roles value or null if there is no IBM-Authorization-Roles header
     */
    public String getFeatureAuthzRoleHeaderValue();
}
