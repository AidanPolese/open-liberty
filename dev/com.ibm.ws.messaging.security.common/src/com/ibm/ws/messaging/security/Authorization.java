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
 * 95504            13-03-13  Sharath   Move the exceptions to the called methods where we have more information
 * 95909            18-03-13  Sharath   Changes related to new Messaging Security Story
 * 99082            16-04-13  Sharath   Wrong destination name seen for alias destination authorization failure
 * 99083            17-04-13  Sharath   Alias Destination Permission for TopicSpace
 */

package com.ibm.ws.messaging.security;

import javax.security.auth.Subject;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.messaging.security.authorization.MessagingAuthorizationException;
import com.ibm.ws.messaging.security.authorization.MessagingAuthorizationService;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * A Proxy class for authorizing the users
 * <ul>
 * <li>When Messaging Security is disabled, it authorizes all the users</li>
 * <li>When Messaging Security is enabled, it calls the Messaging Authorization
 * Service to authorize</li>
 * </ul>
 * 
 * @author Sharath Chandra B
 * 
 */
public class Authorization {

    // Trace component for the Authorization class
    private static TraceComponent tc = SibTr.register(Authorization.class,
                                                      MSTraceConstants.MESSAGING_SECURITY_TRACE_GROUP,
                                                      MSTraceConstants.MESSAGING_SECURITY_RESOURCE_BUNDLE);

    // Absolute class name along with the package used for tracing
    private static final String CLASS_NAME = "com.ibm.ws.messaging.security.Authorization";

    /*
     * Authorization Service for Messaging, it will exists when messaging
     * security is enabled
     */
    private MessagingAuthorizationService messagingAuthorizationService = null;

    /*
     * RuntimeSecurityService is a singleton instance and used to query if
     * Messaging Security is enabled or not
     */
    private final RuntimeSecurityService runtimeSecurityService = RuntimeSecurityService.SINGLETON_INSTANCE;

    /**
     * Checks if the User passed has access to the queue to perform an action
     * <ul>
     * <li> When Messaging Security is disabled, it always returns true </li>
     * <li> When Messaging Security is enabled, it calls
     * MessagingAuthorizationService to check for access </li>
     * </ul>
     * 
     * @param authenticatedSubject
     *            Subject got after authenticating the user
     * @param queue
     *            Destination which user is requesting to access
     * @param operationType
     *            Type of operation (SEND, RECEIVE, BROWSE)
     * @return true : If the User is authorized
     *         false: If the User is not authorized
     */
    public boolean checkQueueAccess(Subject authenticatedSubject,
                                    String queue, String operationType) throws MessagingAuthorizationException {
        SibTr.entry(tc, CLASS_NAME + "checkDestinationAccess", new Object[] { authenticatedSubject, queue,
                                                                             operationType });
        boolean result = false;
        if (!runtimeSecurityService.isMessagingSecure()) {
            result = true;
        } else {
            if (messagingAuthorizationService != null) {
                result = messagingAuthorizationService.checkQueueAccess(
                                                                        authenticatedSubject, queue, operationType, true);
            }
        }
        SibTr.exit(tc, CLASS_NAME + "checkDestinationAccess", result);
        return result;
    }

    /**
     * Checks if the User passed has access to the alias to perform an action
     * <ul>
     * <li> When Messaging Security is disabled, it always returns true </li>
     * <li> When Messaging Security is enabled, it calls
     * MessagingAuthorizationService to check for access </li>
     * </ul>
     * 
     * @param authenticatedSubject
     *            Subject got after authenticating the user
     * @param destination
     *            Target Destination for the alias destination
     * @param destinationType
     * @param aliasDesination
     *            Alias Destination which user is requesting to access
     * @param operationType
     *            Type of operation (SEND, RECEIVE, BROWSE)
     * @return true : If the User is authorized
     *         false: If the User is not authorized
     */
    public boolean checkAliasAccess(Subject authenticatedSubject,
                                    String destination, String aliasDestination, int destinationType, String operationType) throws MessagingAuthorizationException {
        SibTr.entry(tc, CLASS_NAME + "checkAliasAccess", new Object[] { authenticatedSubject, destination,
                                                                       operationType, aliasDestination });
        boolean result = false;
        if (!runtimeSecurityService.isMessagingSecure()) {
            result = true;
        } else {
            if (messagingAuthorizationService != null) {
                result = messagingAuthorizationService.checkAliasAccess(authenticatedSubject, destination, aliasDestination, destinationType, operationType, true);
            }
        }
        SibTr.exit(tc, CLASS_NAME + "checkAliasAccess", result);
        return result;
    }

    /**
     * Checks if the User passed has access to the Temporary Destination to perform an action
     * <ul>
     * <li> When Messaging Security is disabled, it always returns true </li>
     * <li> When Messaging Security is enabled, it calls
     * MessagingAuthorizationService to check for access </li>
     * </ul>
     * 
     * @param authenticatedSubject
     *            Subject got after authenticating the user
     * @param prefix
     *            Temporary Destination Prefix
     * @param operationType
     *            Type of operation (SEND, RECEIVE, CREATE)
     * @return true : If the User is authorized
     *         false: If the User is not authorized
     */
    public boolean checkTemporaryDestinationAccess(Subject authenticatedSubject,
                                                   String prefix, String operationType) throws MessagingAuthorizationException {
        SibTr.entry(tc, CLASS_NAME + "checkTemporaryDestinationAccess", new Object[] { authenticatedSubject, prefix,
                                                                                      operationType });
        boolean result = false;
        if (!runtimeSecurityService.isMessagingSecure()) {
            result = true;
        } else {
            if (messagingAuthorizationService != null) {
                result = messagingAuthorizationService.checkTemporaryDestinationAccess(authenticatedSubject, prefix, operationType);
            }
        }

        SibTr.exit(tc, CLASS_NAME + "checkTemporaryDestinationAccess", result);
        return result;
    }

    /**
     * Checks if the User passed has access to the Topic to perform an action
     * <ul>
     * <li> When Messaging Security is disabled, it always returns true </li>
     * <li> When Messaging Security is enabled, it calls
     * MessagingAuthorizationService to check for access </li>
     * </ul>
     * 
     * @param authenticatedSubject
     *            Subject got after authenticating the user
     * @param topicSpace
     *            TopicSpace in which the Topic is defined
     * @param topicName
     *            TopicName for which the permission should be checked
     * @param operationType
     *            Type of operation (SEND, RECEIVE, BROWSE, CREATE)
     * @return true : If the User is authorized
     *         false: If the User is not authorized
     */
    public boolean checkTopicAccess(Subject authenticatedSubject,
                                    String topicSpace, String topicName, String operationType) throws MessagingAuthorizationException {

        SibTr.entry(tc, CLASS_NAME + "checkTopicAccess", new Object[] { authenticatedSubject, topicSpace, topicName, operationType });

        boolean result = false;
        if (!runtimeSecurityService.isMessagingSecure()) {
            result = true;
        } else {
            if (messagingAuthorizationService != null) {
                result = messagingAuthorizationService.checkTopicAccess(authenticatedSubject, topicSpace, topicName, operationType);
            }
        }

        SibTr.exit(tc, CLASS_NAME + "checkDestinationAccess", result);
        return result;
    }

    /**
     * Set the Messaging Authorization Service
     * 
     * @param messagingAuthorizationService
     *            MessagingAuthorization is set to Null when MessagingSecurity is disabled
     */
    public void setMessagingAuthorizationService(
                                                 MessagingAuthorizationService messagingAuthorizationService) {
        SibTr.entry(tc, CLASS_NAME + "setMessagingAuthorizationService", messagingAuthorizationService);

        this.messagingAuthorizationService = messagingAuthorizationService;

        SibTr.exit(tc, CLASS_NAME + "setMessagingAuthorizationService");
    }

}