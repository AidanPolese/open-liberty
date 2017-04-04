/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.dynamic.bundle;

public class DynamicBundleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DynamicBundleException() {}

    public DynamicBundleException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynamicBundleException(String message) {
        super(message);
    }

    public DynamicBundleException(Throwable cause) {
        super(cause);
    }
}
