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
public enum SchemaVersion {
    v1_0("1.0"), v1_1("1.1");

    private String value;

    private SchemaVersion(String val) {
        value = val;
    }

    @Override
    public String toString() {
        return value;
    }

    public static SchemaVersion getEnum(String value) {
        if (value == null || value.length() == 0) {
            return v1_0; //default to v1 if not specified
        }

        for (SchemaVersion v : values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }

        throw new IllegalArgumentException(value);
    }
}
