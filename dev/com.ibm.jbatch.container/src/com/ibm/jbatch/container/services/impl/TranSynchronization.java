/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.jbatch.container.services.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.Status;
import javax.transaction.Synchronization;

import com.ibm.jbatch.container.execution.impl.RuntimeStepExecution;
import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Automatically performs a task based on the transaction commit status.
 */
class TranSynchronization implements Synchronization {
    private final static String sourceClass = TranSynchronization.class.getName();
    private final static Logger logger = Logger.getLogger(sourceClass);
	private RuntimeStepExecution runtimeStepExecution;
	
    /**
     * Construct a new instance.
     */
	TranSynchronization(RuntimeStepExecution runtimeStepExecution) {
		this.runtimeStepExecution = runtimeStepExecution;
    }

    /**
     * Upon successful transaction commit status, store the value of the committed metrics.
     * Upon any other status value roll back the metrics to the last committed value.
     * 
     * @see javax.transaction.Synchronization#afterCompletion(int)
     */
    @Override
    public void afterCompletion(int status) {

    	
	    logger.log(Level.FINE, "The status of the transaction commit is: " + status);
		
        if (status == Status.STATUS_COMMITTED){
        	//Save the metrics object after a successful commit
        	runtimeStepExecution.setCommittedMetrics();
        } else{
        	//status = 4 = STATUS_ROLLEDBACK;
        	runtimeStepExecution.rollBackMetrics();
        }
    }

    /**
     * @see javax.transaction.Synchronization#beforeCompletion()
     */
    @Override
    @Trivial
    public void beforeCompletion() {}
}
