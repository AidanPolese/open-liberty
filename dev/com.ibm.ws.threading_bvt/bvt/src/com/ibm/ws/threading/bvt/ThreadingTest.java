/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.threading.bvt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

public class ThreadingTest {
    private static SharedOutputManager outputMgr = SharedOutputManager.getInstance();

    @Rule
    public TestRule outputRule = outputMgr;

    @Test
    public void testScheduledExecutorService() {
        runTest("testScheduledExecutorService");
    }

    private void runTest(String test) {
        try {
            String port = System.getProperty("HTTP_default", "8000");
            URL url = new URL("http://localhost:" + port + "/com.ibm.ws.threading.bvt?test=" + test);
            System.out.println("Connecting to " + url);

            InputStream in = url.openConnection().getInputStream();
            try {
                while (in.read() != -1);
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }
    }
}
