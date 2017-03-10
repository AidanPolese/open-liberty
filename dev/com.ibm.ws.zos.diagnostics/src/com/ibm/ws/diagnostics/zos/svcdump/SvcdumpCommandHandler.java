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

import java.util.ArrayList;
import java.util.List;

import com.ibm.ws.kernel.zos.NativeMethodManager;
import com.ibm.wsspi.zos.command.processing.CommandHandler;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 * An implementation of an MVS console command handler that allows a
 * system operator to request a svcdump.
 */
public class SvcdumpCommandHandler implements CommandHandler {

    /**
     * A human readable name for this handler.
     */
    final static String NAME = "SVCDUMP Command Handler";

    /**
     * string used when SDUMPX returned a non zero.
     */
    public final String SDUMPX_ERROR = "SDUMPX returned return code ";

    /**
     * string used when some other error occurred when attempting to take a sdump.
     */
    public final String INTERNAL_ERROR = "Internal error occurred. Error code is ";

    /**
     * Help text.
     */
    final static List<String> HELP_TEXT = buildHelpText();

    /**
     * native method manager reference.
     */
    protected NativeMethodManager nativeMethodManager = null;

    /**
     * DS method to activate this component.
     */
    protected void activate() {
        // Attempt to load native code via the method manager.
        nativeMethodManager.registerNatives(SvcdumpCommandHandler.class);
    }

    /**
     * DS method to deactivate this component.
     */
    protected void deactivate() {}

    /**
     * Method to set the native method manager.
     */
    protected void setNativeMethodManager(NativeMethodManager nativeMethodManager) {
        this.nativeMethodManager = nativeMethodManager;
    }

    /**
     * Method to unset the native method manager.
     */
    protected void unsetNativeMethodManager(NativeMethodManager nativeMethodManager) {
        if (this.nativeMethodManager == nativeMethodManager) {
            this.nativeMethodManager = null;
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
     * Get the name of this console command handler.
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

        if (command != null && command.equalsIgnoreCase("svcdump")) {
            int dumpReturnCode = ntv_takeSvcDump("MODIFY SERVER,SVCDUMP COMMAND");
            if (dumpReturnCode != 0) {
                if (dumpReturnCode > 0) {
                    responses.add(SDUMPX_ERROR + String.valueOf(dumpReturnCode));
                } else {
                    responses.add(INTERNAL_ERROR + String.valueOf(dumpReturnCode));
                }
                results.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
            } else {
                results.setCompletionStatus(ModifyResults.PROCESSED_COMMAND);
            }
        } else {
            results.setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
        }

        results.setResponsesContainMSGIDs(false);
        results.setResponses(responses);
    }

    /**
     * Method to create the help text.
     */
    private static List<String> buildHelpText() {
        List<String> responses = new ArrayList<String>();

        responses.add("Issue \"MODIFY <jobname.>identifier,svcdump\"");
        responses.add("  to request the system to initiate a svc dump");

        return responses;
    }

    /**
     * Call to native code to request a svcdump.
     * 
     */
    protected native int ntv_takeSvcDump(String id);

}
