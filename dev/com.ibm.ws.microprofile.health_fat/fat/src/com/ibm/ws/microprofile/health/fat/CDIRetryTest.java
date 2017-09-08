/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.health.fat;

import static org.junit.Assert.assertNotNull;

import org.junit.ClassRule;
import org.junit.Test;

import com.ibm.ws.fat.util.LoggingTest;
import com.ibm.ws.fat.util.SharedServer;

import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

@Mode(TestMode.LITE)
public class CDIRetryTest extends LoggingTest {

    @ClassRule
    public static SharedServer SHARED_SERVER = new SharedServer("CDIHealth");

    @Test
    public void test() throws Exception {
        if (!SHARED_SERVER.getLibertyServer().isStarted())
            SHARED_SERVER.getLibertyServer().startServer();

        assertNotNull("Kernel did not start", SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKE0002I"));
        assertNotNull("Server did not start", SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKF0011I"));

        assertNotNull("FeatureManager should report update is complete",
                      SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKF0008I"));
        SHARED_SERVER.getLibertyServer().stopServer();

    }

    /** {@inheritDoc} */
    @Override
    protected SharedServer getSharedServer() {
        return SHARED_SERVER;
    }
}
