/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.threading;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.workqueue.AutomaticWorkQueue;

import com.ibm.wsspi.threading.WSExecutorService;

public class LibertyJaxWsAutomaticWorkQueueImpl implements AutomaticWorkQueue {

    private final ScheduledExecutorService scheduleExecutor;

    private final WSExecutorService wsExecutorService;

    final private String name;

    public LibertyJaxWsAutomaticWorkQueueImpl(ScheduledExecutorService scheduleExecutor, WSExecutorService executor) {
        this.name = "default";
        this.wsExecutorService = executor;
        this.scheduleExecutor = scheduleExecutor;
    }

    @Override
    public void execute(Runnable work, long timeout) {
        wsExecutorService.executeGlobal(work);
    }

    @Override
    public void schedule(Runnable work, long delay) {
        this.scheduleExecutor.schedule(work, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void execute(Runnable work) {
        wsExecutorService.executeGlobal(work);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public void shutdown(boolean processRemainingWorkItems) {
        // do nothing so far as LibertyJaxwsAutomaticWorkQueueImpl can not be shutdown
    }

}
