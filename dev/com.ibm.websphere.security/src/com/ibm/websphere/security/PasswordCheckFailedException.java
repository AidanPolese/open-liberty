//  @(#) 1.6 SERV1/ws/code/security.sas/src/com/ibm/websphere/security/PasswordCheckFailedException.java, WAS.security.sas, WASX.SERV1, nn1148.03 2/18/05 16:41:27 [12/4/11 15:43:06]
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// All Rights Reserved * Licensed Materials - Property of IBM

package com.ibm.websphere.security;

/**
 * Thrown to indicate that the userId/Password combination does not exist
 * in the specified custom registry.
 * 
 * @ibm-spi
 */
public class PasswordCheckFailedException extends WSSecurityException {

    private static final long serialVersionUID = 3640506429677174874L; //@vj1: Take versioning into account if incompatible changes are made to this class

    /**
     * Create a new PasswordCheckFailedException with an empty description string.
     */
    public PasswordCheckFailedException() {
        super();
    }

    /**
     * Create a new PasswordCheckFailedException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public PasswordCheckFailedException(String message) {
        super(message);
    }

    /**
     * Create a new PasswordCheckFailedException with the Throwable root cause.
     * 
     * @param t the Throwable root cause.
     */
    public PasswordCheckFailedException(Throwable t) {
        super(t);
    }

    /**
     * Create a new PasswordCheckFailedException with the string description and Throwable root cause.
     * 
     * @param message the String describing the exception.
     * @param t the Throwable root cause.
     */
    public PasswordCheckFailedException(String message, Throwable t) {
        super(message, t);
    }

}
