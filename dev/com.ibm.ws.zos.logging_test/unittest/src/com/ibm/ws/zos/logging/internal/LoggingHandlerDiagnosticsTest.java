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
package com.ibm.ws.zos.logging.internal;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class LoggingHandlerDiagnosticsTest {

    LoggingHandlerDianostics loggingHandlerDiagnostics;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        loggingHandlerDiagnostics = new LoggingHandlerDianostics("TEST", 1);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        loggingHandlerDiagnostics = null;
    }

    /**
     * Test LoggingHandlerDiagnostics constructor.
     */
    @Test
    public void test_LoggingHandlerDiagnostics() {

        assertEquals(loggingHandlerDiagnostics.msg, "TEST");
        assertEquals(loggingHandlerDiagnostics.rc, 1);

    }

}
