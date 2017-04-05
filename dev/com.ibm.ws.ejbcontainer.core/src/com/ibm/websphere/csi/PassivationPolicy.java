/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000, 2001
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 *  <code>PassivationPolicy</code> defines legal values for when stateful
 *  session beans may be passivated. <p>
 */

package com.ibm.websphere.csi;

public class PassivationPolicy {

    public static final PassivationPolicy AT_COMMIT =
                    new PassivationPolicy(0, "AT_COMMIT");

    public static final PassivationPolicy ON_CACHE_FULL =
                    new PassivationPolicy(1, "ON_CACHE_FULL");

    public static final PassivationPolicy ON_DEMAND =
                    new PassivationPolicy(2, "ON_DEMAND");

    /**
     * Construct new <code>PassivationPolicy</code> instance with
     * the given unique value. <p>
     */

    private PassivationPolicy(int value, String s) {

        this.value = value;
        this.name = s;
    }

    /**
     * Return unique value for this <code>PassivationPolicy</code>. <p>
     */

    public final int getValue() {
        return value;
    }

    /**
     * Return string representation of this
     * <code>PassivationPolicy</code>. <p>
     */

    public String toString() {
        return name;
    }

    /**
     * Unique value for each legal <code>PassivationPolicy</code> for
     * fast lookups.
     */

    private int value;
    private String name;

}
