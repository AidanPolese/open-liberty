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

import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.faulttolerance.impl.ExecutionContextImpl;
import com.ibm.ws.microprofile.faulttolerance.impl.FTConstants;
import com.ibm.ws.microprofile.faulttolerance.impl.TaskRunner;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;

/**
 *
 */
public class SemaphoreTaskRunner<R> implements TaskRunner<R> {

    private static final TraceComponent tc = Tr.register(SemaphoreTaskRunner.class);

    private final Semaphore semaphore;

    public SemaphoreTaskRunner(BulkheadPolicy bulkheadPolicy) {
        if (bulkheadPolicy == null) {
            this.semaphore = null;
        } else {
            this.semaphore = new Semaphore(bulkheadPolicy.getMaxThreads());
        }
    }

    @Override
    @FFDCIgnore({ InterruptedException.class })
    public R runTask(Callable<R> callable, ExecutionContextImpl executionContext) throws Exception {
        R result = null;
        executionContext.start();
        try {
            if (this.semaphore != null) {
                boolean acquired = this.semaphore.tryAcquire();
                if (!acquired) {
                    throw new BulkheadException(Tr.formatMessage(tc, "bulkhead.no.threads.CWMFT0001E", executionContext.getMethod()));
                }
            }
            try {
                executionContext.check();
                result = callable.call();
            } finally {
                if (this.semaphore != null) {
                    this.semaphore.release();
                }
            }
        } catch (InterruptedException e) {
            //if the interrupt was caused by a timeout then check and throw that instead
            long remaining = executionContext.check();
            FTConstants.debugTime(tc, "Task Interrupted", remaining);
            throw e;
        } finally {
            executionContext.end();
        }

        return result;
    }

}
