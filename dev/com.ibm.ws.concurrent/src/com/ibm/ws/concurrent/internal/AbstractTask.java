/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013,2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.concurrent.internal;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedTaskListener;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.threadcontext.ThreadContextDescriptor;

/**
 * Abstract task is the common implementation between SubmittedTask and ScheduledTask
 */
abstract class AbstractTask<T> implements Callable<T>, ManagedTask {
    /**
     * Execution properties that specify to suspend the current transaction.
     */
    static final Map<String, String> XPROPS_SUSPEND_TRAN = Collections.singletonMap(ManagedTask.TRANSACTION, ManagedTask.SUSPEND);

    /**
     * Managed task listener. Null if there isn't one.
     */
    final ManagedTaskListener listener;

    /**
     * The task.
     */
    final Object task;

    /**
     * Previously captured thread context with which the task should run.
     */
    ThreadContextDescriptor threadContextDescriptor;

    /**
     * Constructor
     * 
     * @param task the task
     */
    @Trivial
    AbstractTask(Object task) {
        this.listener = task instanceof ManagedTask ? ((ManagedTask) task).getManagedTaskListener() : null;
        this.task = task;
    }

    /**
     * @see javax.enterprise.concurrent.ManagedTask#getExecutionProperties()
     */
    @Override
    public final Map<String, String> getExecutionProperties() {
        return threadContextDescriptor.getExecutionProperties();
    }

    /**
     * @see javax.enterprise.concurrent.ManagedTask#getManagedTaskListener()
     */
    @Override
    @Trivial
    public final ManagedTaskListener getManagedTaskListener() {
        return listener;
    }

    /**
     * Returns the task name.
     *
     * @return the task name.
     */
    @Trivial
    final String getName() {
        Map<String, String> execProps = getExecutionProperties();
        String taskName = execProps == null ? null : execProps.get(ManagedTask.IDENTITY_NAME);
        return taskName == null ? task.toString() : taskName;
    }
}
