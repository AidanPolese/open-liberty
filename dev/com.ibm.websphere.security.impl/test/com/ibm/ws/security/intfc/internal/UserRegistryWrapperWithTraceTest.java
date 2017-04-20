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
package com.ibm.ws.security.intfc.internal;

import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 */
public class UserRegistryWrapperWithTraceTest extends UserRegistryWrapperTest {
    @BeforeClass
    public static void traceSetUp() {
        outputMgr.trace("*=all");
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
        assertFalse("FAIL: should not find any passwords in messages",
                    outputMgr.checkForMessages(PWD));
        assertFalse("FAIL: should not find any passwords in trace",
                    outputMgr.checkForTrace(PWD));
    }

    @AfterClass
    public static void traceTearDown() {
        outputMgr.trace("*=all=disabled");
    }
}
