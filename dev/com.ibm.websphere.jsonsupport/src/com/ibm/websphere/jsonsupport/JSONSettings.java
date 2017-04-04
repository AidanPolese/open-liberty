/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.jsonsupport;

/**
 *
 */
public class JSONSettings {
    public enum Include {
        ALWAYS,
        NON_NULL,
    }

    private Include inclusion;

    public JSONSettings() {
        inclusion = Include.ALWAYS;
    }

    public JSONSettings(Include inclusion) {
        this.inclusion = inclusion;
    }

    public Include getInclusion() {
        return this.inclusion;
    }

    public void setInclusion(Include inclusion) {
        this.inclusion = inclusion;
    }
}
