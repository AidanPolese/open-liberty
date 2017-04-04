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
package com.ibm.ws.security;

import com.ibm.ws.security.authentication.AuthenticationService;
import com.ibm.ws.security.authorization.AuthorizationService;
import com.ibm.ws.security.registry.UserRegistryService;

/**
 * Returns the various Security services that are effective for
 * the current configuration.
 */
public interface SecurityService {

    /**
     * Retrieves the AuthenticationService that is registered for the
     * current configuration.
     * 
     * @return AuthenticationService, does not return <code>null</code>.
     */
    AuthenticationService getAuthenticationService();

    /**
     * Retrieves the AuthorizationService that is registered for the
     * current configuration.
     * 
     * @return AuthorizationService, does not return <code>null</code>.
     */
    AuthorizationService getAuthorizationService();

    /**
     * Retrieves the UserRegistryService that is registered for the
     * current configuration.
     * 
     * @return UserRegistryService, does not return <code>null</code>.
     */
    UserRegistryService getUserRegistryService();

}
