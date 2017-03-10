/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.crypto.util;

/**
 * Exception thrown when the password provided for decoding is invalid.
 */
public class UnsupportedConfigurationException extends Exception {

    private static final long serialVersionUID = -6976724223307570873L;

    /**
     * Create a new UnsupportedConfigurationException with an empty string description.
     */
    public UnsupportedConfigurationException() {
        super();
    }

    /**
     * Create a new UnsupportedConfigurationException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public UnsupportedConfigurationException(String message) {
        super(message);
    }

    /**
     * Create a new CustomRegistryException with the string description and Throwable root cause.
     * 
     * @param message the String describing the exception.
     * @param cause the Throwable root cause.
     */
    public UnsupportedConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
