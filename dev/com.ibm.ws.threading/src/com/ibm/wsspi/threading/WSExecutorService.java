/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * An extension of <tt>ExecutorService</tt> with additional methods that provide
 * more control over how submitted and executed work is handled.
 */
public interface WSExecutorService extends ExecutorService {

    /**
     * Executes the given command at some time in the future. Although the
     * command may execute in a new thread, in a pooled thread, or in the
     * calling thread, there is no bias towards executing the command in
     * the calling thread.
     * 
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     *             accepted for execution.
     * @throws NullPointerException if command is null
     */
    public void executeGlobal(Runnable command) throws RejectedExecutionException;
}
