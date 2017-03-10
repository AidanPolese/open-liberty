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
package com.ibm.ws.config.xml.internal;

/**
 *
 */
class ConfigNotFoundException extends Exception {

    private static final long serialVersionUID = 5761804130272942496L;

    /**
     * @param message
     */
    public ConfigNotFoundException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ConfigNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ConfigNotFoundException(Throwable cause) {
        super(cause);
    }

}
