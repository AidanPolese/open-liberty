/*
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.2 SERV1/ws/code/security.crypto/src/com/ibm/websphere/ssl/SSLException.java, WAS.security.crypto, WASX.SERV1, pp0919.25 1/4/06 09:56:37 [5/15/09 18:04:31]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 *
 */

package com.ibm.websphere.ssl;

/**
 * <p>
 * This is a generic exception thrown for most SSL-related errors.
 * </p>
 * 
 * @author IBM Corporation
 * @version 1.0
 * @since WAS 6.1
 * @see com.ibm.websphere.ssl.JSSEHelper
 * @ibm-api
 **/
public class SSLException extends Exception {
    private static final long serialVersionUID = -3236620232328367856L;

    /**
     * Constructor.
     */
    public SSLException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public SSLException(Exception cause) {
        super(cause);
    }

    /**
     * Constructor.
     * 
     * @param message
     */
    public SSLException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message
     * @param cause
     */
    public SSLException(String message, Exception cause) {
        super(message, cause);
    }

}
