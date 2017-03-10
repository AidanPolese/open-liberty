/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.cmdline;

import java.io.PrintStream;

/**
 * Defines the access point for all child tasks so the master task controller
 * can query and invoke the available tasks.
 */
public interface ActionHandler {

    /**
     * Perform the action logic.
     * 
     * @param stdout handle to standard output
     * @param stderr handle to standard error
     * @param args The arguments passed to the script.
     * @throws IllegalArgumentException if the task was called with invalid arguments
     */
    ExitCode handleTask(PrintStream stdout, PrintStream stderr, Arguments args);
}
