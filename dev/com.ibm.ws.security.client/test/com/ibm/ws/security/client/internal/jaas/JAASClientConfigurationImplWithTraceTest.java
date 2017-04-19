/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.client.internal.jaas;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Drive all of the tests with trace enabled. This has two purposes:
 * 1. Catches any potentially issues that only occur when trace is turned on.
 * 2. Helps improve code coverage by not "penalizing" for not executing trace lines.
 */
public class JAASClientConfigurationImplWithTraceTest extends JAASClientConfigurationImplTest {

    @BeforeClass
    public static void enableTrace() {
        outputMgr.trace("*=all=enabled");
    }

    @AfterClass
    public static void disableTrace() {
        outputMgr.trace("*=all=disabled");
    }
}