/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authorization.jacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

/**
 *
 */
public class RoleInfoTest {

    static SharedOutputManager outputMgr = SharedOutputManager.getInstance();
    @Rule
    public TestRule outputRule = outputMgr;

    /**
     * Tests constructor
     * Expected result: get the expected parameters.
     */
    @Test
    public void RoleInfoCtorRoleOnly() {
        String rn = "roleName";
        RoleInfo ri = new RoleInfo(rn);
        assertEquals(rn, ri.getRoleName());
        assertFalse(ri.isDenyAll());
        assertFalse(ri.isPermitAll());
    }

    /**
     * Tests constructor
     * Expected result: get the expected parameters.
     */
    @Test
    public void RoleInfoCtorNoParam() {
        RoleInfo ri = new RoleInfo();
        assertNull(ri.getRoleName());
        assertFalse(ri.isDenyAll());
        assertFalse(ri.isPermitAll());
    }

    /**
     * Tests setPermitAll
     * Expected result: get the expected parameters.
     */
    @Test
    public void setPermitAllTest() {
        String rn = "roleName";
        RoleInfo ri = new RoleInfo(rn);
        assertEquals(rn, ri.getRoleName());
        ri.setPermitAll();
        assertNull(ri.getRoleName());
        assertFalse(ri.isDenyAll());
        assertTrue(ri.isPermitAll());
    }

    /**
     * Tests setDenyAll
     * Expected result: get the expected parameters.
     */
    @Test
    public void setDenyAllTest() {
        String rn = "roleName";
        RoleInfo ri = new RoleInfo(rn);
        assertEquals(rn, ri.getRoleName());
        ri.setDenyAll();
        assertNull(ri.getRoleName());
        assertTrue(ri.isDenyAll());
        assertFalse(ri.isPermitAll());
    }

    /**
     * Tests toString
     * Expected result: get the expected result
     */
    @Test
    public void toStringTest() {
        String rn = "roleName";
        String output = "role : " + rn + " DenyAll : false PermitAll : false";

        RoleInfo ri = new RoleInfo(rn);
        assertEquals(output, ri.toString());
    }
}
