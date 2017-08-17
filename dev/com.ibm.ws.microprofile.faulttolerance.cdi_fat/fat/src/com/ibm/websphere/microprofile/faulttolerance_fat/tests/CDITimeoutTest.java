package com.ibm.websphere.microprofile.faulttolerance_fat.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.ibm.ws.fat.util.LoggingTest;
import com.ibm.ws.fat.util.SharedServer;
import com.ibm.ws.fat.util.browser.WebBrowser;

import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

@Mode(TestMode.LITE)
public class CDITimeoutTest extends LoggingTest {

    @ClassRule
    public static SharedServer SHARED_SERVER = new SharedServer("CDIFaultTolerance");

    @Test
    public void testTimeout() throws Exception {
        WebBrowser browser = createWebBrowserForTestCase();
        getSharedServer().verifyResponse(browser, "/CDIFaultTolerance/timeout?testMethod=testTimeout",
                                         "SUCCESS");
    }

    @Test
    public void testException() throws Exception {
        WebBrowser browser = createWebBrowserForTestCase();
        getSharedServer().verifyResponse(browser, "/CDIFaultTolerance/timeout?testMethod=testException",
                                         "SUCCESS");
    }

    /** {@inheritDoc} */
    @Override
    protected SharedServer getSharedServer() {
        return SHARED_SERVER;
    }
	
		@BeforeClass
	public static void setUp() throws Exception {
		if (!SHARED_SERVER.getLibertyServer().isStarted()) {
			SHARED_SERVER.getLibertyServer().startServer();
		}
		
	}

    @AfterClass
    public static void tearDown() throws Exception {
        if (SHARED_SERVER != null && SHARED_SERVER.getLibertyServer().isStarted()) {
            /*
             * Ignore following exception as those are expected:
             * CWWKC1101E: The task com.ibm.ws.microprofile.faulttolerance.cdi.FutureTimeoutMonitor@3f76c259, which was submitted to executor service
             * managedScheduledExecutorService[DefaultManagedScheduledExecutorService], failed with the following error:
             * org.eclipse.microprofile.faulttolerance.exceptions.FTTimeoutException: java.util.concurrent.TimeoutException
             */
            SHARED_SERVER.getLibertyServer().stopServer("CWWKC1101E");
        }
    }
}