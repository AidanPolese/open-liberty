//  @(#) 1.6 SERV1/ws/code/security.sas/src/com/ibm/websphere/security/CertificateMapNotSupportedException.java, WAS.security.sas, WASX.SERV1, nn1148.03 2/18/05 16:37:06 [12/4/11 15:43:06]
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// All Rights Reserved * Licensed Materials - Property of IBM

package com.ibm.websphere.security;

/**
 * Thrown to indicate that the certificate mapping for the
 * specified certificate is not supported.
 * 
 * @ibm-spi
 */

public class CertificateMapNotSupportedException extends WSSecurityException {

    private static final long serialVersionUID = -2990393675191692512L; //@vj1: Take versioning into account if incompatible changes are made to this class

    /**
     * Create a new CertificateMapNotSupportedException with an empty description string.
     */
    public CertificateMapNotSupportedException() {
        super();
    }

    /**
     * Create a new CertificateMapNotSupportedException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public CertificateMapNotSupportedException(String message) {
        super(message);
    }

    /**
     * Create a new CertificateMapNotSupportedException with the Throwable root cause.
     * 
     * @param t the Throwable root cause.
     */
    public CertificateMapNotSupportedException(Throwable t) {
        super(t);
    }

    /**
     * Create a new CertificateMapNotSupportedException with the string description and Throwable root cause.
     * 
     * @param message the String describing the exception.
     * @param t the Throwable root cause.
     */
    public CertificateMapNotSupportedException(String message, Throwable t) {
        super(message, t);
    }

}
