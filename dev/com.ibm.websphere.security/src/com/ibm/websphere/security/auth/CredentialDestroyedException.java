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
package com.ibm.websphere.security.auth;

/**
 * <p>
 * Thrown if credential is destroyed. A destroyed credential can not be used at all.
 * </p>
 * 
 * @ibm-api
 * @author IBM Corporation
 * @version 1.0
 * @see java.security.GeneralSecurityException
 * @since 1.0
 * @ibm-spi
 */

public class CredentialDestroyedException extends Exception {
    private static final long serialVersionUID = 1L;

    public CredentialDestroyedException(String msg) {
        super(msg);
    }

    public CredentialDestroyedException(Throwable t) {
        super(t);
    }

    public CredentialDestroyedException(String debug_message, Throwable t) {
        super(debug_message, t);
    }
}
