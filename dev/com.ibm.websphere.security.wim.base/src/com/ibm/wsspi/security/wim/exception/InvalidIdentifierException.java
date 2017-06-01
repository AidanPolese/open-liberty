/************** Begin Copyright - Do not add comments here **************
*
 *
 * IBM Confidential OCO Source Material
 * Virtual Member Manager (C) COPYRIGHT International Business Machines Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.wsspi.security.wim.exception;

public class InvalidIdentifierException extends WIMApplicationException {

    private static final long serialVersionUID = 1435755975795200999L;

    /**
     *
     */
    public InvalidIdentifierException() {
        super();
    }

    /**
     * @param message
     */
    public InvalidIdentifierException(String key, String message) {
        super(key, message);
    }

    /**
     * @param cause
     */
    public InvalidIdentifierException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidIdentifierException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }

}
