/*
 * IBM Confidential OCO Source Material
 * Copyright IBM Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason    Version  Date        User id    Description
 * ----------------------------------------------------------------------------
 * D110679     4.0.2  09-10-2001  jhanders   Part created.
 * LIDB799.100 5.0    10-10-2001  jhanders   Changed permissions.
 * LIDB799.100 5.0    10-11-2001  stopyro    Use WsException
 *
 */

package com.ibm.ejs.ras;

/**
 * This class is used for reporting trace string parsing errors.
 * It extends from the exception class RasException as should all
 * Ras related exceptions.
 */
public class MalformedTraceStringException extends RasException {

    private static final long serialVersionUID = -4722157347311978259L;

    /**
     * Default constructor.
     */
    MalformedTraceStringException() {
        super();
    }

    /**
     * Constructor that takes a Throwable to be chained.
     * 
     * @param throwable The Throwable to be chained
     */
    MalformedTraceStringException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor that takes a String message.
     * 
     * @param message caller-specified text
     */
    MalformedTraceStringException(String message) {
        super(message);
    }

    /**
     * Constructor that takes a String message and a Throwable to chain
     * 
     * @param message caller-specified text
     * @param throwable The Throwable that is to be chained.
     */
    MalformedTraceStringException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
