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
package com.ibm.websphere.concurrent.persistent;

import com.ibm.ws.concurrent.persistent.internal.InvokerTask;

/**
 * Obtains the task ID of the task that is currently running on the thread, if any.
 */
public class TaskIdAccessor {
    /**
     * Returns the task ID of the task that is currently running on the thread. Otherwise null.
     * 
     * @return the task ID of the task that is currently running on the thread. Otherwise null.
     */
    public static final Long get() {
        return InvokerTask.taskIdsOfRunningTasks.get();
    }
}
