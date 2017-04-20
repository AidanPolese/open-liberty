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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.ibm.ws.management.security.ManagementRole;
import com.ibm.ws.management.security.ManagementSecurityConstants;

/**
 *
 */
public class QuickStartSecurityAdministratorRoleTest {
    private static final String USER = "bob";
    private ManagementRole quickStartAdminRole;

    @Before
    public void setUp() {
        quickStartAdminRole = new QuickStartSecurityAdministratorRole(USER);
    }

    /**
     * Test method for {@link com.ibm.ws.security.quickstart.internal.QuickStartSecurityAdministratorRole#getRoleName()}.
     */
    @Test
    public void getRoleName() {
        assertEquals("Must be the Administrator role name",
                     ManagementSecurityConstants.ADMINISTRATOR_ROLE_NAME,
                     quickStartAdminRole.getRoleName());
    }

    /**
     * Test method for {@link com.ibm.ws.security.quickstart.internal.QuickStartSecurityAdministratorRole#getUsers()}.
     */
    @Test
    public void testGetUsers() {
        Set<String> users = quickStartAdminRole.getUsers();
        assertEquals("Only one user should ever be mapped",
                     1, users.size());
        assertTrue("", users.contains(USER));
    }

    /**
     * Test method for {@link com.ibm.ws.security.quickstart.internal.QuickStartSecurityAdministratorRole#getGroups()}.
     */
    @Test
    public void getGroups() {
        assertTrue("No groups should ever be mapped",
                   quickStartAdminRole.getGroups().isEmpty());
    }

}
