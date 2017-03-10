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
package com.ibm.ws.kernel.security.thread;

/**
 *
 */
public class ThreadIdentityException extends Exception {

    /**
     * @param e
     */
    public ThreadIdentityException(Exception e) {
        super(e);
    }

    /**  */
    private static final long serialVersionUID = -5708519766664441499L;
}
