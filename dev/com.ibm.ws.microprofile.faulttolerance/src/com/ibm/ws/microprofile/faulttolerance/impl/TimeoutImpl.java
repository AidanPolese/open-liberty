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

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.microprofile.faulttolerance.impl.async.QueuedFuture;
import com.ibm.ws.microprofile.faulttolerance.spi.TimeoutPolicy;

/**
 *
 */
public class TimeoutImpl {

    private static final TraceComponent tc = Tr.register(TimeoutImpl.class);

    private final TimeoutPolicy timeoutPolicy;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Future<?> future;
    private boolean timedout = false;
    private boolean stopped = false;
    private long targetEnd;

    private Runnable timeoutTask;

    /**
     * @param timeoutPolicy
     */
    public TimeoutImpl(TimeoutPolicy timeoutPolicy, ScheduledExecutorService scheduledExecutorService) {
        this.timeoutPolicy = timeoutPolicy;
        this.scheduledExecutorService = scheduledExecutorService;
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
            queuedFuture.cancel(true);
        };

        start(timeoutTask);
    }

    //WARNING: This method uses System.nanoTime(). nanoTime is a point in time relative to an arbitrary point (fixed at runtime).
    //As a result, it could be positive or negative and will not bare any relation to the actual time ... it's just a relative measure.
    //Also, since it could be massively positive or negative, caution must be used when doing comparisons due to the possibility
    //of numerical overflow e.g. one should use t1 - t0 < 0, not t1 < t0
    //The reason we use this here is that it not affected by changes to the system clock at runtime
    private void start(Runnable timeoutTask) {
        Runnable task = () -> {
            lock.writeLock().lock();
            try {
                long now = System.nanoTime();
                this.timedout = (now - this.targetEnd) >= 0;
                if (this.timedout) {
                    timeoutTask.run();
                }
            } finally {
                lock.writeLock().unlock();
            }
        };

        lock.writeLock().lock();
        try {
            this.timeoutTask = timeoutTask;
            long start = System.nanoTime();
            long timeout = timeoutPolicy.getTimeout().toNanos();
            this.targetEnd = start + timeout;
            long remaining = this.targetEnd - System.nanoTime();

            if (remaining > 0) {
                this.future = scheduledExecutorService.schedule(task, remaining, TimeUnit.NANOSECONDS);
            } else {
                task.run();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void stop() {
        lock.writeLock().lock();
        try {
            this.stopped = true;
            if (this.future != null && !this.future.isDone()) {
                this.future.cancel(true);
            }
            this.future = null;
        } finally {
            lock.writeLock().unlock();
        }

    }

    public void restart() {
        lock.writeLock().lock();
        try {
            if (this.timeoutTask == null) {
                throw new IllegalStateException(Tr.formatMessage(tc, "internal.error.CWMFT4999E"));
            }
            stop();
            this.stopped = false;
            start(this.timeoutTask);
        } finally {
            lock.writeLock().unlock();
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

    public long check() {
        long remaining = 0;
        lock.readLock().lock();
        try {
            if (this.timedout) {
                throw new TimeoutException(Tr.formatMessage(tc, "timeout.occurred.CWMFT0000E"));
            }
            remaining = this.targetEnd - System.nanoTime();
        } finally {
            lock.readLock().unlock();
        }
        return remaining;
    }
}
