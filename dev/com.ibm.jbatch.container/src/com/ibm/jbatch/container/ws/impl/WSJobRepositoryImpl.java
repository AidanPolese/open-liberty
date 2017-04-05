/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.jbatch.container.ws.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.BatchStatus;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.jbatch.container.persistence.jpa.JobInstanceEntity;
import com.ibm.jbatch.container.services.IPersistenceManagerService;
import com.ibm.jbatch.container.ws.InstanceState;
import com.ibm.jbatch.container.ws.RemotablePartitionState;
import com.ibm.jbatch.container.ws.WSBatchAuthService;
import com.ibm.jbatch.container.ws.WSJobExecution;
import com.ibm.jbatch.container.ws.WSJobInstance;
import com.ibm.jbatch.container.ws.WSJobRepository;
import com.ibm.jbatch.container.ws.WSRemotablePartitionExecution;
import com.ibm.jbatch.container.ws.WSSearchObject;
import com.ibm.jbatch.container.ws.WSStepThreadExecutionAggregate;
import com.ibm.jbatch.spi.BatchSecurityHelper;

/**
 * {@inheritDoc}
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE, property = { "service.vendor=IBM" })
public class WSJobRepositoryImpl implements WSJobRepository {

    private IPersistenceManagerService persistenceManagerService;

    private WSBatchAuthService authService;

    private BatchSecurityHelper batchSecurityHelper = null;

    @Reference(policyOption = ReferencePolicyOption.GREEDY)
    protected void setIPersistenceManagerService(IPersistenceManagerService pms) {
        this.persistenceManagerService = pms;
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void setWSBatchAuthService(WSBatchAuthService bas) {
        this.authService = bas;
    }

    protected void unsetWSBatchAuthService(WSBatchAuthService bas) {
        if (this.authService == bas) {
            this.authService = null;
        }
    }

    protected void unsetIPersistenceManagerService(IPersistenceManagerService ref) {
        if (this.persistenceManagerService == ref) {
            this.persistenceManagerService = null;
        }
    }

    @Reference
    protected void setBatchSecurityHelper(BatchSecurityHelper batchSecurityHelper) {
        this.batchSecurityHelper = batchSecurityHelper;
    }

    /**
     * DS un-setter.
     */
    protected void unsetBatchSecurityHelper(BatchSecurityHelper batchSecurityHelper) {
        if (this.batchSecurityHelper == batchSecurityHelper) {
            this.batchSecurityHelper = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSJobInstance getJobInstanceFromExecution(long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        long instanceId = persistenceManagerService.getJobInstanceIdFromExecutionId(authorizedExecutionRead(executionId));
        return persistenceManagerService.getJobInstance(instanceId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSJobExecution getJobExecution(long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        return persistenceManagerService.getJobExecution(authorizedExecutionRead(executionId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSJobExecution createJobExecution(long jobInstanceId, Properties jobParameters) {
        return persistenceManagerService.createJobExecution(jobInstanceId, jobParameters, new Date());
    }

    /**
     * {@inheritDoc}
     */
    public List<WSStepThreadExecutionAggregate> getStepExecutionsFromJobExecution(long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        return persistenceManagerService.getStepExecutionAggregatesFromJobExecutionId(authorizedExecutionRead(jobExecutionId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WSJobInstance> getJobInstances(int page, int pageSize) {

        //Return the whole list for an admin or monitor
        if (authService == null || authService.isAdmin() || authService.isMonitor()) {
            return new ArrayList<WSJobInstance>(persistenceManagerService.getJobInstances(page, pageSize));
        } else if (authService.isSubmitter()) {
            //filter based on current user if not admin or monitor
            return new ArrayList<WSJobInstance>(persistenceManagerService.getJobInstances(page, pageSize, authService.getRunAsUser()));
        }

        throw new JobSecurityException("The current user " + batchSecurityHelper.getRunAsUser() + " is not authorized to perform any batch operations.");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WSJobInstance> getJobInstances(WSSearchObject wsso, int page, int pageSize) throws NoSuchJobExecutionException, JobSecurityException {

        if (authService == null || authService.isAdmin() || authService.isMonitor()) {
            return new ArrayList<WSJobInstance>(persistenceManagerService.getJobInstances(wsso, page, pageSize));
        } else if (authService.isSubmitter()) {
            wsso.setAuthSubmitter(authService.getRunAsUser());
            return new ArrayList<WSJobInstance>(persistenceManagerService.getJobInstances(wsso, page, pageSize));
        }

        throw new JobSecurityException("The current user " + batchSecurityHelper.getRunAsUser() + " is not authorized to perform any batch operations.");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSJobInstance getJobInstance(long instanceId) throws NoSuchJobExecutionException, JobSecurityException {

        return persistenceManagerService.getJobInstance(authorizedInstanceRead(instanceId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WSJobExecution> getJobExecutionsFromInstance(long instanceId) throws NoSuchJobInstanceException, JobSecurityException {
        return new ArrayList<WSJobExecution>(persistenceManagerService.getJobExecutionsFromJobInstanceId(authorizedInstanceRead(instanceId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBatchAppNameFromExecution(long executionId) throws NoSuchJobInstanceException, JobSecurityException {
        return persistenceManagerService.getJobInstanceAppNameFromExecutionId(authorizedExecutionRead(executionId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBatchAppNameFromInstance(long instanceId) throws NoSuchJobInstanceException, JobSecurityException {
        return persistenceManagerService.getJobInstanceAppName(authorizedInstanceRead(instanceId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSJobExecution getMostRecentJobExecutionFromInstance(long instanceId) throws NoSuchJobInstanceException, JobSecurityException {
        return persistenceManagerService.getJobExecutionMostRecent(authorizedInstanceRead(instanceId));
    }

    private long authorizedInstanceRead(long instanceId) throws NoSuchJobInstanceException, JobSecurityException {
        if (authService != null) {
            authService.authorizedInstanceRead(instanceId);
        }
        return instanceId;
    }

    private long authorizedExecutionRead(long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        if (authService != null) {
            authService.authorizedExecutionRead(executionId);
        }
        return executionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getJobNames() {

        if (authService == null || authService.isAdmin() || authService.isMonitor()) {
            return persistenceManagerService.getJobNamesSet();
        } else if (authService.isSubmitter()) {
            return persistenceManagerService.getJobNamesSet(authService.getRunAsUser());
        }

        throw new JobSecurityException("The current user " + authService.getRunAsUser() + " is not authorized to perform any batch operations.");

    }

    @Override
    public boolean isJobInstancePurgeable(long jobInstanceId) throws NoSuchJobInstanceException, JobSecurityException {
        //Make sure users have permissions to know anything about this job instance
        if (authService != null) {
            authService.authorizedInstanceRead(jobInstanceId);
        }

        return persistenceManagerService.isJobInstancePurgeable(jobInstanceId);
    }

    @Override
    public WSJobInstance updateJobInstanceState(long instanceId, InstanceState state) {
        return (WSJobInstance) persistenceManagerService.updateJobInstanceWithInstanceState(instanceId, state, new Date());
    }

    @Override
    public WSJobInstance updateJobInstanceStateUponRestart(long instanceId, InstanceState state) {
        return (WSJobInstance) persistenceManagerService.updateJobInstanceWithInstanceStateUponRestart(instanceId, state, new Date());
    }

    /**
     * @return a updated JobInstance for the given appName and JSL file.
     *
     *         Note: Added for updating jobs to FAILED when JSL cannot be located, hence job fails.
     *         {@inheritDoc}
     *
     */
    @Override
    public WSJobInstance updateJobInstanceAndExecutionWithInstanceStateAndBatchStatus(long instanceId, long executionId,
                                                                                      final InstanceState state, final BatchStatus batchStatus) {

        JobInstanceEntity retMe = null;

        //Update the JobInstance
        retMe = (JobInstanceEntity) persistenceManagerService.updateJobInstanceWithInstanceStateAndBatchStatus(instanceId, state, batchStatus, new Date());

        //Update the Job Execution Instance
        persistenceManagerService.updateJobExecutionAndInstanceOnStatusChange(executionId, batchStatus, new Date());

        return retMe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WSStepThreadExecutionAggregate> getStepExecutionAggregatesFromJobExecution(
                                                                                           long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        if (authService != null) {
            authService.authorizedExecutionRead(jobExecutionId);
        }
        return persistenceManagerService.getStepExecutionAggregatesFromJobExecutionId(jobExecutionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSStepThreadExecutionAggregate getStepExecutionAggregateFromJobExecution(
                                                                                    long jobExecutionId, String stepName) throws NoSuchJobExecutionException, JobSecurityException {

        if (authService != null) {
            authService.authorizedExecutionRead(jobExecutionId);
        }
        return persistenceManagerService.getStepExecutionAggregateFromJobExecutionId(jobExecutionId, stepName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSStepThreadExecutionAggregate getStepExecutionAggregate(
                                                                    long topLevelStepExecutionId) throws IllegalArgumentException, JobSecurityException {

        if (authService != null) {
            authService.authorizedStepExecutionRead(topLevelStepExecutionId);
        }
        return persistenceManagerService.getStepExecutionAggregate(topLevelStepExecutionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSStepThreadExecutionAggregate getStepExecutionAggregateFromJobExecutionNumberAndStepName(long jobInstanceId,
                                                                                                     short jobExecNum,
                                                                                                     String stepName) throws NoSuchJobExecutionException, JobSecurityException {

        if (authService != null) {
            authService.authorizedInstanceRead(jobInstanceId);
        }
        return persistenceManagerService.getStepExecutionAggregateFromJobExecutionNumberAndStepName(jobInstanceId,
                                                                                                    jobExecNum, stepName);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSJobExecution getJobExecutionFromJobExecNum(long jobInstanceId, int jobExecNum) throws NoSuchJobExecutionException, JobSecurityException {
        if (authService != null) {
            authService.authorizedInstanceRead(jobInstanceId);
        }
        return persistenceManagerService.getJobExecutionFromJobExecNum(jobInstanceId, jobExecNum);
    }

    @Override
    public WSJobInstance updateJobInstanceWithInstanceStateAndBatchStatus(
                                                                          long instanceId, InstanceState state, BatchStatus batchStatus) {
        return (WSJobInstance) persistenceManagerService.updateJobInstanceWithInstanceStateAndBatchStatus(instanceId, state, batchStatus, new Date());
    }

    @Override
    public WSJobExecution updateJobExecutionAndInstanceOnStatusChange(
                                                                      long jobExecutionId, BatchStatus status, Date date) {
        return (WSJobExecution) persistenceManagerService.updateJobExecutionAndInstanceOnStatusChange(jobExecutionId, status, date);
    }

    @Override
    public WSJobExecution updateJobExecutionAndInstanceOnStop(
                                                              long jobExecutionId, Date date) {
        return (WSJobExecution) persistenceManagerService.updateJobExecutionAndInstanceOnStop(jobExecutionId, date);
    }

    @Override
    public WSRemotablePartitionExecution createRemotablePartition(long jobExecutionId, String stepName,
                                                                  int partitionNumber, RemotablePartitionState internalState) {
        return persistenceManagerService.createRemotablePartition(jobExecutionId, stepName, partitionNumber, internalState);
    }

    @Override
    public WSRemotablePartitionExecution updateRemotablePartitionInternalState(long jobExecutionId, String stepName,
                                                                               int partitionNumber, RemotablePartitionState internalState) {
        return persistenceManagerService.updateRemotablePartitionInternalState(jobExecutionId, stepName, partitionNumber, internalState);
    }

    /** {@inheritDoc} 
     * @throws Exception */
    @Override
    public int getJobExecutionTableVersion() throws Exception {
        return persistenceManagerService.getJobExecutionTableVersion();
    }
    
    /** {@inheritDoc} 
     * @throws Exception */
    @Override
    public int getJobInstanceTableVersion() throws Exception {
        return persistenceManagerService.getJobInstanceTableVersion();
    }
}
