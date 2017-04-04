//  @(#) 1.6 SERV1/ws/code/security.sas/src/com/ibm/websphere/security/NotImplementedException.java, WAS.security.sas, WASX.SERV1, nn1148.03 2/18/05 16:41:25 [12/4/11 15:43:06]
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// All Rights Reserved * Licensed Materials - Property of IBM

package com.ibm.websphere.security;

/**
 * Thrown to indicate that the method is not implemented.
 * 
 * @ibm-spi
 */
public class NotImplementedException extends WSSecurityException {

    private static final long serialVersionUID = 1680889074992585609L; //@vj1: Take versioning into account if incompatible changes are made to this class

    /**
     * Create a new NotImplementedException with an empty description string.
     */
    public NotImplementedException() {
        super();
    }

    /**
     * Create a new NotImplementedException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public NotImplementedException(String name) {
        super(name);
    }

    /**
     * Create a new NotImplementedException with the Throwable root cause.
     * 
     * @param t the Throwable root cause.
     */
    public NotImplementedException(Throwable t) {
        super(t);
    }

    /**
     * Create a new NotImplementedException with the string description and Throwable root cause.
     * 
     * @param message the String describing the exception.
     * @param t the Throwable root cause.
     */
    public NotImplementedException(String message, Throwable t) {
        super(message, t);
    }

}
