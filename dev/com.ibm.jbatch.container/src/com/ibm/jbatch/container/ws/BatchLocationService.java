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
package com.ibm.jbatch.container.ws;

import javax.batch.runtime.JobExecution;



/**
 * Resolves the batch REST url based on endpoint, server, and/or system config.
 */
public interface BatchLocationService {
   
    /**
     * @return the batch REST url for this server: https://{host}:{port}/ibm/api/batch
     */
    public String getBatchRestUrl() ;
      
    /**
     * @return unique identity for this server: ${defaultHostName}/${wlp.user.dir}/serverName
     */
    public String getServerId() ;

    /**
     * @return true if the given jobexecution ran (or is running) on this server.
     */
    public boolean isLocalJobExecution(WSJobExecution jobExecution) ;
    
    /**
     * @return true if the given jobexecution ran (or is running) on this server.
     */
    public boolean isLocalJobExecution(long executionId) ;
    
    /**
     * @return the JobExecution instance.
     * 
     * @throws BatchJobNotLocalException if the given execution did not execute here in this server.
     */
    public JobExecution assertIsLocalJobExecution(long executionId) throws BatchJobNotLocalException ;
 
}
