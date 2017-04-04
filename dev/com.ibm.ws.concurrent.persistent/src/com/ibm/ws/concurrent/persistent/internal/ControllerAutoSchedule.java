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
package com.ibm.ws.concurrent.persistent.internal;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.concurrent.persistent.controller.Controller;

/**
 * Automatically schedules a task via the controller upon successful transaction commit.
 */
class ControllerAutoSchedule implements Synchronization {
    private final short binaryFlags;
    private final Controller controller;
    private final long expectedExecTime;
    private final long partitionId;
    private final long taskId;
    private final int txTimeout;

    /**
     * Construct a new instance.
     */
    ControllerAutoSchedule(Controller controller, long partitionId, long taskId, long expectedExecTime, short binaryFlags, int txTimeout) {
        this.binaryFlags = binaryFlags;
        this.controller = controller;
        this.expectedExecTime = expectedExecTime;
        this.partitionId = partitionId;
        this.taskId = taskId;
        this.txTimeout = txTimeout;
    }

    /**
     * Upon successful transaction commit, automatically schedules a task via the controller.
     * 
     * @see javax.transaction.Synchronization#afterCompletion(int)
     */
    @Override
    public void afterCompletion(int status) {
        if (status == Status.STATUS_COMMITTED)
            controller.notifyOfTaskAssignment(partitionId, taskId, expectedExecTime, binaryFlags, txTimeout);
    }

    /**
     * @see javax.transaction.Synchronization#beforeCompletion()
     */
    @Override
    @Trivial
    public void beforeCompletion() {}
}
