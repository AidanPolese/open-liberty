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
 * Initialization exception specifying that the initialization failed because an expected configuration
 * property was not found.
 */
public class MissingInitPropertyException extends InitializationException {

    private static final long serialVersionUID = 9005324454219277762L;

    /**
     * Creates the Missing Initialization Property Exception
     */
    public MissingInitPropertyException() {
        super();
    }

    /**
     * Creates the Missing Initialization Property Exception
     *
     * @param message The message or message key of the exception.
     */
    public MissingInitPropertyException(String key, String message) {
        super(key, message);
    }

    /**
     * @param cause
     */
    public MissingInitPropertyException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public MissingInitPropertyException(String key, String message, Throwable cause) {
        super(key, message, cause);
    }

}
