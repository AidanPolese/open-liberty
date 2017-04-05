/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1997, 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.security.auth;

/**
 * This exception is thrown whenever authentication fails.
 * 
 * @ibm-api
 * @author IBM
 * @version 1.0
 * @ibm-spi
 */
public class TokenCreationFailedException extends com.ibm.websphere.security.WSSecurityException {

    private static final long serialVersionUID = 3947153031152584525L;

    public TokenCreationFailedException() {
        super();
    }

    public TokenCreationFailedException(String debug_message) {
        super(debug_message);
    }

    public TokenCreationFailedException(Throwable t) {
        super(t);
    }

    public TokenCreationFailedException(String debug_message, Throwable t) {
        super(debug_message, t);
    }

}
