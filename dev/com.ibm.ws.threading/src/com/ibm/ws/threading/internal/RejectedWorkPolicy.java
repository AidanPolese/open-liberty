/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.threading.internal;

/**
 * The policy that determines what should happen when an executor
 * is unable to queue a piece of work for execution.
 */
public enum RejectedWorkPolicy {

    /**
     * Discard the work and raise a <code>RejectedExecutionException</code>.
     */
    ABORT,

    /**
     * Execute the work immediately on the caller's thread.
     */
    CALLER_RUNS
}
