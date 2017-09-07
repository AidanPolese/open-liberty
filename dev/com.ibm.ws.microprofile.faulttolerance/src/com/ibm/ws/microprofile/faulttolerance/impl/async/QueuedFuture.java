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

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.faulttolerance.impl.ExecutionContextImpl;
import com.ibm.wsspi.threadcontext.ThreadContext;
import com.ibm.wsspi.threadcontext.ThreadContextDescriptor;

/**
 *
 */
public class QueuedFuture<R> implements Future<R>, Callable<Future<R>> {

    private static final TraceComponent tc = Tr.register(QueuedFuture.class);

    private final Callable<Future<R>> innerTask;

    private Future<Future<R>> futureFuture;
    private final ThreadContextDescriptor threadContext;

    private final ExecutionContextImpl executionContext;

    public QueuedFuture(Callable<Future<R>> innerTask, ExecutionContextImpl executionContext, ThreadContextDescriptor threadContext) {
        this.innerTask = innerTask;
        this.executionContext = executionContext;
        this.threadContext = threadContext;
    }

    /** {@inheritDoc} */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return getFutureFuture().cancel(mayInterruptIfRunning);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCancelled() {
        return getFutureFuture().isCancelled();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDone() {
        return getFutureFuture().isDone();
    }

    /** {@inheritDoc} */
    @Override
    public R get() throws InterruptedException, ExecutionException {
        R result = null;
        Future<Future<R>> future = getFutureFuture();

        try {
            executionContext.check();
        } catch (org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException te) {
            throw new ExecutionException(te);
        }

        try {
            result = future.get().get();
        } catch (InterruptedException | CancellationException e) {
            //if the future was interrupted or cancelled, check if it was because the FT Timeout popped
            try {
                executionContext.check();
            } catch (org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException te) {
                throw new ExecutionException(te);
            }

            throw e;
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore({ CancellationException.class, org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException.class })
    public R get(long methodTimeout, TimeUnit methodUnit) throws InterruptedException, ExecutionException, TimeoutException {
        R result = null;
        Future<Future<R>> future = getFutureFuture();

        try {
            executionContext.check();
        } catch (org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException te) {
            throw new ExecutionException(te);
        }

        try {
            result = future.get(methodTimeout, methodUnit).get(methodTimeout, methodUnit); //TODO do both get calls need timeout?
        } catch (InterruptedException | CancellationException e) {
            //if the future was interrupted or cancelled, check if it was because the FT Timeout popped
            try {
                executionContext.check();
            } catch (org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException te) {
                throw new ExecutionException(te);
            }

            throw e;
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
            //apply the JEE contexts to the thread before calling the inner task
            contextAppliedToThread = this.threadContext.taskStarting();
        }
        try {
            result = innerTask.call();
        } finally {
            if (contextAppliedToThread != null) {
                //remove the JEE contexts again since the thread will be re-used
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
     * @param executorService
     */
    @FFDCIgnore({ RejectedExecutionException.class })
    public void start(ExecutorService executorService) {
        synchronized (this) {
            executionContext.start(this);
            try {
                Future<Future<R>> futureFuture = executorService.submit(this);
                this.futureFuture = futureFuture;
            } catch (RejectedExecutionException e) {
                executionContext.end();
                throw new BulkheadException(Tr.formatMessage(tc, "bulkhead.no.threads.CWMFT0001E", executionContext.getMethod()), e);
            }
        }
    }
}
