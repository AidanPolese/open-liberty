/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.faulttolerance.spi.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;

/**
 *
 */
public class Timeout {

    private final TimeoutPolicy timeoutPolicy;
    private final ExecutorService executorService;
    private Future<?> future;
    private volatile boolean timedout = false;
    private volatile boolean stopped = false;
    private volatile long end = Long.MAX_VALUE;

    /**
     * @param timeoutPolicy
     */
    public Timeout(TimeoutPolicy timeoutPolicy, ExecutorService executorService) {
        this.timeoutPolicy = timeoutPolicy;
        this.executorService = executorService;
    }

    /**
     * start timer and interrupt given thread
     */
    public void start(Thread targetThread) {
        Runnable timeoutTask = () -> {
            targetThread.interrupt();
        };

        start(timeoutTask);
    }

    /**
     * start timer and cancel given future
     */
    public void start(QueuedFuture<?> queuedFuture) {
        Runnable timeoutTask = () -> {
            queuedFuture.timeout();
        };

        start(timeoutTask);
    }

    private void start(Runnable timeoutTask) {
        long start = System.currentTimeMillis();
        long timeout = timeoutPolicy.getTimeout().toMillis();
        this.end = start + timeout;

        Runnable task = () -> {
            try {
                long remaining = remaining();
                if (remaining > 0) {
                    Thread.sleep(remaining);
                }
                timedout = true;
                timeoutTask.run();
            } catch (InterruptedException e) {
                //expected when timer is cancelled
            }
        };

        synchronized (this) {
            if (this.stopped || this.future != null) {
                throw new IllegalStateException();
            }

            this.future = executorService.submit(task);
        }
    }

    public void stop() {
        synchronized (this) {
            if (this.future == null) {
                throw new IllegalStateException();
            }
            this.stopped = true;
            if (!this.future.isDone()) {
                this.future.cancel(true);
            }
        }
    }

    /**
     *
     */
    public void stop(boolean exceptionOnTimeout) {
        stop();
        if (exceptionOnTimeout) {
            check();
        }
    }

    public boolean timedout() {
        return timedout;
    }

    /**
     *
     */
    public void check() {
        if (timedout) {
            throw new TimeoutException();
        }
    }

    /**
     * @return
     */
    public long remaining() {
        return this.end - System.currentTimeMillis();
    }

    public long end() {
        return this.end;
    }

}
