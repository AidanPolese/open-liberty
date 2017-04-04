/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.quickstart.internal;

import java.util.HashSet;
import java.util.Set;

import com.ibm.ws.management.security.ManagementRole;
import com.ibm.ws.management.security.ManagementSecurityConstants;

/**
 * Implements the ManagementRole interface to provide the {@link ManagementSecurityConstants.ADMINISTRATOR_ROLE_NAME} role.
 * <p>
 * If any other ManagementRole services is defined, this definition must go away.
 */
class QuickStartSecurityAdministratorRole implements ManagementRole {
    private static final Set<String> EMPTY_SET = new HashSet<String>();
    private final Set<String> users;

    QuickStartSecurityAdministratorRole(String user) {
        users = new HashSet<String>();
        users.add(user);
    }

    /** {@inheritDoc} */
    @Override
    public String getRoleName() {
        return ManagementSecurityConstants.ADMINISTRATOR_ROLE_NAME;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getUsers() {
        return users;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getGroups() {
        return EMPTY_SET;
    }

}
