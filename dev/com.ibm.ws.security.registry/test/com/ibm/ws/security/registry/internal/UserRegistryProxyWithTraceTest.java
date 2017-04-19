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
package com.ibm.ws.security.registry.internal;

import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Drive all of the tests with trace enabled. This has two purposes:
 * 1. Catches any potentially issues that only occur when trace is turned on.
 * 2. Helps improve code coverage by not "penalizing" for not executing trace lines.
 */
public class UserRegistryProxyWithTraceTest extends UserRegistryProxyTest {

    @BeforeClass
    public static void enableTrace() {
        outputMgr.trace("*=all=enabled");
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();

        assertFalse("FAIL: should not find any passwords in messages",
                    outputMgr.checkForMessages(password));
        assertFalse("FAIL: should not find any passwords in trace",
                    outputMgr.checkForTrace(password));
    }

    @AfterClass
    public static void disableTrace() {
        outputMgr.trace("*=all=disabled");
    }
}
