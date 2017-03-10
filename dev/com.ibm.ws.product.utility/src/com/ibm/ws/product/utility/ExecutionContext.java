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
package com.ibm.ws.product.utility;

import java.io.PrintStream;
import java.util.Set;

/**
 *
 */
public interface ExecutionContext {

    public CommandConsole getCommandConsole();

    /**
     * Return the arguments input from the console, will NOT contain the task name
     *
     * @return
     */
    public String[] getArguments();

    /**
     * Returns a set of all option names specified to the execution environment.
     *
     * @return a set of all specified options
     */
    public Set<String> getOptionNames();

    public String getOptionValue(String option);

    public boolean optionExists(String option);

    public CommandTaskRegistry getCommandTaskRegistry();

    public <T> T getAttribute(String name, Class<T> cls);

    public Object getAttribute(String name);

    public void setAttribute(String name, Object value);

    public void setOverrideOutputStream(PrintStream outputStream);
}