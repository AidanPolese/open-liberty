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
package com.ibm.ws.security.authentication;

import javax.security.auth.login.LoginException;

public class AuthenticationException extends LoginException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param e
     */
    public AuthenticationException(String message, Exception e) {
        super(message);
    }
}
