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
 * --------------- --------- -------- ---------------------------------------
 * 231508          17-Sep-04 dcurrie  Original
 * ============================================================================
 */

package com.ibm.ws.sib.ra.inbound;

/**
 * Type-safe enumeration for read ahead optimization.
 */
public final class SibRaReadAhead {

    /**
     * Always perform read ahead.
     */
    public static final SibRaReadAhead ON = new SibRaReadAhead("ON");

    /**
     * Never perform read ahead.
     */
    public static final SibRaReadAhead OFF = new SibRaReadAhead("OFF");

    /**
     * Only perform read ahead for non-durable subscriptions and unshared
     * durable subscriptions.
     */
    public static final SibRaReadAhead DEFAULT = new SibRaReadAhead("DEFAULT");

    /**
     * String representation of the shareability.
     */
    private final String _name;

    /**
     * Private constructor to prevent instantiation.
     * 
     * @param name
     *            a string representation of the shareability
     */
    private SibRaReadAhead(final String name) {

        _name = name;

    }

    /**
     * Returns a string representation of this object.
     * 
     * @return a string representation of this object
     */
    public String toString() {

        return _name;

    }

}
