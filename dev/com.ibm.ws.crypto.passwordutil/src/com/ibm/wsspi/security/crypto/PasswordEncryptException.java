/*
 * COMPONENT_NAME:  WAS.orbext
 * @(#) 1.1 SERV1/ws/code/security.crypto/src/com/ibm/wsspi/security/crypto/PasswordEncryptException.java, WAS.security.crypto, WASX.SERV1, pp0919.25 5/12/05 16:30:58 [5/15/09 18:04:39]
 *
 * ORIGINS: 27
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION: Interface that defines how a customer implements password encryption.
 *
 *             
 * Change History:
 * 
 *  Date       Programmer       Defect        Description
 *  --------   ---------        ------        -------------------------------------
 *  05/12/05   pbirk            fLIDB4135.2   Initial drop of the interface.
 */

package com.ibm.wsspi.security.crypto;

/**
 * Exception thrown when an error occurred while encrypting the data.
 * @ibm-spi
 */
public class PasswordEncryptException extends Exception {
    private static final long serialVersionUID = 2510989550436833115L;

    /**
     * Constructs an PasswordEncryptException with no message.
     */
    public PasswordEncryptException() {
        super();
    }

    /**
     * Constructs an PasswordEncryptException with the specified message.
     * 
     * @param message the detail message.
     */
    public PasswordEncryptException(String message) {
        super(message);
    }

    /**
     * Constructs an PasswordEncryptException with the specified cause.
     * 
     * @param cause the cause.
     */
    public PasswordEncryptException(Exception cause) {
        super(cause);
    }

    /**
     * Constructs an PasswordEncryptException with the specified message and cause.
     * 
     * @param message the detail message.
     * @param cause the cause.
     */
    public PasswordEncryptException(String message, Exception cause) {
        super(message, cause);
    }
}
