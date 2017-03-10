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
package com.ibm.websphere.config;

/**
 *
 */
public class ConfigRetrieverException extends Exception {

    private static final long serialVersionUID = 1890911282859794103L;

    /**
     * @param cause
     */
    public ConfigRetrieverException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ConfigRetrieverException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ConfigRetrieverException(String message) {
        super(message);
    }

}
