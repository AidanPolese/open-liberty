/*
 * 
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * @(#) 1.2 SERV1/ws/code/security.crypto/src/com/ibm/wsspi/ssl/WSPKIException.java, WAS.security.crypto, WASX.SERV1, pp0919.25 11/15/06 17:05:20 [5/15/09 17:37:01]
 * 
 * Defect       Date	    CMVC ID	  	Description
 *
 * LIDB2112-23 11/10/14    alaine	        Initial code drop
 */

package com.ibm.wsspi.ssl;

/**
 * @author IBM Corporation
 * @ibm-spi
 */
public class WSPKIException extends Exception {

    private static final long serialVersionUID = -1572688748868920424L;

    /**
     * Create a new WSPKIException with an empty description string.
     */
    public WSPKIException() {
        super();
    }

    /**
     * Create a new WSPKIException with the associated string description.
     * 
     * @param message
     *            String describing the exception.
     */
    public WSPKIException(String message) {
        super(message);
    }

    /**
     * Create a new WSPKIException with the associated string description and
     * cause.
     * 
     * @param message
     *            the String describing the exception.
     * @param cause
     */
    public WSPKIException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new WSPKIException with the cause
     * 
     * @param cause
     *            the throwable cause of the exception.
     */
    public WSPKIException(Throwable cause) {
        super(cause);
    }
}
