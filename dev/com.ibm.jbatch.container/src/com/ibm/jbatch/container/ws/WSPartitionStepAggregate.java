/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.jbatch.container.ws;

/**
 * For a given logical partition, this object aggregates related data
 * from each of the STEPTHREADEXECUTION and REMOTABLEPARTITION tables.
 */
public interface WSPartitionStepAggregate {

    /**
     * @return STEPTHREADEXECUTION data
     */
    public WSPartitionStepThreadExecution getPartitionStepThread();

    /**
     * @return REMOTABLEPARTITION data
     */
    public WSRemotablePartitionExecution getRemotablePartition();

}