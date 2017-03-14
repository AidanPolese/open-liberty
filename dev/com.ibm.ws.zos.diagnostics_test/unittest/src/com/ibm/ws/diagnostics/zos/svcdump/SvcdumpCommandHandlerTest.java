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
package com.ibm.ws.diagnostics.zos.svcdump;

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

import com.ibm.ws.diagnostics.zos.svcdump.SvcdumpCommandHandler;
import com.ibm.ws.kernel.zos.NativeMethodManager;
import com.ibm.ws.zos.command.processing.internal.ModifyResultsImpl;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 *
 */
public class SvcdumpCommandHandlerTest {

    private final int NTV_TAKE_SVCDUMP_POSITIVE_RC = 8;
    private final int NTV_TAKE_SVCDUMP_NEGATIVE_RC = -1;

    /**
     * Mock environment for NativeMethodManager and native methods.
     */
    private static Mockery mockery = new JUnit4Mockery();

    SvcdumpCommandHandler svcDumpCommandHandler;
    int ntv_takeSvcDumpReturncode = 0;

    class TestSvcdumpCommandHandler extends SvcdumpCommandHandler {
        @Override
        protected int ntv_takeSvcDump(String id) {
            return ntv_takeSvcDumpReturncode;
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
        svcDumpCommandHandler = new TestSvcdumpCommandHandler();
    }

    @After
    public void tearDown() {
        svcDumpCommandHandler = null;
    }

    @Test
    public void testLifecycle() {
        svcDumpCommandHandler = new SvcdumpCommandHandler();

        final NativeMethodManager mockNmm = mockery.mock(NativeMethodManager.class);

        // Set up Expectations of method calls for the mock NativeMethodManager.
        mockery.checking(new Expectations() {
            {
                oneOf(mockNmm).registerNatives(with(equal(SvcdumpCommandHandler.class)));
            }
        });

        svcDumpCommandHandler.setNativeMethodManager(mockNmm);
        assertTrue(svcDumpCommandHandler.nativeMethodManager == mockNmm);

        // Activate the component
        svcDumpCommandHandler.activate();

        svcDumpCommandHandler.unsetNativeMethodManager(mockNmm);
        assertTrue(null == svcDumpCommandHandler.nativeMethodManager);

        // Test deactivate
        svcDumpCommandHandler.deactivate();

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(SvcdumpCommandHandler.NAME, svcDumpCommandHandler.getName());
    }

    @Test
    public void testGetHelp() throws Exception {
        assertEquals(SvcdumpCommandHandler.HELP_TEXT, svcDumpCommandHandler.getHelp());
    }

    @Test
    public void testHandleModify() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "svcdump";
        svcDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.PROCESSED_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyUpper() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "SVCDUMP";
        svcDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.PROCESSED_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyMixed() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "sVcDuMp";
        svcDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.PROCESSED_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyBadCommand() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "sVcDuMpbad";
        svcDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.UNKNOWN_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyBadCommand2() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "sVcDuMp bad";
        svcDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.UNKNOWN_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyBadCommand3() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "";
        svcDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.UNKNOWN_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyBadCommandNull() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = null;
        svcDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.UNKNOWN_COMMAND, modifyResults.getCompletionStatus());
    }

    @Test
    public void testHandleModifyPositiveReturnCode() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "svcdump";
        ntv_takeSvcDumpReturncode = NTV_TAKE_SVCDUMP_POSITIVE_RC;
        svcDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.ERROR_PROCESSING_COMMAND, modifyResults.getCompletionStatus());
        List<String> responses = modifyResults.getResponses();
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        Iterator<String> it = responses.iterator();
        assertTrue(it.hasNext());
        String currentMsg = it.next();
        assertEquals(svcDumpCommandHandler.SDUMPX_ERROR + String.valueOf(ntv_takeSvcDumpReturncode),
                     currentMsg);
    }

    @Test
    public void testHandleModifyNegativeReturnCode() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "svcdump";
        ntv_takeSvcDumpReturncode = NTV_TAKE_SVCDUMP_NEGATIVE_RC;
        svcDumpCommandHandler.handleModify(commandString, modifyResults);
        assertFalse(modifyResults.responsesContainMSGIDs());
        assertEquals(ModifyResults.ERROR_PROCESSING_COMMAND, modifyResults.getCompletionStatus());
        List<String> responses = modifyResults.getResponses();
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        Iterator<String> it = responses.iterator();
        assertTrue(it.hasNext());
        String currentMsg = it.next();
        assertEquals(svcDumpCommandHandler.INTERNAL_ERROR + String.valueOf(ntv_takeSvcDumpReturncode),
                     currentMsg);
    }

}
