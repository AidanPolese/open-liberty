package com.ibm.websphere.microprofile.health_fat;

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
        SHARED_SERVER.getLibertyServer().startServer();
        assertNotNull("Kernel did not start", SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKE0002I"));
        assertNotNull("Server did not start", SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKF0011I"));

        assertNotNull("FeatureManager should report update is complete",
                      SHARED_SERVER.getLibertyServer().waitForStringInLog("CWWKF0008I"));
        SHARED_SERVER.getLibertyServer().stopServer("CWWKE0702E"); // could not resolve module

    }

    /** {@inheritDoc} */
    @Override
    protected SharedServer getSharedServer() {
        return SHARED_SERVER;
    }
}