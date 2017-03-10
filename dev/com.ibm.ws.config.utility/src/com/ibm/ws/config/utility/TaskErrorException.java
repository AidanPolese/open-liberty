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

/**
 * Exception to encapsulate errors in the task handling.
 */
public class TaskErrorException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Construct an TaskErrorException with an explanation of the error.
     * Null may be passed if the error was already printed.
     * 
     * @param message The error to report, may be null if the message has
     *            already been logged.
     */
    public TaskErrorException(String message) {
        super(message);
    }
}
