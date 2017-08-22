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
package com.ibm.ws.microprofile.faulttolerance.impl.async;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.faulttolerance.impl.Timeout;
import com.ibm.wsspi.threadcontext.ThreadContext;
import com.ibm.wsspi.threadcontext.ThreadContextDescriptor;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 *
 */
public class QueuedFuture<R> implements Future<R>, Callable<Future<R>> {

    private static final TraceComponent tc = Tr.register(QueuedFuture.class);

    private final Callable<Future<R>> callable;
    private final Timeout timeout;

    private Future<Future<R>> futureFuture;
    private final ThreadContextDescriptor threadContext;

    public QueuedFuture(Callable<Future<R>> callable, Timeout timeout, ThreadContextDescriptor threadContext) {
        this.callable = callable;
        this.timeout = timeout;
        this.threadContext = threadContext;
    }

    /** {@inheritDoc} */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this) {
            return getFutureFuture().cancel(mayInterruptIfRunning);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCancelled() {
        synchronized (this) {
            return getFutureFuture().isCancelled();
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDone() {
        synchronized (this) {
            return getFutureFuture().isDone();
        }
    }

    /** {@inheritDoc} */
    @Override
    public R get() throws InterruptedException, ExecutionException {
        R result = null;
        synchronized (this) {
            Future<Future<R>> future = getFutureFuture();

            if (this.timeout != null) {
                this.timeout.check();
            }

            try {
                result = future.get().get();
            } finally {
                if (timeout != null) {
                    timeout.stop(true);
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore({ CancellationException.class, TimeoutException.class })
    public R get(long methodTimeout, TimeUnit methodUnit) throws InterruptedException, ExecutionException, TimeoutException {
        R result = null;
        synchronized (this) {
            long start = System.currentTimeMillis();

            Future<Future<R>> future = getFutureFuture();
            if (this.timeout != null) {
                this.timeout.check();
            }

            //convert the method params to millis
            long methodMillis = TimeUnit.MILLISECONDS.convert(methodTimeout, methodUnit);
            //when do the method params say we should end?
            long methodEnd = start + methodMillis;
            //when does the FT Timeout think we should end?
            long timeoutEnd = this.timeout == null ? Long.MAX_VALUE : this.timeout.end();

            //what is the least amount of time we should wait to get the futureFuture?
            long earliest = Math.min(methodEnd, timeoutEnd);

            long now = System.currentTimeMillis();
            long remaining = earliest - now;
            //how long remaining according the the method params (FT Timeout will interrupt if it needs to)
            remaining = methodEnd - now;

            try {
                result = future.get(remaining, TimeUnit.MILLISECONDS).get(remaining, TimeUnit.MILLISECONDS); //TODO do both get calls need timeout?
            } catch (InterruptedException | CancellationException e) {
                //if the future was interrupted or cancelled, check if it was because the FT Timeout popped
                if (this.timeout != null) {
                    this.timeout.check();
                }
                throw e;
            } catch (TimeoutException e) {
                throw e;
            } finally {
                //we've finished one way or another!
                if (this.timeout != null) {
                    this.timeout.stop();
                }
            }
        }
        return result;

    }

    /**
     * @return
     * @throws Exception
     */
    @Override
    public Future<R> call() throws Exception {
        Future<R> result = null;

        ArrayList<ThreadContext> contextAppliedToThread = null;
        if (this.threadContext != null) {
            contextAppliedToThread = this.threadContext.taskStarting();
        }
        try {
            result = callable.call();
        } finally {
            if (contextAppliedToThread != null) {
                this.threadContext.taskStopping(contextAppliedToThread);
            }
        }
        return result;
    }

    private Future<Future<R>> getFutureFuture() {
        synchronized (this) {
            if (this.futureFuture == null) {
                //shouldn't be possible unless the QueuedFuture was created but not started
                throw new IllegalStateException(Tr.formatMessage(tc, "internal.error.CWMFT4999E"));
            }
        }
        return this.futureFuture;
    }

    /**
     *
     */
    public void timeout() {
        synchronized (this) {
            cancel(true);
        }
    }

    /**
     * @param executorService
     */
    @FFDCIgnore({ RejectedExecutionException.class })
    public void start(ExecutorService executorService) {
        synchronized (this) {
            if (timeout != null) {
                timeout.start(this);
            }
            try {
                Future<Future<R>> futureFuture = executorService.submit(this);
                this.futureFuture = futureFuture;
            } catch (RejectedExecutionException e) {
                if (timeout != null) {
                    timeout.stop();
                }
                throw new BulkheadException(e);
            }
        }
    }
}
