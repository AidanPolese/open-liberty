/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.feature.internal;

/**
 *
 */
public class ProvisioningException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public ProvisioningException() {}

    /**
     * @param message
     */
    public ProvisioningException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ProvisioningException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ProvisioningException(String message, Throwable cause) {
        super(message, cause);
    }
}
