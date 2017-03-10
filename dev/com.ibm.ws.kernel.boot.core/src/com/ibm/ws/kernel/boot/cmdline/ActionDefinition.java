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

import java.util.List;

/**
 * The definition of a command line action. Allows retrieval of known
 * options and required positional arguments for verification.
 */
public interface ActionDefinition {

    /** Return fixed array of required command options */
    public List<String> getCommandOptions();

    /** Return the number of expected positional arguments for command line verification */
    public int numPositionalArgs();

    /**
     * Perform the action logic, usually by delegating to an internal {@link ActionHandler}
     * 
     * @param args The arguments passed to the script.
     * @throws IllegalArgumentException if the task was called with invalid arguments
     */
    public ExitCode handleTask(Arguments args);
}
