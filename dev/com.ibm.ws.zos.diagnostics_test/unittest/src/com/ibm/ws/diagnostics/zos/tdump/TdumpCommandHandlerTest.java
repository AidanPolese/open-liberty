/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.diagnostics.zos.tdump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.ws.diagnostics.zos.tdump.TdumpCommandHandler;
import com.ibm.ws.kernel.zos.NativeMethodManager;
import com.ibm.ws.zos.command.processing.internal.ModifyResultsImpl;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 *
 */
public class TdumpCommandHandlerTest {

    private final int NTV_TAKE_TDUMP_POSITIVE_RC = 8;
    private final int NTV_TAKE_TDUMP_NEGATIVE_RC = -1;
    /**
     * Mock environment for NativeMethodManager and native methods.
     */
    private static Mockery mockery = new JUnit4Mockery();

    TdumpCommandHandler tDumpCommandHandler;
    int ntv_takeTDumpReturncode = 0;

    class TestTdumpCommandHandler extends TdumpCommandHandler {
        @Override
        protected int ntv_takeTDump() {
            return ntv_takeTDumpReturncode;
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setup() throws Exception {
        tDumpCommandHandler = new TestTdumpCommandHandler();
    }

    @After
    public void tearDown() {
        tDumpCommandHandler = null;
    }

    @Test
    public void testLifecycle() {
        tDumpCommandHandler = new TdumpCommandHandler();

        final NativeMethodManager mockNmm = mockery.mock(NativeMethodManager.class);

        // Set up Expectations of method calls for the mock NativeMethodManager.
        mockery.checking(new Expectations() {
            {
                oneOf(mockNmm).registerNatives(with(equal(TdumpCommandHandler.class)));
            }
        });

        tDumpCommandHandler.setNativeMethodManager(mockNmm);
        assertTrue(tDumpCommandHandler.nativeMethodManager == mockNmm);

        // Activate the component
        tDumpCommandHandler.activate();

        tDumpCommandHandler.unsetNativeMethodManager(mockNmm);
        assertTrue(null == tDumpCommandHandler.nativeMethodManager);

        // Test deactivate
        tDumpCommandHandler.deactivate();

        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(TdumpCommandHandler.NAME, tDumpCommandHandler.getName());
    }

    @Test
    public void testGetHelp() throws Exception {
        assertEquals(TdumpCommandHandler.HELP_TEXT, tDumpCommandHandler.getHelp());
    }

    @Test
    public void testHandleModify() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "tdump";
        tDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.PROCESSED_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyUpper() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "TDUMP";
        tDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.PROCESSED_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyMixed() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "TdUmP";
        tDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.PROCESSED_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyBadCommand() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "TdUmPbad";
        tDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.UNKNOWN_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyBadCommand2() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "TdUmP bad";
        tDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.UNKNOWN_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyBadCommand3() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "";
        tDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.UNKNOWN_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyBadCommandNull() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = null;
        tDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.UNKNOWN_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyPositiveReturnCode() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "tdump";
        ntv_takeTDumpReturncode = NTV_TAKE_TDUMP_POSITIVE_RC;
        tDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.ERROR_PROCESSING_COMMAND, modifyResults.getCompletionStatus());
        List<String> responses = modifyResults.getResponses();
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        Iterator<String> it = responses.iterator();
        assertTrue(it.hasNext());
        String currentMsg = it.next();
        assertEquals(tDumpCommandHandler.INTERNAL_ERROR + String.valueOf(ntv_takeTDumpReturncode),
                     currentMsg);
    }

    @Test
    public void testHandleModifyNegativeReturnCode() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "tdump";
        ntv_takeTDumpReturncode = NTV_TAKE_TDUMP_NEGATIVE_RC;
        tDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.ERROR_PROCESSING_COMMAND, modifyResults.getCompletionStatus());
        List<String> responses = modifyResults.getResponses();
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        Iterator<String> it = responses.iterator();
        assertTrue(it.hasNext());
        String currentMsg = it.next();
        assertEquals(tDumpCommandHandler.DUMP_ERROR + String.valueOf(ntv_takeTDumpReturncode),
                     currentMsg);
    }

}
