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
package com.ibm.ws.threading.fat;

import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.websphere.simplicity.log.Log;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

@Mode(TestMode.FULL)
public class MemLeakTest {
    private static LibertyServer server = LibertyServerFactory.getLibertyServer("com.ibm.ws.threading.memleak_fat_server");
    private static final Class<?> c = MemLeakTest.class;

    @BeforeClass
    public static void beforeClass() throws Exception {
        final String method = "beforeClass";
        Log.entering(c, method);

        boolean serverWasStarted = false;

        if (server != null && !server.isStarted()) {
            server.startServer();
            serverWasStarted = true;
        }

        Log.exiting(c, method, serverWasStarted);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        final String method = "afterClass";
        Log.entering(c, method);

        boolean serverWasStopped = false;

        if (server != null && server.isStarted()) {
            server.stopServer();
            serverWasStopped = true;
        }

        Log.exiting(c, method, serverWasStopped);
    }

    /**
     * Starting the server with this configuration memLeakTest=true will enabled the
     * MemLeakChecker class to activate and run tests to see if we are leaking memory.
     * 
     * This test checks to see if we leak memory by scheduling and then canceling
     * a large number of tasks.
     */
    @Test
    public void testScheduleCancel() throws Exception {
        final String method = "testScheduleCancel";
        Log.entering(c, method);

        assertNotNull("Expected message indicating the test passed on the server was not found.", server.waitForStringInLog("runScheduleCancelTest PASSED"));

        Log.exiting(c, method);
    }

    /**
     * Starting the server with this configuration memLeakTest=true will enabled the
     * MemLeakChecker class to activate and run tests to see if we are leaking memory.
     * 
     * This test checks to see if we leak memory by scheduling and then running
     * a large number of tasks.
     */
    @Test
    public void testScheduleExecute() throws Exception {
        final String method = "testScheduleExecute";
        Log.entering(c, method);

        assertNotNull("Expected message indicating the test passed on the server was not found.", server.waitForStringInLog("runScheduleExecuteTest PASSED"));

        Log.exiting(c, method);
    }
}
