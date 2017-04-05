package com.ibm.jbatch.container.ws;

import java.io.Serializable;

/**
 * Properties are also propagatable from the top level, but they need to be treated differently,
 * since some will potentially need to be resolved (especially for partitions)
 * early, before the job model is fully constructed.
 */
public class TopLevelNameInstanceExecutionInfo implements Serializable {

	/**
     * default.
     */
    private static final long serialVersionUID = 1L;
    
    private String jobName;
	private long instanceId;
	private long executionId;
	
	public TopLevelNameInstanceExecutionInfo(String jobName, long instanceId, long executionId) {
		this.jobName = jobName;
		this.instanceId = instanceId;
		this.executionId = executionId;
	}	

    public String getJobName() {
		return jobName;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public long getExecutionId() {
		return executionId;
	}
	
	public String toString() {
	    return "TopLevelNameIntanceExecutionInfo:jobName=" + jobName + ":instanceId=" + instanceId + ":executionId=" + executionId;
	}
		
}
