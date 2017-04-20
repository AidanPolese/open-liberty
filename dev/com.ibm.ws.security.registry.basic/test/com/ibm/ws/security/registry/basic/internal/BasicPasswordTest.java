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
package com.ibm.ws.security.registry.basic.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import org.junit.Test;

/**
 *
 */
public class BasicPasswordTest {

    /**
     * Test method for constructor with null
     */
    @Test
    public void testConstructorNull() {
        try {
            BasicPassword bp = new BasicPassword(null, true);
            assertNotNull("Even a given parameter is null with 2nd parameter is set as true, an object needs to be constructed properly", bp);
            bp = new BasicPassword(null, false);
            assertNotNull("Even a given parameter is null with 2nd parameter is set as false, an object needs to be constructed properly", bp);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Test method for constructor with empty string
     */
    @Test
    public void testConstructorEmpty() {
        try {
            BasicPassword bp = new BasicPassword("", true);
            assertNotNull("Even a given parameter is an empty string, an object needs to be constructed properly", bp);
            bp = new BasicPassword("", false);
            assertNotNull("Even a given parameter is an empty string, an object needs to be constructed properly", bp);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Test method for constructor with valid value, no 2nd parameter.
     */
    @Test
    public void testConstructorOneParameter() {
        String str = "password";
        BasicPassword bp = new BasicPassword(str);
        assertNotNull(bp);
        assertTrue(Arrays.equals(str.toCharArray(), bp.getPassword().getChars()));
        assertNull("if a constructor which takes one pamameter is used, getHashedPassword should return null", bp.getHashedPassword());
        assertFalse("If a constructor which takes one parameter is used, isHashed is set as false", bp.isHashed());
    }

    /**
     * Test method for constructor with valid values, 2nd parameter.
     */
    @Test
    public void testConstructorTwoParametersPlain() {
        String str = "password";
        BasicPassword bp = new BasicPassword(str, false);
        assertNotNull(bp);
        assertTrue(Arrays.equals(str.toCharArray(), bp.getPassword().getChars()));
        assertNull("getHashedPassword should return null", bp.getHashedPassword());
        assertFalse("isHashed is set as false", bp.isHashed());
    }

    /**
     * Test method for constructor with valid values, 2nd parameter.
     */
    @Test
    public void testConstructorTwoParametersHashed() {
        String str = "hashedpassword";
        BasicPassword bp = new BasicPassword(str, true);
        assertNotNull(bp);
        assertEquals(str, bp.getHashedPassword());
        assertNull("getPassword should return null", bp.getPassword());
        assertTrue("isHashed is set as false", bp.isHashed());
    }

}
