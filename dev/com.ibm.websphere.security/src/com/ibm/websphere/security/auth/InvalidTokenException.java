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
public class InvalidTokenException extends com.ibm.websphere.security.WSSecurityException {

    private static final long serialVersionUID = 5335739079891689755L;

    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String debug_message) {
        super(debug_message);
    }

    public InvalidTokenException(Throwable t) {
        super(t);
    }

    public InvalidTokenException(String debug_message, Throwable t) {
        super(debug_message, t);
    }

}
