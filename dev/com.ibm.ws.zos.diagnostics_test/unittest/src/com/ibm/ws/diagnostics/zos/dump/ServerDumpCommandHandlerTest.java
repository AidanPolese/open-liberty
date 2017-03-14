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
package com.ibm.ws.diagnostics.zos.dump;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.ws.zos.command.processing.internal.ModifyResultsImpl;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.kernel.service.location.WsResource;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 * Unit test for JavaHeapCommandHandler
 */
public class ServerDumpCommandHandlerTest {
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
            throw new IllegalStateException();
        }

        @Override
        public String createServerDump(Set<String> includedDumps) {
            assertNotNull(includedDumps);
            for (String dump : includedDumps) {
                assertTrue(ServerDumpCommandHandler.VALID_DUMPS.contains(dump));
            }
            return "notanullstring";
        }
    }

    private static class ExpectNoneLibertyProcessImpl extends MyLibertyProcessImpl {
        @Override
        public String createServerDump(Set<String> includedDumps) {
            assertNotNull(includedDumps);
            assertEquals(includedDumps.size(), 0);
            return "notanullstring";
        }
    }

    private static class ExpectThreadLibertyProcessImpl extends MyLibertyProcessImpl {
        @Override
        public String createServerDump(Set<String> includedDumps) {
            assertNotNull(includedDumps);
            assertEquals(includedDumps.size(), 1);
            assertTrue(includedDumps.contains("thread"));
            return "notanullstring";
        }
    }

    private static class ExpectHeapLibertyProcessImpl extends MyLibertyProcessImpl {
        @Override
        public String createServerDump(Set<String> includedDumps) {
            assertNotNull(includedDumps);
            assertEquals(includedDumps.size(), 1);
            assertTrue(includedDumps.contains("heap"));
            return "notanullstring";
        }
    }

    private static class ExpectThreadAndHeapLibertyProcessImpl extends MyLibertyProcessImpl {
        @Override
        public String createServerDump(Set<String> includedDumps) {
            assertNotNull(includedDumps);
            assertEquals(includedDumps.size(), 2);
            assertTrue(includedDumps.contains("thread"));
            assertTrue(includedDumps.contains("heap"));
            return "notanullstring";
        }
    }

    private static class MyWsLocationAdminImpl implements WsLocationAdmin {
        @Override
        public String printLocations(boolean useLineBreaks) {
            return null;
        }

        @Override
        public UUID getServerId() {
            return null;
        }

        @Override
        public String getServerName() {
            return "somethingthatisntnull";
        }

        @Override
        public File getBundleFile(Object caller, String relativeBundlePath) {
            return null;
        }

        @Override
        public WsResource getRuntimeResource(String relativeRuntimePath) {
            return null;
        }

        @Override
        public WsResource getServerResource(String relativeServerPath) {
            return null;
        }

        @Override
        public WsResource getServerOutputResource(String relativeServerPath) {
            return null;
        }

        @Override
        public WsResource getServerWorkareaResource(String relativeServerWorkareaPath) {
            return null;
        }

        @Override
        public WsResource resolveResource(String resourceURI) {
            return null;
        }

        @Override
        public WsResource resolveResource(URI resourceURI) {
            return null;
        }

        @Override
        public WsResource asResource(File file, boolean isFile) {
            return null;
        }

        @Override
        public String resolveString(String string) {
            return null;
        }

        @Override
        public Iterator<WsResource> matchResource(String resourceGroupName, String resourceRegex, int limit) {
            return null;
        }

        @Override
        public WsResource addLocation(String fileName, String symbolicName) {
            return null;
        }
    }

    private static ServerDumpCommandHandler handler;
    private static MyLibertyProcessImpl process;
    private static MyWsLocationAdminImpl locationAdmin;

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
        handler = new ServerDumpCommandHandler();
        process = new MyLibertyProcessImpl();
        locationAdmin = new MyWsLocationAdminImpl();
        handler.setLibertyProcess(process);
        handler.setLocationAdmin(locationAdmin);
    }

    @After
    public void tearDown() {
        handler.unsetLibertyProcess(process);
        handler.unsetLocationAdmin(locationAdmin);
        process = null;
        locationAdmin = null;
        handler = null;
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(ServerDumpCommandHandler.NAME, handler.getName());
    }

    @Test
    public void testGetHelp() throws Exception {
        assertEquals(ServerDumpCommandHandler.HELP_TEXT, handler.getHelp());
    }

    @Test
    public void testHandleModify() throws Exception {
        handler.setLibertyProcess(new ExpectNoneLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dump";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyUpper() throws Exception {
        handler.setLibertyProcess(new ExpectNoneLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "DUMP";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyMixed() throws Exception {
        handler.setLibertyProcess(new ExpectNoneLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dUmP";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyNoJavaDumps() throws Exception {
        handler.setLibertyProcess(new ExpectNoneLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dump,include=";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyWithJavacore() throws Exception {
        handler.setLibertyProcess(new ExpectThreadLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dump,include=thread";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyWithJavaheap() throws Exception {
        handler.setLibertyProcess(new ExpectHeapLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dump,include=heap";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyWithJavacoreAndJavaheap() throws Exception {
        handler.setLibertyProcess(new ExpectThreadAndHeapLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dump,include=thread,heap";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyWithJavacoreAndJavaheapReversed() throws Exception {
        handler.setLibertyProcess(new ExpectThreadAndHeapLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dump,include=heap,thread";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyWithBadJavaDumps() throws Exception {
        handler.setLibertyProcess(new ExpectNoneLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dump,include=notadump";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.PROCESSED_COMMAND);
    }

    @Test
    public void testHandleModifyWithBadJavaDumps2() throws Exception {
        handler.setLibertyProcess(new ExpectThreadAndHeapLibertyProcessImpl());
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dump,include=baddump1,thread,baddump2,heap";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getResponses().size(), 1);
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

        String commandString = "dumpNotACommand";
        handler.handleModify(commandString, modifyResults);

        assertFalse(modifyResults.responsesContainMSGIDs());
        assertNotNull(modifyResults.getResponses());
        assertEquals(modifyResults.getCompletionStatus(), ModifyResults.UNKNOWN_COMMAND);
    }

    @Test
    public void testHandleModifyBadCommand3() throws Exception {
        ModifyResultsImpl modifyResults = new ModifyResultsImpl();

        String commandString = "dump,include";
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