//  @(#) 1.6 SERV1/ws/code/security.sas/src/com/ibm/websphere/security/CertificateMapFailedException.java, WAS.security.sas, WASX.SERV1, nn1148.03 2/18/05 16:37:04 [12/4/11 15:43:06]
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// All Rights Reserved * Licensed Materials - Property of IBM

package com.ibm.websphere.security;

/**
 * Thrown to indicate that a error occurred while mapping the
 * specified certificate.
 * 
 * @ibm-spi
 */

public class CertificateMapFailedException extends WSSecurityException {

    private static final long serialVersionUID = -2745350089461368647L; //@vijaylax: Take versioning into account if incompatible changes are made to this class

    /**
     * Create a new CertificateMapFailedException with an empty description string.
     */
    public CertificateMapFailedException() {
        super();
    }

    /**
     * Create a new CertificateMapFailedException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public CertificateMapFailedException(String message) {
        super(message);
    }

    /**
     * Create a new CertificateMapFailedException with the Throwable root cause.
     * 
     * @param t the Throwable root cause.
     */
    public CertificateMapFailedException(Throwable t) {
        super(t);
    }

    /**
     * Create a new CertificateMapFailedException with the string description and Throwable root cause.
     * 
     * @param message the String describing the exception.
     * @param t the Throwable root cause.
     */
    public CertificateMapFailedException(String message, Throwable t) {
        super(message, t);
    }

}
