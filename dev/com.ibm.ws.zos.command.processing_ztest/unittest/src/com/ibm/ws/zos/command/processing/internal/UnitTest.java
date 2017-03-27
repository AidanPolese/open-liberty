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
package com.ibm.ws.zos.command.processing.internal;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import test.common.SharedOutputManager;
import test.common.zos.NativeLibraryUtils;
import test.common.zos.ZosOperations;

/**
 *
 */
public class UnitTest {
    private static SharedOutputManager outputMgr;
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
        NativeLibraryUtils.registerNatives(CommandProcessor.class);
    }

    /**
     * Final teardown work when class is exiting.
     * 
     * @throws Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        NativeLibraryUtils.reset();

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
     * Test CommandProcessor.ntv_getIEZCOMReference returns a CIB
     * pointer.
     */
    @Test
    public void test_ntv_getIEZCOMReference() {
        final String m = "test_ntv_getIEZCOMReference";
        try {
            // Do stuff here. The outputMgr catches all output issued to stdout
            // or stderr
            // unless/until an unexpected exception occurs. failWithThrowable
            // will copy
            // all captured output back to the original streams before failing
            // the testcase.
            CommandProcessor cp = new CommandProcessor();
            long iezcom = cp.ntv_getIEZCOMReference();
            assertTrue(iezcom != 0);
        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }

    /**
     * Call to stop listening
     * 
     */
    @Test
    public void test_ntv_stopListeningForCommands() {
        final String m = "test_ntv_stopListeningForCommands";
        try {
            CommandProcessor cp = new CommandProcessor();
            long iezcom = cp.ntv_getIEZCOMReference();
            assertTrue(iezcom != 0);
            cp.ntv_stopListeningForCommands();
        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }

    /**
     * To test the "Call to native code to wait for a Command" (ntv_getCommand)
     * we would need another thread to issue the method and this
     * thread to then monitor it. We could then either issue a
     * command to break it out or issue the ntv_stopListeningForCommands
     * method to break it out.
     * TODO:
     */
    @Ignore
    @Test
    public void test_ntv_getCommand() {
        // TODO:
    }

    /**
     * Test issuing command responses
     * 
     * 
     */
    @Test
    public void test_ntv_issueCommandResponse() {
        final String m = "test_ntv_issueCommandResponse";
        try {
            CommandProcessor cp = new CommandProcessor();
            long iezcom = cp.ntv_getIEZCOMReference();
            assertTrue(iezcom != 0);

            //int ntv_issueCommandResponse(byte[] response, long cart, int consid);

            int masterConid = 1;
            long cart = 0;
            byte[] response = "TSTCP0001: simple message, no cart, consid(1)".getBytes("Cp1047");
            int rc = cp.ntv_issueCommandResponse(response, cart, masterConid);
            assertTrue(rc == 0);

            rc = cp.ntv_issueCommandResponse((byte[]) null, cart, masterConid);
            assertTrue(rc == -1);

            response = "TSTCP0003: simple message, cart(12345678), consid(1)".getBytes("Cp1047");
            rc = cp.ntv_issueCommandResponse(response, cart, masterConid);
            assertTrue(rc == 0);

            cp.ntv_stopListeningForCommands();
        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }

    /**
     * Test issuing command responses
     * 
     * 
     */
    @Test
    public void test_ntv_issueCommandResponse_ml() {
        final String m = "test_ntv_issueCommandResponse";
        try {
            CommandProcessor cp = new CommandProcessor();
            long iezcom = cp.ntv_getIEZCOMReference();
            assertTrue(iezcom != 0);

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
            int masterConid = 1;
            long cart = 0;
            byte[] response = longMsg.getBytes("Cp1047");
            int rc = cp.ntv_issueCommandResponse(response, cart, masterConid);
            assertTrue(rc == 0);

            response = longMsg2.getBytes("Cp1047");
            rc = cp.ntv_issueCommandResponse(response, cart, masterConid);
            assertTrue(rc == 0);

            // Generate a message that will exceed the 1,000 continuation limit.
            int i = 0;
            String tooLong = "";
            for (; i < 1001; i++) {
                tooLong = tooLong + longMsg2;
            }
            response = tooLong.getBytes("Cp1047");
            rc = cp.ntv_issueCommandResponse(response, cart, masterConid);

            // Assert that we truncated the response
            assertTrue(rc == -12);

        } catch (Throwable t) {
            outputMgr.failWithThrowable(m, t);
        }
    }
}
