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
 * @author skurz
 *
 */
public class RemotableSplitFlowKey implements Serializable {

	private static final long serialVersionUID = 1L;

	public RemotableSplitFlowKey() { }
	
	public RemotableSplitFlowKey(long jobExecutionId, String flowName) {
		this.jobExec = jobExecutionId;
		this.flowName = flowName;
	}

	private long jobExec;
	
	private String flowName;

    public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public int hashCode() {
        return (new Long(jobExec).intValue() + flowName.hashCode()) / 37;
    }
    
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RemotableSplitFlowKey)) return false;
        RemotableSplitFlowKey pk = (RemotableSplitFlowKey) obj;
        return (pk.flowName.equals(this.flowName) && pk.jobExec == this.jobExec);
    }

	public long getJobExec() {
		return jobExec;
	}

	public void setJobExec(long jobExecutionId) {
		this.jobExec = jobExecutionId;
	}

	@Override
	public String toString() {
		return "Type: RemotableSplitFlowKey, fields:  jobExecutionId = " + jobExec + ", flowName = " + flowName;
	}
}
