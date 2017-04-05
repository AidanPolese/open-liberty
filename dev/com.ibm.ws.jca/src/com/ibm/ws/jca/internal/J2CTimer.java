/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2005, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.internal;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.resource.spi.RetryableUnavailableException;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.work.WorkManager;

import com.ibm.wsspi.threadcontext.ThreadContext;
import com.ibm.wsspi.threadcontext.ThreadContextDescriptor;
import com.ibm.wsspi.threadcontext.WSContextService;

public class J2CTimer extends Timer {

    /**
     * The bootstrap context.
     */
    private final BootstrapContextImpl bootstrapContext;

    /**
     * Main constructor for creating a new J2CTimer
     * 
     * @param bootstrapContext the bootstrap context.
     * @throws UnavailbleException if unable to create the timer.
     */
    public J2CTimer(BootstrapContextImpl bootstrapContext) throws UnavailableException {
        super("resourceAdapter[" + bootstrapContext.resourceAdapterID + "]-Timer", true);
        this.bootstrapContext = bootstrapContext;

        if (bootstrapContext.propagateThreadContext) {
            // Immediately run a task that contextualizes the timer thread.
            // It is not an option to wrap the java.util.TimerTask in order to apply context, because wrapping the TimerTask
            // would interfere with how it interoperates internally with java.util.Timer.
            Object result = null;
            try {
                BlockingQueue<Object> results = new LinkedBlockingQueue<Object>();
                Map<String, String> execProps = new TreeMap<String, String>();
                execProps.put("javax.enterprise.concurrent.IDENTITY_NAME", getClass().getName());
                execProps.put("javax.enterprise.concurrent.LONGRUNNING_HINT", Boolean.TRUE.toString());
                execProps.put(WSContextService.DEFAULT_CONTEXT, WSContextService.UNCONFIGURED_CONTEXT_TYPES);
                execProps.put(WSContextService.SKIP_CONTEXT_PROVIDERS, "com.ibm.ws.transaction.context.provider"); // Avoid creating an LTC that lasts for the duration of the timer thread
                execProps.put(WSContextService.TASK_OWNER, bootstrapContext.resourceAdapterID);
                TimerTask contextualizer = new ContextualizerTask(bootstrapContext.contextSvc.captureThreadContext(execProps), results);
                schedule(contextualizer, WorkManager.IMMEDIATE);
                result = results.poll(10, TimeUnit.SECONDS);
                if (result == null)
                    throw new RetryableUnavailableException(Utils.getMessage("J2CA8510.create.timed.out", bootstrapContext.resourceAdapterID, 10000));
                else if (result instanceof UnavailableException)
                    throw (UnavailableException) result;
            } catch (IllegalStateException x) {
                throw new UnavailableException(x);
            } catch (InterruptedException x) {
                throw new RetryableUnavailableException(x);
            } finally {
                if (result instanceof ArrayList)
                    bootstrapContext.timers.add(this);
                else
                    super.cancel();
            }
        } else
            bootstrapContext.timers.add(this);
    }

    /**
     * @see java.util.Timer#cancel()
     */
    @Override
    public void cancel() {
        bootstrapContext.timers.remove(this);
        super.cancel();
    }

    /**
     * This task applies context to the Timer thread.
     */
    private static class ContextualizerTask extends TimerTask {
        /**
         * Results of running the task.
         */
        private final BlockingQueue<Object> results;

        /**
         * Thread context descriptor.
         */
        private final ThreadContextDescriptor threadContextDescriptor;

        /**
         * Construct a new ContextualizerTask.
         * 
         * @param threadContextDescriptor thread context descriptor.
         * @param results queue that is populated with the thread context once it is propagated to the timer thread.
         */
        private ContextualizerTask(ThreadContextDescriptor threadContextDescriptor, BlockingQueue<Object> results) {
            this.threadContextDescriptor = threadContextDescriptor;
            this.results = results;
        }

        /**
         * Apply context to the timer thread.
         */
        @Override
        public void run() {
            try {
                ArrayList<ThreadContext> threadContext = threadContextDescriptor.taskStarting();
                results.add(threadContext);
            } catch (Throwable x) {
                results.add(new UnavailableException(x));
            }
        }
    }
}