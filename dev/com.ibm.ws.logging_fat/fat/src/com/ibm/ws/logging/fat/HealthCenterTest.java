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

import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;
import componenttest.topology.utils.HttpUtils;

public class HealthCenterTest {
    private static final boolean J9 = System.getProperty("java.vm.name", "unknown").contains("J9");
    private static LibertyServer server;

    @BeforeClass
    public static void beforeClass() {
        Assume.assumeTrue(J9);
        server = LibertyServerFactory.getStartedLibertyServer("com.ibm.ws.logging.healthcenter");
    }

    @Test
    public void testHealthCenterInfo() throws Exception {
        Assert.assertFalse("Expected healthcenter INFO message",
                           server.findStringsInLogs("^INFO:.*com\\.ibm\\.java\\.diagnostics\\.healthcenter\\.agent\\.iiop\\.port",
                                                    server.getConsoleLogFile()).isEmpty());
    }

    @Test
    public void testConsoleLogLevelOff() throws Exception {
        HttpUtils.findStringInReadyUrl(server, "/logger-servlet", "Hello world!");
        List<String> messages = server.findStringsInLogs("Hello world!", server.getConsoleLogFile());
        Assert.assertTrue("Did not expect to find servlet Logger message: " + messages, messages.isEmpty());
    }
}
