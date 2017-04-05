//  @(#) 1.6 SERV1/ws/code/security.sas/src/com/ibm/websphere/security/CustomRegistryException.java, WAS.security.sas, WASX.SERV1, nn1148.03 2/18/05 16:41:20 [12/4/11 15:43:06]
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// All Rights Reserved * Licensed Materials - Property of IBM

package com.ibm.websphere.security;

/**
 * Thrown to indicate that a error occurred while using the
 * specified custom registry.
 * 
 * @ibm-spi
 */

public class CustomRegistryException extends WSSecurityException {

    private static final long serialVersionUID = -7806436129183927093L; //@vj1: Take versioning into account if incompatible changes are made to this class

    /**
     * Create a new CustomRegistryException with an empty description string.
     */
    public CustomRegistryException() {
        super();
    }

    /**
     * Create a new CustomRegistryException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public CustomRegistryException(String message) {
        super(message);
    }

    /**
     * Create a new CustomRegistryException with the associated Throwable root cause.
     * 
     * @param t the Throwable root cause.
     */
    public CustomRegistryException(Throwable t) {
        super(t);
    }

    /**
     * Create a new CustomRegistryException with the string description and Throwable root cause.
     * 
     * @param message the String describing the exception.
     * @param t the Throwable root cause.
     */
    public CustomRegistryException(String message, Throwable t) {
        super(message, t);
    }

}
