/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.metatype;

/**
 *
 */
public enum OutputVersion {
    v1("1"), v2("2");

    private String value;

    private OutputVersion(String val) {
        value = val;
    }

    @Override
    public String toString() {
        return value;
    }

    public static OutputVersion getEnum(String value) {
        if (value == null || value.length() == 0) {
            return v1; //default to v1 if not specified
        }

        for (OutputVersion v : values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }

        throw new IllegalArgumentException(value);
    }
}