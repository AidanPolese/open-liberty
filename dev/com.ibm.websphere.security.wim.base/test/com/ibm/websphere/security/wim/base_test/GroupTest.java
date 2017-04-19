/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.security.wim.base_test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ibm.wsspi.security.wim.model.Group;

/**
 *
 */
public class GroupTest {

    @Test
    public void testCaseSentitive() {
        Group group = new Group();
        assertEquals("String", group.getDataType("cn"));
    }

    @Test
    public void testCaseInSentitive() {
        Group group = new Group();
        assertEquals(null, group.getDataType("CN"));
    }
}
