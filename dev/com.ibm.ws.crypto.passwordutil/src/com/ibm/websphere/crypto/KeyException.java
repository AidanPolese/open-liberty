// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
// 
// @(#) 1.2 SERV1/ws/code/security.crypto/src/com/ibm/websphere/crypto/KeyException.java, WAS.security.crypto, WASX.SERV1, pp0919.25 1/4/06 09:51:13 [5/15/09 18:04:30]
//
// Date         Defect      CMVC ID    Description
//
// 06/19/05     LIDB3557    pbirk      Used for generic KeySet/KeySetGroup exceptions

package com.ibm.websphere.crypto;

/**
 * <p>
 * This is a generic exception thrown for most key management-related errors.
 * </p>
 * 
 * @author IBM Corporation
 * @version 1.0
 * @since WAS 6.1
 * @see com.ibm.websphere.ssl.JSSEHelper
 * @ibm-api
 **/
public class KeyException extends Exception {
    private static final long serialVersionUID = 7626200077347108110L;

    /**
     * Constructor.
     */
    public KeyException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param cause
     */
    public KeyException(Exception cause) {
        super(cause);
    }

    /**
     * Constructor.
     * 
     * @param message
     */
    public KeyException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message
     * @param cause
     */
    public KeyException(String message, Exception cause) {
        super(message, cause);
    }

}
