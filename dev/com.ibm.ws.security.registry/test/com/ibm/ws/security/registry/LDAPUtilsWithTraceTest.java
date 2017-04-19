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
package com.ibm.ws.security.registry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

/**
 * Drive all of the tests with trace enabled. This has two purposes:
 * 1. Catches any potentially issues that only occur when trace is turned on.
 * 2. Helps improve code coverage by not "penalizing" for not executing trace lines.
 */
public class LDAPUtilsWithTraceTest extends LDAPUtilsTest {
    static final SharedOutputManager outputMgr = SharedOutputManager.getInstance();
    /**
     * Using the test rule will drive capture/restore and will dump on error..
     * Notice this is not a static variable, though it is being assigned a value we
     * allocated statically. -- the normal-variable-ness is for before/after processing
     */
    @Rule
    public TestRule managerRule = outputMgr;

    @BeforeClass
    public static void enableTrace() {
        outputMgr.trace("*=all=enabled");
    }

    @AfterClass
    public static void disableTrace() {
        outputMgr.trace("*=all=disabled");
    }
}
