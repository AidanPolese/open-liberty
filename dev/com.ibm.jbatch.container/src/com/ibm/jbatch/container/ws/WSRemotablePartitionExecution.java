package com.ibm.jbatch.container.ws;

import javax.batch.runtime.JobExecution;

public interface WSRemotablePartitionExecution {
	
	public String getLogpath();
	
	public String getRestUrl();
	
	public String getServerId();
	
	public JobExecution getJobExecution();
	
	public String getStepName();
	
	public int getPartitionNumber();

}
