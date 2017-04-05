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
package com.ibm.jbatch.container.ws;

import com.ibm.jbatch.container.exception.BatchContainerRuntimeException;

public class BatchDispatcherException extends BatchContainerRuntimeException {
    
    private long jobExecutionId;
    private long jobInstanceId;
    
    private static final long serialVersionUID = 1L;

    public BatchDispatcherException(String message, long jobInstanceId, long jobExecutionId) {
        super(message + " encounters when dispatching job instance " + jobInstanceId + ", job execution " + jobExecutionId);
        this.jobExecutionId = jobExecutionId;
        this.jobInstanceId = jobInstanceId;
    }
    
    public BatchDispatcherException(Throwable cause, long jobInstanceId, long jobExecutionId) {
        super("Unable to dispatch job instance " + jobInstanceId + ", job execution id " + jobExecutionId, cause);
    }
    
    public BatchDispatcherException(Throwable cause, long jobExecutionId) {
        super("Unable to dispatch job execution id " + jobExecutionId, cause);
        this.jobExecutionId = jobExecutionId;
    } 
    
    public BatchDispatcherException(Throwable cause ){
        super(cause);
    }
    
    public BatchDispatcherException(String message, Throwable cause ){
        super(message, cause);
    }
    
    public BatchDispatcherException(String message, long jobExecutionId) {
        super(message + " encounters when dispatching job execution " + jobExecutionId);
        this.jobExecutionId = jobExecutionId;        
    }
    
    public BatchDispatcherException(String message) {
        super(message + " encounters when dispatching job execution ");
         
    }
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    public long getJobInstanceId() {
        return jobInstanceId;
    }
}
