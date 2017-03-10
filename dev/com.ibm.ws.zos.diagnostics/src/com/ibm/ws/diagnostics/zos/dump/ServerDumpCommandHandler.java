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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.zos.command.processing.CommandHandler;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 * An implementation of an MVS console command handler that allows a
 * system operator to request a server dump.
 */
public class ServerDumpCommandHandler implements CommandHandler {

    /**
     * A human readable name for this handler.
     */
    protected static final String NAME = "Server Dump Command Handler";

    /**
     * Help text.
     */
    protected static final List<String> HELP_TEXT = new ArrayList<String>();
    static {
        HELP_TEXT.add("Issue \"MODIFY <jobname.>identifier,dump[,include=javadump1,javadump2,...]\"");
        HELP_TEXT.add("  to request a server dump that can include optional java dumps");
        HELP_TEXT.add("  Valid values for java dumps are:");
        HELP_TEXT.add("    thread (javacore)");
        HELP_TEXT.add("    heap (JVM heap dump)");
    }

    private static final String DUMP_COMMAND = "dump";
    private static final String DUMP_COMMAND_WITH_INCLUDE = "dump,include=";
    private static final int DUMP_COMMAND_WITH_INCLUDE_LENGTH = DUMP_COMMAND_WITH_INCLUDE.length();

    protected static final Set<String> VALID_DUMPS = new LinkedHashSet<String>();
    static {
        VALID_DUMPS.addAll(Arrays.asList("thread", "heap"));
    }

    protected LibertyProcess process = null;

    protected void setLibertyProcess(LibertyProcess process) {
        this.process = process;
    }

    protected void unsetLibertyProcess(LibertyProcess process) {
        if (this.process == process) {
            this.process = null;
        }
    }

    protected WsLocationAdmin locationAdmin = null;

    protected void setLocationAdmin(WsLocationAdmin locationAdmin) {
        this.locationAdmin = locationAdmin;
    }

    protected void unsetLocationAdmin(WsLocationAdmin locationAdmin) {
        if (this.locationAdmin == locationAdmin) {
            this.locationAdmin = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getHelp() {
        return HELP_TEXT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void handleModify(String command, ModifyResults results) {
        List<String> responses = new ArrayList<String>();
        results.setResponsesContainMSGIDs(false);
        results.setResponses(responses);

        Set<String> dumps = getOptionalDumpSet(command);
        if (dumps == null) {
            results.setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
        }
        else {
            String dumpFileName = process.createServerDump(dumps);
            if (dumpFileName != null) {
                responses.add("Server " + locationAdmin.getServerName() + " dump complete in " + dumpFileName + ".");
                results.setCompletionStatus(ModifyResults.PROCESSED_COMMAND);
            }
            else {
                responses.add("Error creating server dump, see server log for details.");
                results.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
            }
        }
    }

    /**
     * Parses the command string to create a set of dump names to be included
     * in the server dump.
     * 
     * @param command the command string
     * @return a set of dump names to be included in the server dump, or null if the command
     *         string contains a syntax error
     */
    private Set<String> getOptionalDumpSet(String command) {
        Set<String> dumps = new LinkedHashSet<String>();

        if (command == null) {
            return null;
        }

        command = command.toLowerCase();

        if (command.equals(DUMP_COMMAND)) {
            // no optional args were specified
            return dumps;
        }

        if (!command.startsWith(DUMP_COMMAND_WITH_INCLUDE)) {
            // command has an invalid syntax
            return null;
        }

        // to be consistent with server script behavior, unrecognized dump types are NOT considered a syntax
        // error and are simply ignored
        String[] requestedDumps = command.substring(DUMP_COMMAND_WITH_INCLUDE_LENGTH).split(",");
        for (String requestedDump : requestedDumps) {
            if (VALID_DUMPS.contains(requestedDump)) {
                dumps.add(requestedDump);
            }
        }

        return dumps;
    }
}