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

/**
 * Thrown when a job request (e.g STOP, or GetJobLog) cannot be run in this
 * server because the job is not running here.
 */
public class BatchJobNotLocalException extends Exception {

    private final WSJobExecution jobExecution;

    private static final long serialVersionUID = 1L;

    public BatchJobNotLocalException(WSJobExecution jobExecution, String localBatchRestUrl, String localServerId) {
        super("The request cannot be completed because the job execution " + jobExecution.getExecutionId()
              + " did not run in this server.  The job execution's restUrl=" + jobExecution.getRestUrl()
              + " and serverId=" + jobExecution.getServerId()
              + ". This server's restUrl=" + localBatchRestUrl + " and serverId=" + localServerId);

        this.jobExecution = jobExecution;
    }

    public WSJobExecution getJobExecution() {
        return jobExecution;
    }
}
