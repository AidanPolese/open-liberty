/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.tx.remote;

/**
 *
 */
public class TRANSIENT extends RuntimeException {

    /**
     * @param serverBusy
     * @param false1
     */
    public TRANSIENT(int minor, Boolean false1) {}

    /**
     * 
     */
    public TRANSIENT() {
        super();
    }

}
