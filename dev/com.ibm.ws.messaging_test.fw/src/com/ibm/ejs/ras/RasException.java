/**
 *
 * IBM Confidential OCO Source Material
 * Copyright IBM Corp. 2012
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * DESCRIPTION:
 *
 * Change History:
 *
 * Reason    Version  Date        Userid    Description
 * ----------------------------------------------------------------------------
 * D102621     4.0    05-21-2001  stopyro   Part created.
 * LIDB799.100 5.0    10-10-2001  jhanders  Change permissions.
 * LIDB799.100 5.0    10-11-2001  stopyro   Extend WsException
 * d114018     5.0    04-01-2002  jhanders  Small performance changes.
 */

package com.ibm.ejs.ras;

/**
 * This class is the base class of all Ras exceptions. It extends from the
 * exception class com.ibm.ws.exception.WsException to enable exception chaining.
 * <p>
 * All other Ras exceptions must extend this exception.
 */
public class RasException extends com.ibm.ws.exception.WsException {

    private static final long serialVersionUID = -3174194708446299559L;

    /**
     * Default constructor.
     */
    RasException() {
        super();
    }

    /**
     * Constructor that takes a Throwable to be chained.
     * 
     * @param throwable The Throwable to be chained
     */
    RasException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor that takes a String message.
     * 
     * @param message caller-specified text
     */
    RasException(String message) {
        super(message);
    }

    /**
     * Constructor that takes a String message and a Throwable to chain
     * 
     * @param message caller-specified text
     * @param throwable The Throwable that is to be chained.
     */
    RasException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
