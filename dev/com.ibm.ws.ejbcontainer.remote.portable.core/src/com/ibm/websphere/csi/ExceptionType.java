/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 *  <code>ExceptionType</code> defines legal values for the 
 *  exception type attribute passed to <code>TransactionControl</code>. <p>
 */

package com.ibm.websphere.csi;

public class ExceptionType {

    public static final ExceptionType NO_EXCEPTION =
                    new ExceptionType(0, "NO_EXCEPTION");

    public static final ExceptionType CHECKED_EXCEPTION =
                    new ExceptionType(1, "CHECKED_EXCEPTION");

    public static final ExceptionType UNCHECKED_EXCEPTION =
                    new ExceptionType(2, "UNCHECKED_EXCEPTION");

    /**
     * Construct new <code>ExceptionType</code> instance with
     * the given unique value. <p>
     */

    private ExceptionType(int value, String s) {

        this.value = value;
        this.name = s;
    }

    /**
     * Return unique value for this <code>ExceptionType</code>. <p>
     */

    public int getValue() {
        return value;
    }

    /**
     * Return string representation of this
     * <code>ExceptionType</code>. <p>
     */

    public String toString() {
        return name;
    }

    /**
     * Unique value for each legal <code>ExceptionType</code> for
     * fast lookups.
     */

    private int value;
    private String name;
}
