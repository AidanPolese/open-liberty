/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility;

import java.io.PrintStream;

import com.ibm.ws.config.utility.utils.ConsoleWrapper;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.repository.exceptions.RepositoryException;

/**
 * Defines the access point for all actions so we can query and invoke the available tasks.
 */
public interface ConfigUtilityAction {
    /**
     * Answers the name of the action, which should be as succinct as possible.
     * The action name is used in help display and is how the action is invoked
     * by the script user.
     * 
     * @return the name of the action
     */
    String getActionName();

    /**
     * Perform the task logic.
     * 
     * @param stdin handle to standard input wrapper
     * @param stdout handle to standard output
     * @param stderr handle to standard error
     * @param args The arguments passed to the script, including the task name
     * @throws IllegalArgumentException if the task was called with invalid arguments
     * @throws TaskErrorException
     */
    void handleAction(ConsoleWrapper stdin, PrintStream stdout, PrintStream stderr, String[] args)
                    throws TaskErrorException, RepositoryException, InstallException;
}
