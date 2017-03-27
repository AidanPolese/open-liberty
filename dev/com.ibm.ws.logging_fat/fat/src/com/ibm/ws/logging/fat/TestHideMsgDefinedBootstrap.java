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
package com.ibm.ws.logging.fat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 *
 */
public class TestHideMsgDefinedBootstrap {

    private static LibertyServer msgServer = LibertyServerFactory.getLibertyServer("com.ibm.ws.logging.hidemsg.bootstrap");
    private static final Class<?> logClass = TestHideMsgDefinedBootstrap.class;

    @Rule
    public TestName name = new TestName();

    @BeforeClass
    public static void prepareTest() throws Exception {
        msgServer.startServer();
    }

    @Test
    public void testHiddenMsgIds() throws Exception {
        assertTrue("Hidden Message CWWKZ0058I should not be seen in messages.log",
                   msgServer.findStringsInLogs("CWWKZ0058I:", msgServer.getMatchingLogFile("messages.log")).isEmpty());
        assertTrue("Hidden Message CWWKZ0058I should not be seen in console.log", msgServer.findStringsInLogs("CWWKZ0058I:", msgServer.getMatchingLogFile("console.log")).isEmpty());
        assertFalse("Hidden Message CWWKZ0058I should be seen in trace", msgServer.findStringsInTrace("CWWKZ0058I:").isEmpty());
    }

    @Test
    public void testSuppressedIdsInMsgHeader() throws Exception {
        assertFalse("Suppressed Message Ids logged in header ",
                    msgServer.findStringsInLogs("Suppressed message ids:", msgServer.getMatchingLogFile("messages.log")).isEmpty());

    }

    @AfterClass
    public static void completeTest() throws Exception {
        msgServer.stopServer();
    }

}
