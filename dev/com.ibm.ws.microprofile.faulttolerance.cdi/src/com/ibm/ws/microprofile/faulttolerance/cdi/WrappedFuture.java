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
package com.ibm.ws.microprofile.faulttolerance.cdi;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class WrappedFuture implements Future<Object> {

    private final Future<?> future;

    public WrappedFuture(Future<?> future) {
        this.future = future;
    }

    /** {@inheritDoc} */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDone() {
        return future.isDone();
    }

    /** {@inheritDoc} */
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        Future<?> wrapped = (Future<?>) future.get();
        return wrapped.get();
    }

    /** {@inheritDoc} */
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Future<?> wrapped = (Future<?>) future.get(timeout, unit);
        return wrapped.get(timeout, unit);
    }

}
