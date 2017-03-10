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
package com.ibm.ws.threading.listeners;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface CompletionListener<T> {
    public void successfulCompletion(Future<T> future, T result);

    /**
     * The implementor is expected to FFDC if necessary for the
     * supplied {@link Throwable}, the threading utilities will not FFDC
     * for the enclosing {@link ExecutionException}.
     * 
     * @param t
     */
    public void failedCompletion(Future<T> future, Throwable t);
}