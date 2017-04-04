/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2007, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.j2c;

/**
 * ConnectionEvent extends the javax.resource.spi.ConnectionEvent to add additional constants
 * for connection event IDs.
 * 
 * @ibm-spi
 */
public class ConnectionEvent extends javax.resource.spi.ConnectionEvent {

    static final long serialVersionUID = 7709055559014574730L;

    private ConnectionEvent() {
        super(null, 0);
    }

    /**
     * Constant to indicate that only the connection the event was fired on
     * is to be destroyed, regardless of the purge policy.
     */
    public static final int SINGLE_CONNECTION_ERROR_OCCURRED = 51;

    /**
     * Constant to indicate that no message should be logged for this error, as it
     * was initiated by the application or by JMS
     */
    public static final int CONNECTION_ERROR_OCCURRED_NO_EVENT = 52;

}
