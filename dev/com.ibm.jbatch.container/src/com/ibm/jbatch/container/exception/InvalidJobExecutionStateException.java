/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.jbatch.container.exception;

/**
 * This is used when a stop occurs, but the exact job execution is not found
 */
public class InvalidJobExecutionStateException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidJobExecutionStateException() {
        // TODO Auto-generated constructor stub
    }

    public InvalidJobExecutionStateException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public InvalidJobExecutionStateException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public InvalidJobExecutionStateException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
