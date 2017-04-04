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
 * Thrown by UserRegistry when the mapCertificate() operation is not supported.
 */
public class CertificateMapNotSupportedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @see java.lang.Exception#Exception(String)
     */
    public CertificateMapNotSupportedException(String msg) {
        super(msg);
    }

}
