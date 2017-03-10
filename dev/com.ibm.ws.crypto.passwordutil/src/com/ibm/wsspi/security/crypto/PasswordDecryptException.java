/*
 * COMPONENT_NAME:  WAS.orbext
 * @(#) 1.1 SERV1/ws/code/security.crypto/src/com/ibm/wsspi/security/crypto/PasswordDecryptException.java, WAS.security.crypto, WASX.SERV1, pp0919.25 5/12/05 16:30:43 [5/15/09 18:04:38]
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
 * Exception thrown when an error occurred while decrypting the data.
 * @ibm-spi
 */
public class PasswordDecryptException extends Exception {

    private static final long serialVersionUID = 7895710950547149371L;

    /**
     * Constructs an PasswordDecryptException with no detail message.
     */
    public PasswordDecryptException() {
        super();
    }

    /**
     * Constructs an PasswordDecryptException with the specified detail message.
     * 
     * @param message the detail message.
     */
    public PasswordDecryptException(String message) {
        super(message);
    }

    /**
     * Constructs an PasswordDecryptException with the specified cause.
     *
     * @param cause the cause.
     */
    public PasswordDecryptException(Exception cause) {
        super(cause);
    }

    /**
     * Constructs an PasswordDecryptException with the specified message and cause.
     * 
     * @param message the detail message.
     * @param cause the cause.
     */
    public PasswordDecryptException(String message, Exception cause) {
        super(message, cause);
    }

}
