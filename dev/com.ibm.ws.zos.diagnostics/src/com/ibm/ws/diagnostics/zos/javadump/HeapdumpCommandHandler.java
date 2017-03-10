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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.wsspi.zos.command.processing.CommandHandler;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 * An implementation of an MVS console command handler that allows a
 * system operator to request a jvm heapdump.
 */
public class HeapdumpCommandHandler implements CommandHandler {

    /**
     * A human readable name for this handler.
     */
    protected static final String NAME = "Heapdump Command Handler";

    private static final String COMMAND_NAME = "HEAPDUMP";

    /**
     * Help text.
     */
    protected static final List<String> HELP_TEXT = new ArrayList<String>();
    static {
        HELP_TEXT.add("Issue \"MODIFY <jobname.>identifier,heapdump\"");
        HELP_TEXT.add("  to request a JVM heap dump");
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

        if (command == null || !command.equalsIgnoreCase(COMMAND_NAME)) {
            results.setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
        }
        else {
            Set<String> dumps = new LinkedHashSet<String>();
            dumps.add("heap");
            process.createJavaDump(dumps);
            results.setCompletionStatus(ModifyResults.PROCESSED_COMMAND);
        }
    }
}