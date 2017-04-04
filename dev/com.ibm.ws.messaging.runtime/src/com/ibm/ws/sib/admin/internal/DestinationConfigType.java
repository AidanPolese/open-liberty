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
 * 175637.5.2      150304 philip   Original
 * ============================================================================
 */

package com.ibm.ws.sib.admin.internal;

/**
 * This class is a "Java typesafe enum", the values of which represent different
 * types of configured destination.
 * 
 * @author philip
 */
public class DestinationConfigType {

    public final static DestinationConfigType LOCAL = new DestinationConfigType("Local", 0);

    public final static DestinationConfigType ALIAS = new DestinationConfigType("Alias", 1);

    public final static DestinationConfigType FOREIGN = new DestinationConfigType("Foreign", 2);

    private final String name;
    private final int value;
    private final static DestinationConfigType[] set = { LOCAL, ALIAS, FOREIGN };

    /**
     * @param name
     * @param value
     */
    private DestinationConfigType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return name;
    }

    /**
     * Returns an integer value representing the instance of this class
     * 
     * @return
     */
    public final int toInt() {
        return value;
    }

    /**
     * Get the DestinationConfigType represented by the given integer value;
     * 
     * @param value the integer representation of the required DestinationType
     * @return the DestinationType represented by the given integer value
     */
    public final static DestinationConfigType getDestinationType(int value) {
        return set[value];
    }
}
