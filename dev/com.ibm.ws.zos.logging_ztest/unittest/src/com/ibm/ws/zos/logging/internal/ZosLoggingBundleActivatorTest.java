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
package com.ibm.ws.zos.logging.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.ws.zos.jni.NativeMethodUtils;

import test.common.SharedOutputManager;
import test.common.zos.NativeLibraryUtils;
import test.common.zos.ZosOperations;

/**
 *
 */
public class ZosLoggingBundleActivatorTest {

    private static SharedOutputManager outputMgr;

    /**
     * Capture stdout/stderr output to the manager.
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // There are variations of this constructor:
        // e.g. to specify a log location or an enabled trace spec. Ctrl-Space
        // for suggestions
        outputMgr = SharedOutputManager.getInstance();
        outputMgr.captureStreams();

        new ZosOperations().restartAngel();

        NativeLibraryUtils.loadUnauthorized();
        NativeLibraryUtils.registerServer();
        NativeLibraryUtils.registerNatives(ZosLoggingBundleActivator.class);
    }

    /**
     * Final teardown work when class is exiting.
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        NativeLibraryUtils.reset();

        NativeLibraryUtils.deregisterNatives(ZosLoggingBundleActivator.class);
        NativeLibraryUtils.deregisterServer();

        new ZosOperations().cancelAngel();

        // Make stdout and stderr "normal"
        outputMgr.restoreStreams();
    }

    /**
     * Individual teardown after each test.
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        // Clear the output generated after each method invocation
        outputMgr.resetStreams();
    }

    /**
     * Test write to operator console
     *
     *
     */
    @Test
    public void test_ntv_WriteToOperatorConsole() {
        final String m = "test_ntv_WriteToOperatorConsole";
        try {
            ZosLoggingBundleActivator zosLoggingBundleActivator = new ZosLoggingBundleActivator();

            int rc = zosLoggingBundleActivator.ntv_WriteToOperatorConsole(NativeMethodUtils.convertToEBCDIC("CWWKF0000I: This is a operator console test", false));
            assertTrue(rc == 0);

            byte[] msg = null;
            rc = zosLoggingBundleActivator.ntv_WriteToOperatorConsole(msg);
            assertTrue(rc == -102);

            rc = zosLoggingBundleActivator.ntv_WriteToOperatorConsole(NativeMethodUtils.convertToEBCDIC("", false));
            assertTrue(rc == 4);
        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }

    /**
     * Test write to operator console multiple line message
     *
     *
     */
    @Test
    public void test_ntv_WriteToOperatorConsole_ml() {
        final String m = "test_ntv_WriteToOperatorConsole_ml";
        try {
            ZosLoggingBundleActivator zosLoggingBundleActivator = new ZosLoggingBundleActivator();
            //                         1        2         3         4         5         6         7
            String longMsg = "TSTCP0004I: This message should be split just about almost here, " +
                             "thenthenextlinewillcomeouttobesplithere " +
                             "becauseoftheblankIputinthetext,you know it would be nice to split " +
                             "near some data like this that has a lot of blanks in the line     " +
                             "                  like this, the end                              ";
            //                          1         2         3         4         5         6         7
            String longMsg2 = "TSTCP0005I: extemelylongmessagetoexceedthetenlinesofMultline1234567890" +
                              "1234567890123456789012345678901234567890123456789012345678901234567890" +
                              "1234567890123456789012345678901234567890123456789012345678901234567890" +
                              "1234567890123456789012345678901234567890123456789012345678901234567890" +
                              "1234567890123456789012345678901234567890123456789012345678901234567890" +
                              "1234567890123456789012345678901234567890123456789012345678901234567890" +
                              "1234567890123456789012345678901234567890123456789012345678901234567890" +
                              "1234567890123456789012345678901234567890123456789012345678901234567890" +
                              "1234567890123456789012345678901234567890123456789012345678901234567890" +
                              "123456789012345678901234567890123456789012345678901234567ENDOFTENLINES" +
                              "Eleventhline";
            int rc = zosLoggingBundleActivator.ntv_WriteToOperatorConsole((longMsg + '\0').getBytes("Cp1047"));
            assertTrue(rc == 0);

            rc = zosLoggingBundleActivator.ntv_WriteToOperatorConsole((longMsg2 + '\0').getBytes("Cp1047"));
            assertTrue(rc == 0);

            // Generate a message that will exceed the 1,000 continuation limit.
            int i = 0;
            String tooLong = "";
            for (; i < 1001; i++) {
                tooLong = tooLong + longMsg2;
            }
            rc = zosLoggingBundleActivator.ntv_WriteToOperatorConsole((tooLong + '\0').getBytes("Cp1047"));
            // Assert that we truncated the response
            assertTrue(rc == -12);

        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }

    /**
     * This test expects the the JVM not to have been launched as a started task.
     */
    @Test
    public void test_ntv_isLaunchContextShell_true() {
        ZosLoggingBundleActivator zosLoggingBundleActivator = new ZosLoggingBundleActivator();
        assertTrue(zosLoggingBundleActivator.ntv_isLaunchContextShell());
    }

    /**
     * The unit-testing environment does not have the MSGLOG dd defined.
     */
    @Test
    public void test_ntv_isMsgLogDDDefined() {
        ZosLoggingBundleActivator zosLoggingBundleActivator = new ZosLoggingBundleActivator();
        assertFalse(zosLoggingBundleActivator.ntv_isMsgLogDDDefined());
    }
}
