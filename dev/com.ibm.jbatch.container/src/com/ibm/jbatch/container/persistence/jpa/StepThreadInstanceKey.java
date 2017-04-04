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

import java.io.Serializable;

/**
 * If this represents a top-level thread (any non-sub-job-partition thread),
 * then the partitionNumber will be -1, and it will actually be an instance of
 * TopLevelStepInstanceKey (subclass).
 * 
 */
public class StepThreadInstanceKey implements Serializable, EntityConstants {
	
	private static final long serialVersionUID = 1L;

	public StepThreadInstanceKey() { }

	public StepThreadInstanceKey(long topLevelJobInstanceId, String stepName, Integer partitionNumber) {
		this.jobInstance = topLevelJobInstanceId;
		this.stepName = stepName;
		this.partitionNumber = partitionNumber;
	}
	public StepThreadInstanceKey(StepThreadInstanceEntity stepThreadInstance) {
		this.jobInstance = stepThreadInstance.getJobInstance().getInstanceId();
		this.stepName = stepThreadInstance.getStepName();
		this.partitionNumber = stepThreadInstance.getPartitionNumber();
	}

	private String stepName;

	private int partitionNumber;
	
	private long jobInstance;

    public int hashCode() {
        return (new Long(jobInstance).intValue() + partitionNumber + stepName.hashCode()) / 37;
    }
    
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof StepThreadInstanceKey)) return false;
        StepThreadInstanceKey pk = (StepThreadInstanceKey) obj;
        return (pk.stepName.equals(this.stepName) && pk.partitionNumber == this.partitionNumber && pk.jobInstance == this.jobInstance);
    }

	/**
	 * @return the stepName
	 */
	public String getStepName() {
		return stepName;
	}

	/**
	 * @return the jobInstance
	 */
	public long getJobInstance() {
		return jobInstance;
	}

	/**
	 * @return the partitionNumber
	 */
	public int getPartitionNumber() {
		return partitionNumber;
	}

	@Override
	public String toString() {
		return "Type: StepThreadInstanceKey, fields:  jobInstanceId = " + jobInstance + ", stepName = " + stepName + ", partitionNumber = " + partitionNumber;
	}
}
