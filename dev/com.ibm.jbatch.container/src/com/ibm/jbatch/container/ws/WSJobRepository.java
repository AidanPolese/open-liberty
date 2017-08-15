/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.jbatch.container.ws;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.batch.operations.JobOperator;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.BatchStatus;

import com.ibm.jbatch.container.exception.ExecutionAssignedToServerException;
import com.ibm.jbatch.container.services.IJPAQueryHelper;

/**
 * This interface is a relatively thin layer around the persistence service.
 *
 * However, that interface has exploded with so many methods it's hard to get a
 * handle on, so the idea is that we have this WSJobRepository interface as a much-more
 * restricted window into the persistence service.
 *
 * In exporting this interface, but not the IPersistenceManagerService interface, we have
 * a simpler view of what the interactions from the WAS-specific remote layer really are.
 *
 * Note there is some overlap here between this class and the read-only, view methods of
 * JobOperator. It is expected that WAS-specific code will use this class rather than
 * JobOperator as its interface into accessing job execution data in the persistent store.
 *
 * Note the presence of <code>isTopLevelJobInstance</code> and <code>isTopLevelJobExecution</code>
 * methods. The reason this approach was chosen as an alternative to shutting off non-top-level ids from
 * all users of this class is that we lack methods for viewing "subjob" (i.e. partition & split-flow)
 * execution data. Since this might be useful, we leave the door open to individual methods to
 * decide whether and how to expose the subjob data to its callers.
 *
 * @see JobOperator
 */
public interface WSJobRepository {

    /**
     * Get job instance associated with the job execution identified by <code>executionId</code>
     *
     * @param executionId
     * @return associated JobInstance
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    public abstract WSJobInstance getJobInstanceFromExecution(long executionId) throws NoSuchJobExecutionException, JobSecurityException;

    /**
     * Get job instance identified by <code>instanceId</code>
     *
     * @param instanceId
     * @return associated JobInstance
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    public abstract WSJobInstance getJobInstance(long instanceId) throws NoSuchJobExecutionException, JobSecurityException;

    /**
     * Returns full list of JobExecution(s) associated with this job instance.
     * The list order is from most-recent execution to least-recent.
     * The repository keeps its own order and does not depend on execution id or creation time to order these.
     *
     * @param instanceId
     * @return full list of JobExecution(s) associated with this job instance, from most-recent to least-recent execution
     * @throws NoSuchJobInstanceException
     * @throws JobSecurityException
     */
    public abstract List<WSJobExecution> getJobExecutionsFromInstance(long instanceId) throws NoSuchJobInstanceException, JobSecurityException;

    public abstract String getBatchAppNameFromExecution(long executionId) throws NoSuchJobExecutionException, JobSecurityException;

    public abstract String getBatchAppNameFromInstance(long instanceId) throws NoSuchJobInstanceException, JobSecurityException;

    /**
     * This is "most recent" in terms of execution id, not any kind of timestamp.
     *
     * @param instanceId
     * @return the JobExecution with the highest execution id, associated with the job instance
     *         identified by <code>instanceId</code>
     * @throws NoSuchJobInstanceException
     * @throws JobSecurityException
     */
    public abstract WSJobExecution getMostRecentJobExecutionFromInstance(long instanceId) throws NoSuchJobInstanceException, JobSecurityException;

    public abstract WSJobExecution getJobExecution(long executionId) throws NoSuchJobExecutionException, JobSecurityException;

    /**
     * Creates new job execution.
     *
     * @param jobInstanceId
     * @param jobParameters
     * @param create time
     * @return jobExecution
     */
    public abstract WSJobExecution createJobExecution(long jobInstanceId, Properties jobParameters);

    /**
     * Note list order is not defined
     *
     * @param jobExecutionId
     * @return list of step execution aggregates associated with job execution
     *         identified by <code>jobExecutionId</code>. The list is ordered by start time, earliest to latest,
     *         of the top-level {@code StepExecution} of each {@code WSStepThreadExecutionAggregate}
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    public abstract List<WSStepThreadExecutionAggregate> getStepExecutionAggregatesFromJobExecution(long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;

    /**
     * @param jobExecutionId
     * @param stepName
     * @return The aggregate
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    public abstract WSStepThreadExecutionAggregate getStepExecutionAggregateFromJobExecution(long jobExecutionId,
                                                                                             String stepName) throws NoSuchJobExecutionException, JobSecurityException;

    public abstract WSStepThreadExecutionAggregate getStepExecutionAggregate(long topLevelStepExecutionId) throws IllegalArgumentException, JobSecurityException;

    /**
     * @param page the page of rows to get (starts at 0)
     * @param pageSize the number of rows in a page
     *
     * @return the list of job instances (top-level job instances only)
     */
    public abstract List<WSJobInstance> getJobInstances(int page, int pageSize);

//	public abstract void updateJobExecutionLogDir(long workUnitInternalExecutionId, String logDirPath);

    /**
     * Returns top-level job names only, not internal names associated with "subjobs".
     *
     * @return full set of top-level job names (note: not JSL names).
     */
    public abstract Set<String> getJobNames();

    /**
     * Returns true if the job instance is in a purgeable state (not STARTING, STARTED or STOPPING).
     *
     * @param jobInstanceId
     * @return
     */
    public abstract boolean isJobInstancePurgeable(long jobInstanceId) throws NoSuchJobInstanceException, JobSecurityException;

    /**
     * Update the instanceState of this job instance
     *
     * @param instanceId
     * @param state
     */
    public abstract WSJobInstance updateJobInstanceState(long instanceId, InstanceState state);

    /**
     * Update the instanceState of this job instance if state is currently STOPPED or FAILED
     *
     * @param instanceId
     * @param state
     */
    public abstract WSJobInstance updateJobInstanceStateUponRestart(long instanceId, InstanceState state);

    /**
     * Update the instanceState and batch status of this job instance
     *
     * @param instanceId
     * @param state
     * @param batchStatus
     * @return
     */
    public abstract WSJobInstance updateJobInstanceWithInstanceStateAndBatchStatus(long instanceId, InstanceState state, BatchStatus batchStatus);

    /**
     * Update the instanceState and batch status of this job instance and associated execution
     *
     * @param instance
     * @param executionId
     * @param state
     * @param batchStatus
     * @return
     */
    public abstract WSJobInstance updateJobInstanceAndExecutionWithInstanceStateAndBatchStatus(long instanceId, long executionId, final InstanceState state,
                                                                                               final BatchStatus batchStatus);

    /**
     * Update the batch status of this job execution and instance
     *
     * @param executionId
     * @param batchStatus
     * @param date
     * @return
     * @throws ExecutionAssignedToServerException
     */
    WSJobExecution updateJobExecutionAndInstanceNotSetToServerYet(long jobExecutionId, Date date) throws ExecutionAssignedToServerException;

    /**
     * Update the batch status of this job execution and instance
     *
     * @param executionId
     * @param batchStatus
     * @param date
     * @return
     * @throws ExecutionAssignedToServerException
     */
    public abstract WSJobExecution updateJobExecutionAndInstanceOnStop(long executionId, Date date) throws ExecutionAssignedToServerException;

    /**
     * Update the batch status of this job execution and instance
     *
     * @param executionId
     * @param batchStatus
     * @param date
     * @return
     */
    public abstract WSJobExecution updateJobExecutionAndInstanceOnStatusChange(long executionId, BatchStatus batchStatus, Date date);

    /**
     * Gets the StepExecutionAggregate using the JobExecutionNumber and StepName
     *
     * @param jobInstanceId
     * @param jobExecNum
     * @param stepName
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    public abstract WSStepThreadExecutionAggregate getStepExecutionAggregateFromJobExecutionNumberAndStepName(
                                                                                                              long jobInstanceId, short jobExecNum,
                                                                                                              String stepName) throws NoSuchJobExecutionException, JobSecurityException;

    /**
     * Gets the JobExecution using the JobExecution Number
     *
     * @param instanceId
     * @param jobExecNum
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    public abstract WSJobExecution getJobExecutionFromJobExecNum(long instanceId, int jobExecNum) throws NoSuchJobExecutionException, JobSecurityException;

    /**
     * Gets the Job Instances with parameters extracted from the pre-purge search URL
     *
     * @param queryHelper
     * @return
     * @throws NoSuchJobExecutionException
     * @throws JobSecurityException
     */
    public abstract List<WSJobInstance> getJobInstances(IJPAQueryHelper queryHelper, int page, int pageSize) throws NoSuchJobExecutionException, JobSecurityException;

    /**
     * Creates an entry for this remote partition in the RemotablePartition table
     *
     * @param jobExecutionId the id for the top level job execution that this partition is associated with
     * @param stepName the name of the step
     * @param internalState the state of the partition that is being dispatched
     * @return
     */
    public abstract WSRemotablePartitionExecution createRemotablePartition(long jobExecutionId,
                                                                           String stepName, int partitionNumber, RemotablePartitionState internalState);

    /**
     * Updates an entry for this remote partition in the RemotablePartition table with the given internalStatus
     * If the remotable partition is not found, then it just returns
     *
     * @param jobExecutionId the id for the top level job execution that this partition is associated with
     * @param stepName the name of the step
     * @param internalState the state of the partition that is being dispatched
     * @return
     */
    public abstract WSRemotablePartitionExecution updateRemotablePartitionInternalState(long jobExecutionId,
                                                                                        String stepName, int partitionNumber, RemotablePartitionState internalState);

    /**
     * Check the version of the job execution table in the job repository.
     */
    int getJobExecutionTableVersion() throws Exception;

    /**
     * Check the version of the job instance table in the job repository.
     */
    int getJobInstanceTableVersion() throws Exception;

}
