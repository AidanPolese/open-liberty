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
 *                 18-May-04 dcurrie  Original
 * ============================================================================
 */

package com.ibm.ws.sib.ra.inbound;

/**
 * Type-safe enumeration for deletion modes.
 */
public final class SibRaMessageDeletionMode {

    
    /**
     * Deletion mode where the resource adapter deletes each message
     * individually after delivery.
     */
    public static final SibRaMessageDeletionMode SINGLE = new SibRaMessageDeletionMode("SINGLE");

    /**
     * Deletion mode where the resource adapter may deliver a batch of messages
     * and then delete them all together. This may represent a performance
     * improvement but increases the window during which the server may crash
     * and a message may be redelivered.
     */
    public static final SibRaMessageDeletionMode BATCH = new SibRaMessageDeletionMode("BATCH");

    /**
     * Deletion mode where the resource adapter does not delete the messages
     * after delivery instead leaving this to the application.
     */
    public static final SibRaMessageDeletionMode APPLICATION = new SibRaMessageDeletionMode("APPLICATION");

    /**
     * String representation of the message deletion mode.
     */
    private final String _name;
    
    /**
     * Private constructor to prevent instantiation.
     * 
     * @param name a string representation of the mode
     */
    private SibRaMessageDeletionMode(final String name) {
        
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
