/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.faulttolerance.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

import com.ibm.ws.microprofile.faulttolerance.impl.async.QueuedFuture;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;


import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
/**
 *
 */
public class Timeout {


    private static final TraceComponent tc = Tr.register(Timeout.class);

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
                throw new IllegalStateException(Tr.formatMessage(tc, "internal.error.CWMFT4999E"));
            }

            this.future = executorService.submit(task);
        }
    }

    public void stop() {
        synchronized (this) {
            if (this.future == null) {
                throw new IllegalStateException(Tr.formatMessage(tc, "internal.error.CWMFT4999E"));
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
            throw new TimeoutException(Tr.formatMessage(tc, "timeout.occurred.CWMFT0000E"));
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
