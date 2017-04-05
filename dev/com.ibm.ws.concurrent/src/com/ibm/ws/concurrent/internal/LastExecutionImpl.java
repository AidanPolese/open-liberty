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
package com.ibm.ws.concurrent.internal;

import java.util.Date;

import javax.enterprise.concurrent.LastExecution;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Information about a single execution of a task.
 */
@Trivial
public class LastExecutionImpl implements LastExecution {
    private final long endTime;
    private final String identityName;
    private final Object result;
    private final long scheduledStartTime;
    private final long startTime;

    /**
     * Construct a record of task execution.
     * 
     * @param identityName identity name for the task, if any.
     * @param scheduledStartTime the time scheduled for the task to start executing.
     * @param startTime time when the task actually started executing.
     * @param endTime time when the task completed executing.
     * @param result result of the last execution of the task as of the time when this LastExecution instance was created.
     */
    LastExecutionImpl(String identityName, long scheduledStartTime, long startTime, long endTime, Object result) {
        this.identityName = identityName;
        this.scheduledStartTime = scheduledStartTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }

    /**
     * @see javax.enterprise.concurrent.LastExecution#getIdentityName()
     */
    @Override
    public final String getIdentityName() {
        return identityName;
    }

    /**
     * @see javax.enterprise.concurrent.LastExecution#getResult()
     */
    @Override
    public final Object getResult() {
        return result;
    }

    /**
     * @see javax.enterprise.concurrent.LastExecution#getRunEnd()
     */
    @Override
    public final Date getRunEnd() {
        return new Date(endTime);
    }

    /**
     * @see javax.enterprise.concurrent.LastExecution#getRunStart()
     */
    @Override
    public final Date getRunStart() {
        return new Date(startTime);
    }

    /**
     * @see javax.enterprise.concurrent.LastExecution#getScheduledStart()
     */
    @Override
    public final Date getScheduledStart() {
        return new Date(scheduledStartTime);
    }

    /**
     * Returns a textual representation suitable for display in trace.
     * 
     * @return a textual representation suitable for display in trace.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append(getClass().getSimpleName()).append(' ').append(identityName).append(' ')
                        .append("scheduledStart=").append(Utils.toString(new Date(scheduledStartTime))).append(' ')
                        .append("runStart=").append(Utils.toString(new Date(startTime))).append(' ')
                        .append("runEnd=").append(Utils.toString(new Date(endTime))).append(' ')
                        .append(result);
        return sb.toString();
    }
}
