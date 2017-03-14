/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.bvt;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class BVTTest {
    @Test
    public void testConfiguredLogFiles() {
        String serverRoot = System.getProperty("server.root");

        File traceLogFile = new File(serverRoot, "logs/traceLog.log");
        Assert.assertTrue(traceLogFile.exists());

        File loggingTraceFile = new File(serverRoot, "logs/loggingTrace.log");
        Assert.assertTrue(loggingTraceFile.exists());
    }
}
