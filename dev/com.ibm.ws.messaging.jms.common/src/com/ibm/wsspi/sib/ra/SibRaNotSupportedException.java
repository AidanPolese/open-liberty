/**
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date      Origin   Description
 * --------------- ------    -------- ---------------------------------------
 * 183467          26-Jan-04 dcurrie  Original
 * 197921.10       28-Apr-04 dcurrie  Add SPI JavaDoc tags
 * 201972.6        28-Jul-04 pnickoll Update core SPI exceptions
 * 226508          28-Apr-04 dcurrie  Remove SPI Javadoc tags
 * LIDB3706-5.252  04-Feb-05 kingdon  Add serial version UID
 * ============================================================================
 */

package com.ibm.wsspi.sib.ra;

/**
 * Exception thrown when a method is invoked that is not supported by the core
 * SPI resource adapter.
 */
public class SibRaNotSupportedException extends RuntimeException {
  
    // Added at version 1.8
    private static final long serialVersionUID = -3063679180201169050L;

    /**
     * Constructor.
     */
    public SibRaNotSupportedException() {

        super();

    }

    /**
     * Constructor.
     * 
     * @param msg
     *            the exception message
     */
    public SibRaNotSupportedException(String msg) {

        super(msg);

    }

    /**
     * Constructor.
     * 
     * @param throwable
     *            the cause
     */
    public SibRaNotSupportedException(Throwable throwable) {

        super(throwable);

    }

    /**
     * Constructor.
     * 
     * @param msg
     *            the exception message
     * @param throwable
     *            the cause
     */
    public SibRaNotSupportedException(String msg, Throwable throwable) {

        super(msg, throwable);

    }

}
