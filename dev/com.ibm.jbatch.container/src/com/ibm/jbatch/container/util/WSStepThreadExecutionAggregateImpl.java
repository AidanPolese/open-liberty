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
package com.ibm.jbatch.container.util;

import java.util.List;

import com.ibm.jbatch.container.ws.WSPartitionStepAggregate;
import com.ibm.jbatch.container.ws.WSPartitionStepThreadExecution;
import com.ibm.jbatch.container.ws.WSStepThreadExecutionAggregate;
import com.ibm.jbatch.container.ws.WSTopLevelStepExecution;

/**
 * @author skurz
 *
 */
public class WSStepThreadExecutionAggregateImpl implements
		WSStepThreadExecutionAggregate {
	
	private WSTopLevelStepExecution topLevelStepExecution;
	
	// 222050 - Backout 205106
	// private List<WSPartitionStepAggregate> partitionAggregate;
	private List<WSPartitionStepThreadExecution> partitionLevelStepExecutions;
	

	public WSTopLevelStepExecution getTopLevelStepExecution() {
		return topLevelStepExecution;
	}

	public void setTopLevelStepExecution(WSTopLevelStepExecution topLevelStepExecution) {
		this.topLevelStepExecution = topLevelStepExecution;
	}

	
	public List<WSPartitionStepAggregate> getPartitionAggregate() {
		//222050 - Backout 205106
	        // return partitionAggregate;
	        return null;
	}

	public void setPartitionAggregate(List<WSPartitionStepAggregate> partitionAggregate) {
		//222050 - Backout 205106
	        //this.partitionAggregate = partitionAggregate
	}
	
	public List<WSPartitionStepThreadExecution> getPartitionLevelStepExecutions() {
            return partitionLevelStepExecutions;
        }

        public void setPartitionLevelStepExecutions(
                    List<WSPartitionStepThreadExecution> partitionLevelStepExecutions) {
            this.partitionLevelStepExecutions = partitionLevelStepExecutions;
        }
	
}
