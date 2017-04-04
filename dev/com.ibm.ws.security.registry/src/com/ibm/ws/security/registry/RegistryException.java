/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.registry;

/**
 * Generic Exception type for UserRegistry issues. Any UserRegistry
 * implementation specific problem should be reported back via a
 * thrown RegistryException.
 */
public class RegistryException extends Exception {
    private static final long serialVersionUID = 1L;

    // Implementation note:
    // No default constructor should be provided.
    // A RegistryException without a message is meaningless.

    /**
     * @see java.lang.Exception#Exception(String)
     */
    public RegistryException(String msg) {
        super(msg);
    }

    /**
     * @see java.lang.Exception#Exception(String, Throwable)
     */
    public RegistryException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
