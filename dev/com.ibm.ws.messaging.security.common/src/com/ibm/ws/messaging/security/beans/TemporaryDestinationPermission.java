/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.messaging.security.beans;

import java.util.HashSet;
import java.util.Set;

import com.ibm.ws.messaging.security.MessagingSecurityConstants;

/**
 * Permission Object for the Temporary Destinations
 */
public class TemporaryDestinationPermission extends Permission {

    String prefix = null;

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void addUserAndGroupsToRole(String[] actionArray, Set<String> users, Set<String> groups) {
        // Once we have added all the actions, it does not make sense to add other action again
        // In case someone has defined ALL and SEND permission, ALL would have added for SEND also, 
        // so we should avoid adding SEND again.
        if (checkActionArrayHasAllPermission(actionArray)) {
            addUsersAndGroupsToAllActions(users, groups);
        } else {
            for (String action : actionArray) {
                if (validateAction(action)) {
                    if (action.equals(MessagingSecurityConstants.OPERATION_TYPE_ALL)) {
                        addUsersAndGroupsToAllActions(users, groups);
                    } else {
                        action = action.trim().toUpperCase();
                        Set<String> tempUsers = new HashSet<String>();
                        Set<String> tempGroups = new HashSet<String>();
                        tempUsers.addAll(users);
                        tempGroups.addAll(groups);
                        addAllUsersToRole(tempUsers, action);
                        addAllGroupsToRole(tempGroups, action);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.messaging.security.beans.Permission#validateAction(java.lang.String)
     */
    @Override
    public boolean validateAction(String action) {
        boolean result = false;
        if (action.equals(MessagingSecurityConstants.OPERATION_TYPE_SEND) ||
            action.equals(MessagingSecurityConstants.OPERATION_TYPE_RECEIVE) ||
            action.equals(MessagingSecurityConstants.OPERATION_TYPE_CREATE)) {
            result = true;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.messaging.security.beans.Permission#addUsersAndGroupsToAllActions(java.util.Set, java.util.Set)
     */
    @Override
    public void addUsersAndGroupsToAllActions(Set<String> users, Set<String> groups) {
        Set<String> tempUsers = new HashSet<String>();
        Set<String> tempGroups = new HashSet<String>();
        tempUsers.addAll(users);
        tempGroups.addAll(groups);

        addAllUsersToRole(tempUsers, MessagingSecurityConstants.OPERATION_TYPE_SEND);
        addAllGroupsToRole(tempGroups, MessagingSecurityConstants.OPERATION_TYPE_SEND);

        addAllUsersToRole(tempUsers, MessagingSecurityConstants.OPERATION_TYPE_RECEIVE);
        addAllGroupsToRole(tempGroups, MessagingSecurityConstants.OPERATION_TYPE_RECEIVE);

        addAllUsersToRole(tempUsers, MessagingSecurityConstants.OPERATION_TYPE_CREATE);
        addAllGroupsToRole(tempGroups, MessagingSecurityConstants.OPERATION_TYPE_CREATE);
    }

}
