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

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import org.eclipse.microprofile.faulttolerance.exceptions.ExecutionException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.faulttolerance.spi.BulkheadPolicy;

/**
 *
 */
public class SemaphoreExecutor<R> implements InternalExecutor<Callable<R>, R> {

    private final Semaphore semaphore;

    public SemaphoreExecutor(BulkheadPolicy bulkheadPolicy) {
        if (bulkheadPolicy == null) {
            this.semaphore = null;
        } else {
            this.semaphore = new Semaphore(bulkheadPolicy.getMaxThreads());
        }
    }

    @Override
    @FFDCIgnore({ TimeoutException.class, Exception.class })
    public R execute(Callable<R> callable, Timeout timeout) throws InterruptedException {
        R result = null;
        if (timeout != null) {
            timeout.start(Thread.currentThread());
        }
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
                if (timeout != null) {
                    timeout.stop(true);
                }
            }
        } catch (InterruptedException | TimeoutException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }

        return result;
    }

}
