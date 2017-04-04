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
 * This exception is thrown when a token is no longer valid.
 * 
 * @ibm-api
 * @author IBM
 * @version 1.0
 * @ibm-spi
 */
public class TokenExpiredException extends com.ibm.websphere.security.WSSecurityException {
    private static final long serialVersionUID = 3704719086095702906L;
    private long expiration = 0;

    public TokenExpiredException() {
        super();
    }

    public TokenExpiredException(String debug_message) {
        super(debug_message);
    }

    public TokenExpiredException(Throwable t) {
        super(t);
    }

    public TokenExpiredException(String debug_message, Throwable t) {
        super(debug_message, t);
    }

    public TokenExpiredException(long expiration, String debug_message) {
        super(debug_message);
        this.expiration = expiration;
    }

    public long getExpiration() {
        return expiration;
    }
}
