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
public class LoggingHardcopyLogHandlerTest {
    private static SharedOutputManager outputMgr;
    private static Class<?> testClass = LoggingHardcopyLogHandler.class;
    private static boolean registeredWithAngel = false;

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
        if (!registeredWithAngel) {
            registeredWithAngel = (NativeLibraryUtils.registerServer() == 0);
        }
        if (testClass != null) {
            NativeLibraryUtils.registerNatives(testClass);
        }

    }

    /**
     * Final teardown work when class is exiting.
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        NativeLibraryUtils.reset();

        if (testClass != null) {
            NativeLibraryUtils.deregisterNatives(testClass);
        }

        if (registeredWithAngel) {
            registeredWithAngel = (NativeLibraryUtils.deregisterServer() != 0);
        }

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
     * Test write to programmer and hardcopy
     *
     *
     */
    @Test
    public void test_ntv_WriteToOperatorProgrammerAndHardcopy() {
        final String m = "test_ntv_WriteToOperatorProgrammerAndHardcopy";
        try {
            LoggingHardcopyLogHandler loggingHardcopyLogHandler = new LoggingHardcopyLogHandler();

            int rc = loggingHardcopyLogHandler.ntv_WriteToOperatorProgrammerAndHardcopy(NativeMethodUtils.convertToEBCDIC("CWWKF0001I: This is a programmer and hardcopy test",
                                                                                                                          false));
            assertTrue(rc == 0);

            byte[] msg = null;
            rc = loggingHardcopyLogHandler.ntv_WriteToOperatorProgrammerAndHardcopy(msg);
            assertTrue(rc == -102);

            rc = loggingHardcopyLogHandler.ntv_WriteToOperatorProgrammerAndHardcopy(NativeMethodUtils.convertToEBCDIC("", false));
            assertTrue(rc == 4);
        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }

    /**
     * Test write to programmer and hardcopy multiple line message
     *
     *
     */
    @Test
    public void test_ntv_WriteToOperatorProgrammerAndHardcopy_ml() {
        final String m = "test_ntv_WriteToOperatorProgrammerAndHardcopy_ml";
        try {
            LoggingHardcopyLogHandler loggingHardcopyLogHandler = new LoggingHardcopyLogHandler();
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
            int rc = loggingHardcopyLogHandler.ntv_WriteToOperatorProgrammerAndHardcopy((longMsg + '\0').getBytes("Cp1047"));
            assertTrue(rc == 0);

            rc = loggingHardcopyLogHandler.ntv_WriteToOperatorProgrammerAndHardcopy((longMsg2 + '\0').getBytes("Cp1047"));
            assertTrue(rc == 0);

            // Generate a message that will exceed the 1,000 continuation limit.
            int i = 0;
            String tooLong = "";
            for (; i < 1001; i++) {
                tooLong = tooLong + longMsg2;
            }
            rc = loggingHardcopyLogHandler.ntv_WriteToOperatorProgrammerAndHardcopy((tooLong + '\0').getBytes("Cp1047"));
            // Assert that we truncated the response
            assertTrue(rc == -12);

        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }

}
