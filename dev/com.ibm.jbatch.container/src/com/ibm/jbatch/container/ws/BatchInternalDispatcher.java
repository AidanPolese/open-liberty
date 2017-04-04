/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.jbatch.container.ws;

import java.util.Properties;
import java.util.concurrent.Future;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;

import com.ibm.jbatch.jsl.model.Step;

/**
 * Dispatches batch jobs locally (i.e the batch app lives in the same server).
 *
 * Constructs the proper application runtime context (ComponentMetaData, ClassLoader)
 * in order to dispatch into the app.
 *
 * Methods return a Future object represented the newly started jobs or partitions
 */
public interface BatchInternalDispatcher {

    /**
     *
     * @param jobInstance
     * @param jobParameters
     *
     */
    public Future<?> start(WSJobInstance jobInstance, Properties jobParameters, long executionId) throws BatchDispatcherException;

    /**
     *
     * @param instanceId
     * @param executionId
     *
     */
    public void markInstanceExecutionFailed(long instanceId, long executionId);

    /**
     *
     * @param executionId
     *
     */
    public WSJobExecution getJobExecution(long executionId);

    /**
     *
     * @param instanceId
     *
     */
    public WSJobInstance getJobInstance(long instanceId);

    /**
     * @param instanceId the job instance to restart
     * @return Future object of the newly restarted job
     */
    public Future<?> restartInstance(long instanceId, Properties restartParameters, long executionId) throws BatchDispatcherException;

    /**
     * Start a sub-job partition.
     *
     * @return Future object of the newly restarted partition
     */
    public Future<?> startPartition(PartitionPlanConfig partitionPlanConfig, Step step, PartitionReplyQueue partitionReplyQueue) throws BatchDispatcherException;

    /**
     * @param executionId the job execution to stop
     * @throws BatchJobNotLocalException
     */
    public void stop(long executionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException, JobSecurityException, BatchDispatcherException, BatchJobNotLocalException;

}
