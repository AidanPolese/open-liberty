/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.internal.monitor;

public enum UpdateTrigger {
    POLLED, MBEAN, DISABLED;

    /**
     * @param val
     * @return
     */
    public static UpdateTrigger get(String val) {
        if (DISABLED.toString().equalsIgnoreCase(val)) {
            return DISABLED;
        } else if (MBEAN.toString().equalsIgnoreCase(val)) {
            return MBEAN;
        }
        return POLLED;
    }
}