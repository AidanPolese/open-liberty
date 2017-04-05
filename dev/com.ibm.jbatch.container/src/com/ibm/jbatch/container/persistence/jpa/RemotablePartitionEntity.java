/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.jbatch.container.persistence.jpa;

import java.util.Date;

import javax.batch.runtime.JobExecution;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;

import com.ibm.jbatch.container.ws.RemotablePartitionState;
import com.ibm.jbatch.container.ws.WSRemotablePartitionExecution;

/**
 * @author skurz
 * 
 */
/* 222050 - Backout 205106
@NamedQueries({

               @NamedQuery(name = RemotablePartitionEntity.GET_ALL_RELATED_REMOTABLE_PARTITIONS,
                               query = "SELECT r FROM RemotablePartitionEntity r WHERE r.stepExecutionEntity.stepExecutionId IN (SELECT s.stepExecutionId FROM StepThreadExecutionEntity s WHERE s.topLevelStepExecution.stepExecutionId = :topLevelStepExecutionId AND TYPE(s) = StepThreadExecutionEntity ) ORDER BY r.stepExecutionEntity.partitionNumber ASC"),
               @NamedQuery(name = RemotablePartitionEntity.GET_PARTITION_STEP_THREAD_EXECUTIONIDS_BY_SERVERID_AND_STATUSES_QUERY,
                               query = "SELECT r FROM RemotablePartitionEntity r WHERE r.serverId = :serverid AND r.stepExecutionEntity.batchStatus IN :status ORDER BY r.stepExecutionEntity.startTime DESC"),
})*/
@IdClass(RemotablePartitionKey.class)

/* 222050 - Backout 205106
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "FK_JOBEXECUTIONID", "STEPNAME", "PARTNUM" }))
@Entity
*/
public class RemotablePartitionEntity implements WSRemotablePartitionExecution {

    public static final String GET_ALL_RELATED_REMOTABLE_PARTITIONS = "RemotablePartitionEntity.getAllRelatedRemotablePartitions";
    public static final String GET_PARTITION_STEP_THREAD_EXECUTIONIDS_BY_SERVERID_AND_STATUSES_QUERY = "RemotablePartitionEntity.getPartitionStepExecutionByServerIdAndStatusesQuery";

    @Id
    @ManyToOne()
    @JoinColumn(name = "FK_JOBEXECUTIONID", nullable = false)
    private JobExecutionEntity jobExec;

    @JoinColumn(name = "FK_STEPEXECUTIONID")
    private StepThreadExecutionEntity stepExecutionEntity;

    @Id
    @Column(name = "STEPNAME")
    private String stepName;

    @Column(name = "PARTNUM")
    @Id
    private int partitionNumber;

    @Column(name = "INTERNALSTATE")
    private RemotablePartitionState internalStatus;

    @Column(name = "SERVERID", length = 256)
    private String serverId;

    @Column(name = "RESTURL", length = 512)
    private String restUrl;

    @Column(name = "LOGPATH", nullable = true, length = 512)
    private String logpath;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "LASTUPDATED")
    private Date lastUpdated;

    public RemotablePartitionEntity() {}

    public RemotablePartitionEntity(JobExecutionEntity jobExecution,
                                    RemotablePartitionKey partitionKey) {
        this.jobExec = jobExecution;
        this.stepName = partitionKey.getStepName();
        this.partitionNumber = partitionKey.getPartitionNumber();
    }

    public RemotablePartitionEntity(JobExecutionEntity jobExecution, String stepName, int partitionNum) {
        this.jobExec = jobExecution;
        this.stepName = stepName;
        this.partitionNumber = partitionNum;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date queuedTime) {
        this.lastUpdated = queuedTime;
    }

    /**
     * @return the stepName
     */
    @Override
    public String getStepName() {
        return stepName;
    }

    /**
     * @param stepName the stepName to set
     */
    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    @Override
    public int getPartitionNumber() {
        return partitionNumber;
    }

    public void setPartitionNumber(int partitionNumber) {
        this.partitionNumber = partitionNumber;
    }

    public RemotablePartitionState getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(RemotablePartitionState execution) {
        this.internalStatus = execution;
    }

    public JobExecutionEntity getJobExec() {
        return jobExec;
    }

    @Override
    public JobExecution getJobExecution() {
        return jobExec;
    }

    public void setJobExec(JobExecutionEntity jobExecutionId) {
        this.jobExec = jobExecutionId;
    }

    @Override
    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    @Override
    public String getLogpath() {
        return logpath;
    }

    public void setLogpath(String logpath) {
        this.logpath = logpath;
    }

    public StepThreadExecutionEntity getStepExecution() {
        return stepExecutionEntity;
    }

    public void setStepExecution(StepThreadExecutionEntity stepExecution) {
        this.stepExecutionEntity = stepExecution;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        Long jobExecutionId = jobExec == null ? null : jobExec.getExecutionId();
        buf.append(super.toString() + System.getProperty("line.separator"));
        buf.append("For RemotablePartitionExecutionEntity:");
        buf.append(", job executionId = " + jobExecutionId);
        buf.append(" stepName = " + stepName);
        buf.append(", partition number = " + partitionNumber);
        return buf.toString();
    }

}
