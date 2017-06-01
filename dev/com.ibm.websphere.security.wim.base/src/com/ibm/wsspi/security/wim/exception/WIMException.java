/************** Begin Copyright - Do not add comments here **************
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
 * Base class representing a virtual member manager exception. This can be extended to create
 * component specific exceptions.
 *
 **/
public class WIMException extends Exception {

    private static final long serialVersionUID = 2213794407328217976L;

    private String messageKey = null;

    /**
     * Default Constructor
     **/
    public WIMException() {
        super();
    }

    /**
     * Creates the WIMException.
     *
     * @param message The message or message key of the exception.
     **/
    public WIMException(String key, String message) {
        super(message);
        messageKey = key;
    }

    public WIMException(String message) {
        super(message);
    }

    /**
     * Creates the WIMException.
     *
     * @param cause The cause of the exception.
     **/
    public WIMException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates the WIMException.
     *
     * @param message The error message.
     * @param cause The cause of the exception.
     **/
    public WIMException(String key, String message, Throwable cause) {
        super(message, cause);
        messageKey = key;
    }

    /**
     * Return the message key.
     **/
    public String getMessageKey() {
        return messageKey;
    }
}