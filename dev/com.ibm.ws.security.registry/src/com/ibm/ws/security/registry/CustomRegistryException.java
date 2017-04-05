/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

package com.ibm.ws.security.registry;

/**
 * Thrown to indicate that a error occurred while using the
 * specified custom registry.
 * 
 * @ibm-spi
 */

public class CustomRegistryException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new CustomRegistryException with an empty description string.
     */
    public CustomRegistryException() {
        super();
    }

    /**
     * Create a new CustomRegistryException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public CustomRegistryException(String message) {
        super(message);
    }

    /**
     * Create a new CustomRegistryException with the associated Throwable root cause.
     * 
     * @param t the Throwable root cause.
     */
    public CustomRegistryException(Throwable t) {
        super(t);
    }

    /**
     * Create a new CustomRegistryException with the string description and Throwable root cause.
     * 
     * @param message the String describing the exception.
     * @param t the Throwable root cause.
     */
    public CustomRegistryException(String message, Throwable t) {
        super(message, t);
    }

}
