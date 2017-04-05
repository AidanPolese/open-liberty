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
 * Type-safe enumeration for durable subscription sharing.
 */
public final class SibRaDurableSubscriptionSharing {

    
    /**
     * Always permit sharing.
     */
    public static final SibRaDurableSubscriptionSharing ALWAYS = new SibRaDurableSubscriptionSharing("ALWAYS");

    /**
     * Never permit sharing.
     */
    public static final SibRaDurableSubscriptionSharing NEVER = new SibRaDurableSubscriptionSharing("NEVER");

    /**
     * Only permit sharing in a cluster.
     */
    public static final SibRaDurableSubscriptionSharing CLUSTER_ONLY = new SibRaDurableSubscriptionSharing("CLUSTER_ONLY");

    /**
     * String representation of the shareability.
     */
    private final String _name;
    
    /**
     * Private constructor to prevent instantiation.
     * 
     * @param name a string representation of the shareability
     */
    private SibRaDurableSubscriptionSharing(final String name) {
        
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
