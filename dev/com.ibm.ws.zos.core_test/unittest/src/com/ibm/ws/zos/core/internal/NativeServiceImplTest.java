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
package com.ibm.ws.zos.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * JUnit tests for {@code NativeServiceImpl}.
 */
public class NativeServiceImplTest {

    @Test
    public void testNativeServiceImpl() {
        NativeServiceImpl testImpl = new NativeServiceImpl("SERVICE1", "SAFGRP1", false, false);
        assertEquals("SERVICE1", testImpl.getServiceName());
        assertEquals("SAFGRP1", testImpl.getAuthorizationGroup());
        assertFalse(testImpl.isPermitted());
        assertTrue(testImpl.toString().contains("serviceName=SERVICE1"));
        assertTrue(testImpl.toString().contains("authorizationGroup=SAFGRP1"));
        assertTrue(testImpl.toString().contains("permitted=false"));

        testImpl = new NativeServiceImpl("SERVICE2", "SAFGRP2", true, false);
        assertEquals("SERVICE2", testImpl.getServiceName());
        assertEquals("SAFGRP2", testImpl.getAuthorizationGroup());
        assertTrue(testImpl.isPermitted());
        assertTrue(testImpl.toString().contains("serviceName=SERVICE2"));
        assertTrue(testImpl.toString().contains("authorizationGroup=SAFGRP2"));
        assertTrue(testImpl.toString().contains("permitted=true"));
    }

}
