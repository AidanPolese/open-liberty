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
package com.ibm.websphere.ssl;

/**
 *
 */
public class SSLConfigurationNotAvailableException extends SSLException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public SSLConfigurationNotAvailableException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public SSLConfigurationNotAvailableException(Exception cause) {
        super(cause);
    }

    /**
     * Constructor.
     * 
     * @param message
     */
    public SSLConfigurationNotAvailableException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message
     * @param cause
     */
    public SSLConfigurationNotAvailableException(String message, Exception cause) {
        super(message, cause);
    }

}
