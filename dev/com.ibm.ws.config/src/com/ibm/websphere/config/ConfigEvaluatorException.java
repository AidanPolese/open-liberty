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
public class ConfigEvaluatorException extends Exception {

    private static final long serialVersionUID = 7497451013878508812L;

    /**
     * @param cause
     */
    public ConfigEvaluatorException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ConfigEvaluatorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ConfigEvaluatorException(String message) {
        super(message);
    }

}
