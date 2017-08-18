/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.websphere.microprofile.faulttolerance_fat.tests;

import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.ibm.ws.fat.util.LoggingTest;
import com.ibm.ws.fat.util.SharedServer;
import com.ibm.ws.fat.util.browser.WebBrowser;

import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

@Mode(TestMode.LITE)
public class CDIBulkheadTest extends LoggingTest {

    @ClassRule
    public static SharedServer SHARED_SERVER = new SharedServer("CDIFaultTolerance");

    @Test
    public void testBulkheadSmall() throws Exception {
        WebBrowser browser = createWebBrowserForTestCase();
        getSharedServer().verifyResponse(browser, "/CDIFaultTolerance/bulkhead?testMethod=testBulkheadSmall",
                                         "SUCCESS");
    }

    @Test
    public void testBulkheadQueueFull() throws Exception {
        WebBrowser browser = createWebBrowserForTestCase();
        getSharedServer().verifyResponse(browser, "/CDIFaultTolerance/bulkhead?testMethod=testBulkheadQueueFull",
                                         "SUCCESS");
    }

    @Test
    public void testBulkheadTimeout() throws Exception {
        WebBrowser browser = createWebBrowserForTestCase();
        getSharedServer().verifyResponse(browser, "/CDIFaultTolerance/bulkhead?testMethod=testBulkheadTimeout",
                                         "SUCCESS");
    }

    /** {@inheritDoc} */
    @Override
    protected SharedServer getSharedServer() {
        return SHARED_SERVER;
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
