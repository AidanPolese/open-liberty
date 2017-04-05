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
 * 95504            13-03-13  Sharath   Change the Interface such that we know when the log the warning message
 * 95909            14-03-13  Sharath   Changes to the interface with respect to the new design
 * 99082            16-04-13  Sharath   Wrong destination name seen for alias destination authorization failure
 * 99083            17-04-13  Sharath   Alias Destination Permission for TopicSpace
 */

package com.ibm.ws.messaging.security.authorization;

import javax.security.auth.Subject;

/**
 * Authorization Service Interface for Messaging
 * This is responsible for authorizing a user for accessing destination
 * 
 * @author Sharath Chandra B
 * 
 */
public interface MessagingAuthorizationService {

    /**
     * Check if the AuthenticatedSubject has the access for specific operation on the Queue requested
     * 
     * @param authenticatedSubject
     * @param destination
     * @param operationType (SEND, RECEIVE, BROWSE)
     * @param logWarning
     * @return
     *         true : If the User has access to perform particular action on the destination
     *         false: If the User is not authorized to perform an action on a destination
     * @throws MessagingAuthorizationException
     */
    public boolean checkQueueAccess(Subject authenticatedSubject,
                                    String destination, String operationType, boolean logWarning) throws MessagingAuthorizationException;

    /**
     * Check if the AuthenticatedSubject has the access for specific operation on the Temporary Destination requested
     * 
     * @param authenticatedSubject
     * @param prefix
     * @param operationType (CREATE, SEND, RECEIVE)
     * @return
     *         true : If the User has access to perform particular action on the destination
     *         false: If the User is not authorized to perform an action on a destination
     * @throws MessagingAuthorizationException
     */
    public boolean checkTemporaryDestinationAccess(Subject authenticatedSubject,
                                                   String prefix, String operationType) throws MessagingAuthorizationException;

    /**
     * Check if the AuthenticatedSubject has the access for specific operation on the Topic requested
     * 
     * @param authenticatedSubject
     * @param topicSpace
     * @param topicName
     * @param operationType (SEND, RECEIVE)
     * @return
     *         true : If the User has access to perform particular action on the destination
     *         false: If the User is not authorized to perform an action on a destination
     * @throws MessagingAuthorizationException
     */
    public boolean checkTopicAccess(Subject authenticatedSubject,
                                    String topicSpace, String topicName, String operationType) throws MessagingAuthorizationException;

    /**
     * @param authenticatedSubject
     * @param targetDestination
     * @param aliasDestination
     * @param destinationType
     * @param operationType
     * @param logWarning
     * @return
     *         true : If the User has access to perform particular action on the destination
     *         false: If the User is not authorized to perform an action on a destination
     * @throws MessagingAuthorizationException
     */
    public boolean checkAliasAccess(Subject authenticatedSubject, String targetDestination, String aliasDestination, int destinationType, String operationType,
                                    boolean logWarning) throws MessagingAuthorizationException;

}
