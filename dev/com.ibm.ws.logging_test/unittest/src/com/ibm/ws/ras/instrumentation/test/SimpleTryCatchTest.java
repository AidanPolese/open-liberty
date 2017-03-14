package com.ibm.ws.ras.instrumentation.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.TestConstants;
import test.common.SharedOutputManager;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;

public class SimpleTryCatchTest {

    // Set ffdc exception dir in test data
    static SharedOutputManager outputMgr = SharedOutputManager.getInstance().logTo(TestConstants.TEST_DATA);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // make stdout/stderr "quiet"-- no output will show up for test
        // unless one of the copy methods or documentThrowable is called
        outputMgr.captureStreams();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // Make stdout and stderr "normal"
        outputMgr.restoreStreams();
    }

    public void waitSomeTime(long millis) {
        try {
            wait(millis);
        } catch (InterruptedException ex) {
        }
    }

    @Test
    public void simpleTryCatch() {
        try {
            throw new IllegalArgumentException();
        } catch (IllegalArgumentException iae) {
        }
    }

    @Test
    @FFDCIgnore(IllegalArgumentException.class)
    public void ignoredSimpleTryCatch() {
        try {
            throw new IllegalArgumentException();
        } catch (IllegalArgumentException iae) {
        }
    }
}
