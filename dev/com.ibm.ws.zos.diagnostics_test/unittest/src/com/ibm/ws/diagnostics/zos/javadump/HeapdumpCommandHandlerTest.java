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
package com.ibm.ws.diagnostics.zos.javadump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.ws.zos.command.processing.internal.ModifyResultsImpl;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 * Unit test for JavaHeapCommandHandler
 */
public class HeapdumpCommandHandlerTest {
    private static class MyLibertyProcessImpl implements LibertyProcess {
        @Override
        public String[] getArgs() {
            throw new IllegalStateException();
        }

        @Override
        public void shutdown() {
            throw new IllegalStateException();
        }

        @Override
        public void createJavaDump(Set<String> includedDumps) {
            assertNotNull(includedDumps);
            assertEquals(includedDumps.size(), 1);
            assertEquals(includedDumps.iterator().next(), "heap");
        }

        @Override
        public String createServerDump(Set<String> includedDumps) {
            throw new IllegalStateException();
        }
    }

    private static HeapdumpCommandHandler handler;
    private static MyLibertyProcessImpl process;

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
        handler = new HeapdumpCommandHandler();
        process = new MyLibertyProcessImpl();
        handler.setLibertyProcess(process);
    }

    @After
    public void tearDown() {
        handler.unsetLibertyProcess(process);
        process = null;
        handler = null;
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(HeapdumpCommandHandler.NAME, handler.getName());
    }

    @Test
    public void testGetHelp() throws Exception {
        assertEquals(HeapdumpCommandHandler.HELP_TEXT, handler.getHelp());
    }

    @Test
    public void testHandleModify() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "heapdump";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyUpper() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "HEAPDUMP";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyMixed() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "hEaPdUmP";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyBadCommand() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "NOT A command";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.UNKNOWN_COMMAND);
    }

    @Test
    public void testHandleModifyBadCommand2() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "heapdumpNotACommand";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.UNKNOWN_COMMAND);
    }

    @Test
    public void testHandleModifyBadCommandEmpty() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.UNKNOWN_COMMAND);
    }

    @Test
    public void testHandleModifyBadCommandNull() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = null;
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.UNKNOWN_COMMAND);
    }
}
