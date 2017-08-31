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
package com.ibm.ws.microprofile.faulttolerance.impl.sync;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.exceptions.ExecutionException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskContext;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;

/**
 *
 */
public class SemaphoreTaskRunner<R> implements TaskRunner<R> {

    private final Semaphore semaphore;

    public SemaphoreTaskRunner(BulkheadPolicy bulkheadPolicy) {
        if (bulkheadPolicy == null) {
            this.semaphore = null;
        } else {
            this.semaphore = new Semaphore(bulkheadPolicy.getMaxThreads());
        }
    }

    @Override
    @FFDCIgnore({ TimeoutException.class, Exception.class })
    public R runTask(Callable<R> callable, ExecutionContext executionContext, TaskContext taskContext) throws InterruptedException {
        R result = null;
        taskContext.start();
        try {
            if (this.semaphore != null) {
                this.semaphore.acquire();
            }
            try {
                result = callable.call();
            } finally {
                if (this.semaphore != null) {
                    this.semaphore.release();
                }
            }
        } catch (InterruptedException | TimeoutException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecutionException(e);
        } finally {
            taskContext.end();
        }

        return result;
    }

}
