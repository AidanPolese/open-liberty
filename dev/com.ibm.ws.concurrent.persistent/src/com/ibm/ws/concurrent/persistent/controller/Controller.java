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
package com.ibm.ws.concurrent.persistent.controller;

/**
 * Interface for a persistent executor to talk to a controller.
 */
public interface Controller {
    /**
     * Returns the id of a partition for a persistent executor instance that is active and able to run tasks.
     * 
     * @return a partition id.
     */
    Long getActivePartitionId();

    /**
     * Notifies the controller that another persistent executor instance has been assigned a task.
     */
    void notifyOfTaskAssignment(long partitionId, long newTaskId, long expectedExecTime, short binaryFlags, int transactionTimeout);
}
