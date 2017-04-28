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
 *
 * vmm application exception to indicate that validation of the
 * the request failed.
 */
public class ValidationFailException extends WIMApplicationException {

    /**
     * Creates the Validation Fail Exception
     */
    public ValidationFailException() {
        super();
    }

    /**
     * Creates the Validation Fail Exception
     * 
     * @param message The message or message key of the exception.
     */
    public ValidationFailException(String key, String message) {
        super(key, message);
    }

    /**
     * Creates the Validation Fail Exception
     * 
     * @param message The message or message key of the exception.
     * @param cause The cause of the exception.
     */
    public ValidationFailException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }

    /**
     * Creates the Validation Fail Exception
     * 
     * @param cause The cause of the exception.
     */
    public ValidationFailException(Throwable cause) {
        super(cause);
    }
}