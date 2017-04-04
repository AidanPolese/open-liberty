/*
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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 187670.1        060504 philip   Original implementation
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

public final class QualifiedDestinationName {

    private String _bus = null;
    private String _destination = null;

    public QualifiedDestinationName(String bus, String destination) {
        _bus = bus;
        _destination = destination;
    }

    public String getBus() {
        return _bus;
    }

    public String getDestination() {
        return _destination;
    }
}
