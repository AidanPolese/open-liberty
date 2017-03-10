/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.launch.service;

/**
 * Exception representing failure during Server Command Processing
 */
public class CommandProcessingException extends Exception {

    /** serial version id for this exception. */
    static final long serialVersionUID = -6243464083666116670L;

    /**
     * Creates an exception with the provided message.
     * 
     * @param message The error message.
     */
    public CommandProcessingException(String message) {
        super(message);
    }

    /**
     * Creates an exception from the provided cause.
     * 
     * @param cause the cause of the current exception
     */
    public CommandProcessingException(Throwable cause) {
        super(cause);
    }

}
