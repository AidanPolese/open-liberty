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
 * A generic vmm application exception to indicate to the caller
 * that there was a problem with the current request due to incorrect inputs
 * from the caller.
 **/
public class WIMApplicationException extends WIMException {

    /**
     * Creates the vmm application exception.
     */
    public WIMApplicationException() {
        super();
    }

    /**
     * Creates the WIMApplicationException.
     * 
     * @param message The message or message key of the exception.
     **/
    public WIMApplicationException(String key, String message) {
        super(key, message);
    }

    /**
     * Creates the WIMApplicationException.
     * 
     * @param cause The cause of the exception.
     **/
    public WIMApplicationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates the WIMApplicationException.
     * 
     * @param cause The cause of the exception.
     **/
    public WIMApplicationException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }

}