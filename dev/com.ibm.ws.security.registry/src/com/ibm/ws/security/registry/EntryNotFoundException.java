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
 * Thrown by UserRegistry to indicate no such entry exists
 * in the UserRegistry.
 */
public class EntryNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    // Implementation note:
    // No default constructor should be provided:
    // An EntryNotFoundException should inform the caller which entry could not be found.

    /**
     * @see java.lang.Exception#Exception(String)
     */
    public EntryNotFoundException(String msg) {
        super(msg);
    }

    /**
     * @see java.lang.Exception#Exception(String, Throwable)
     */
    public EntryNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
