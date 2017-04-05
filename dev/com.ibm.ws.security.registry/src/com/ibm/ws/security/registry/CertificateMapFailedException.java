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
 * Thrown when a UserRegistry can not successfully map a provided
 * certificate to an entry in the UserRegistry.
 */
public class CertificateMapFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    // Implementation note:
    // No default constructor should be provided.
    // A CertificateMapFailedException without a message is not helpful.

    /**
     * @see java.lang.Exception#Exception(String)
     */
    public CertificateMapFailedException(String msg) {
        super(msg);
    }

    /**
     * @see java.lang.Exception#Exception(String, Throwable)
     */
    public CertificateMapFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
