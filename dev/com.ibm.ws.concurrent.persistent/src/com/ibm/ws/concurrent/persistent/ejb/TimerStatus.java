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
package com.ibm.ws.concurrent.persistent.ejb;

import java.io.IOException;

import com.ibm.websphere.concurrent.persistent.TaskStatus;

/**
 * Snapshot of status for a persistent EJB timer.
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public interface TimerStatus<T> extends TaskStatus<T> {
    /**
     * Returns the serializable timer task/trigger for the EJB timer task.
     * The state of the timer task/trigger is a snapshot of the point in time
     * when the <code>TimerStatus</code> instance was captured.
     * Each invocation of this method causes a new copy to be deserialized.
     * 
     * @return the task/trigger (if any) for the EJB timer task with the specified id.
     * @throws ClassNotFoundException if the class of a serialized object cannot be found.
     * @throws IOException if an error occurs during deserialization of the task/trigger.
     */
    TimerTrigger getTimer() throws ClassNotFoundException, IOException;
}
