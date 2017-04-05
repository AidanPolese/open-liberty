/*
 * IBM Confidential OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * 
 * Change activity:
 * ---------------  --------  --------  ------------------------------------------
 * Reason           Date      Origin    Description
 * ---------------  --------  --------  ------------------------------------------
 * 95788            18-03-13  Sharath   Throw exception while trying to authenticate a User which will be navigated to calling methods
 */

package com.ibm.ws.messaging.security;

import javax.security.auth.Subject;

import com.ibm.ws.messaging.security.authentication.MessagingAuthenticationService;
import com.ibm.ws.messaging.security.authorization.MessagingAuthorizationService;

/**
 * Interface for Messaging Security Service
 * 
 * @author Sharath Chandra B
 * 
 */
public interface MessagingSecurityService {

    /**
     * Gets the MessagingAuthenticationService
     * If this method is called when Messaging Security is disabled, returns Null.
     * When Messaging Security is enabled, it returns the MessagingAuthentication Service
     * 
     * @return
     *         MessagingAuthenticationService
     */
    public MessagingAuthenticationService getMessagingAuthenticationService();

    /**
     * Gets the MessagingAuthorizationService
     * If this method is called when Messaging Security is disabled, returns Null
     * When Messaging Security is enabled, it returns the MessagingAuthorization Service
     * 
     * @return
     *         MessagingAuthorizationService
     */
    public MessagingAuthorizationService getMessagingAuthorizationService();

    /**
     * This method returns the unique name of the user that was being
     * authenticated. This is a best can do process and a user name may not be
     * available, in which case null should be returned. This method should not
     * return an empty string.
     * 
     * @param subject
     *            the WAS authenticated subject
     * 
     * @return The name of the user being authenticated.
     * @throws MessagingSecurityException
     */
    public String getUniqueUserName(Subject subject) throws MessagingSecurityException;

    /**
     * Check if the Subject is Authenticated
     * 
     * @param subject
     * @return
     *         true if Subject is not authenticated
     */
    public boolean isUnauthenticated(Subject subject) throws Exception;

}
