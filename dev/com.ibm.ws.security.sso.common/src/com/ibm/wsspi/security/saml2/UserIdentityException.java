/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.security.saml2;

/**
 *
 */
public class UserIdentityException extends Exception {
    static final long serialVersionUID = -3387516993124229949L;

    public UserIdentityException() {
        super();
    }

    public UserIdentityException(String message) {
        super(message);
    }

    public UserIdentityException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserIdentityException(Throwable cause) {
        super(cause);
    }
}
