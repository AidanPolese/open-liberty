/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.internal;

/**
 * Monitors the status of a running process.
 */
public interface ProcessStatus {
    /**
     * Returns true if the process is possibly running.
     */
    boolean isPossiblyRunning();
}
