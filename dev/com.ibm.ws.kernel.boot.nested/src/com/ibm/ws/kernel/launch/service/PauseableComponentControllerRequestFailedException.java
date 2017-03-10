/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.kernel.launch.service;

/**
 * Exception representing no pauseable components during a pause or resume request
 */
public class PauseableComponentControllerRequestFailedException extends Exception {

    /**
     * Constructs a new instance with null as its detail message and cause.
     */
    public PauseableComponentControllerRequestFailedException() {
        super();
    }

    /**
     * Constructs a new instance with the specified detail message.
     *
     * @param message The detail message.
     */
    public PauseableComponentControllerRequestFailedException(String message) {
        super(message);
    }

    /**
     * Constructs a new instance with the specified cause.
     *
     * @param throwable The cause of type Throwable.
     */
    public PauseableComponentControllerRequestFailedException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs a new instance with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param throwable The cause of type Throwable.
     */
    public PauseableComponentControllerRequestFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }

}