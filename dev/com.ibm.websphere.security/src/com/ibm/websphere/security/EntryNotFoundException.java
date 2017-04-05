//  @(#) 1.6 SERV1/ws/code/security.sas/src/com/ibm/websphere/security/EntryNotFoundException.java, WAS.security.sas, WASX.SERV1, nn1148.03 2/18/05 16:41:23 [12/4/11 15:43:06]
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// All Rights Reserved * Licensed Materials - Property of IBM

package com.ibm.websphere.security;

/**
 * Thrown to indicate that the specified entry is not found in the
 * custom registry.
 * 
 * @ibm-spi
 */
public class EntryNotFoundException extends WSSecurityException {

    private static final long serialVersionUID = 5789163023036418269L; //@vj1: Take versioning into account if incompatible changes are made to this class

    /**
     * Create a new EntryNotFoundException with an empty description string.
     */
    public EntryNotFoundException() {
        super();
    }

    /**
     * Create a new EntryNotFoundException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public EntryNotFoundException(String message) {
        super(message);
    }

    /**
     * Create a new EntryNotFoundException with the associated Throwable root cause.
     * 
     * @param t the Throwable root cause
     */
    public EntryNotFoundException(Throwable t) {
        super(t);
    }

    /**
     * Create a new EntryNotFoundException with the string description and Throwable root cause.
     * 
     * @param message the String describing the exception.
     * @param t the Throwable root cause.
     */
    public EntryNotFoundException(String message, Throwable t) {
        super(message, t);
    }

}
