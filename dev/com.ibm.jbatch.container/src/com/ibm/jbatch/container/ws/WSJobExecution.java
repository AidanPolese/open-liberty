package com.ibm.jbatch.container.ws;

import javax.batch.runtime.JobExecution;

import com.ibm.jbatch.container.persistence.jpa.JobExecutionEntity;
import com.ibm.jbatch.container.persistence.jpa.JobInstanceEntity;

/**
 * WS-specific extension to the JobExecution interface, used internally by WAS/Liberty.
 */
public interface WSJobExecution extends JobExecution {

    /**
     * @return The batch rest URL (https://<host>:<port>/ibm/api/batch) of the server
     *         where this jobexecution ran/is running.
     */
    public String getRestUrl();
    
    /**
     * @return the unique identity of the server where this jobexecution ran/is running.
     *         ${defaultHostName}/${wlp.user.dir}/serverName
     */
    public String getServerId();
    
    /**
     * @return the HFS path to the job execution's logs.
     */
    public String getLogpath();
    
	/**
	 * Get unique id for this JobInstance.
	 * @return instance id
	 */
	public long getInstanceId();

	/**
	 * Gets execution number for instance. Indexed from '0', 
	 * so the initial "start" execution => 0, the first restart => 1, etc.
	 * 
	 * @return
	 */
	public int getExecutionNumberForThisInstance();
 
	
	/**
	 * Get job instance.
	 * Caller can cast this to WSJobInstance
	 * 
	 * @return
	 */
	public JobInstanceEntity getJobInstance();
}
