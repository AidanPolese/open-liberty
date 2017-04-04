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
 * The queuing API used by a partitioned step to send and recv data between the
 * top-level thread and the partitioned threads.
 * 
 * There are two implementations:
 * 1. PartitionReplyQueueLocal - for local partitions (same JVM)
 * 2. PartitionReplyQueueJms - for remote partitions (multi-JVM)
 * 
 */
public interface PartitionReplyQueue  {

    /**
     * Receive a msg from the queue.  
     * 
     * The top-level thread calls this method to recv data from the sub-job partition threads.
     */
    public PartitionReplyMsg take() throws InterruptedException;
    
    /**
     * Add a msg to the queue.
     * 
     * The partition threads call this method to send data back to the top-level thread.
     */
    public boolean add(PartitionReplyMsg msg) ;
    
    /**
     * Close the queue.
     */
    public void close() ;

    /**
     * Receive a msg from the queue without waiting for it indefinitely. 
     * 
     * The top-level thread calls this method to recv data from the sub-job partition threads.
     */
	public PartitionReplyMsg takeWithoutWaiting();

}
