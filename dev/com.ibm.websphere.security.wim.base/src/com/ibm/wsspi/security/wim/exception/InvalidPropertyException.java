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

/**
 * Member Manager application exception to indicate that the attribute specified by the caller is invalid.
 */
public class InvalidPropertyException extends WIMApplicationException {

    /**
     * Creates the Invalid Attribute Exception
     */
    public InvalidPropertyException() {
        super();
    }

    /**
     * Creates the Invalid Attribute Exception
     * 
     * @param message The message or message key of the exception.
     */
    public InvalidPropertyException(String key, String message) {
        super(key, message);
    }

    /**
     * Creates the Invalid Attribute Exception
     * 
     * @param message The message or message key of the exception.
     * @param cause The cause of the exception.
     */
    public InvalidPropertyException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }

    /**
     * Creates the Invalid Attribute Exception
     * 
     * @param cause The cause of the exception.
     */
    public InvalidPropertyException(Throwable cause) {
        super(cause);
    }
}