package com.ibm.jbatch.container.ws.impl;

import java.util.concurrent.LinkedBlockingQueue;

import com.ibm.jbatch.container.ws.PartitionReplyMsg;
import com.ibm.jbatch.container.ws.PartitionReplyQueue;

/**
 * The local (same JVM) version of the PartitionReplyQueue.
 * 
 * It uses a LinkedBlockingQueue to send messages from the partition threads to
 * the top-level thread.
 */
public class PartitionReplyQueueLocal extends LinkedBlockingQueue<PartitionReplyMsg> implements PartitionReplyQueue {

    /**
     * Does nothing in the local case.
     */
    @Override
    public void close() {
        // no-op.
    }

	@Override
	public PartitionReplyMsg takeWithoutWaiting() {
		return null;
	}
}
