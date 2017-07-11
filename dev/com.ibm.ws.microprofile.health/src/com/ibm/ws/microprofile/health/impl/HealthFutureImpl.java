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
package com.ibm.ws.microprofile.health.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

//just a wrapper to add trace
public class HealthFutureImpl<T> implements Future<T> {

    private static final TraceComponent tc = Tr.register(HealthFutureImpl.class);

    Future<T> _futureFS = null;

    public HealthFutureImpl(Future<T> futureFS) {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "Construct an HealthFutureImpl to wrap " + futureFS);
        _futureFS = futureFS;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean result = false;
        if (_futureFS != null) {
            result = _futureFS.cancel(mayInterruptIfRunning);
        }
        if (tc.isDebugEnabled())
            Tr.debug(tc, "cancel FS future " + _futureFS + ", with result " + result);
        return result;
    }

    @Override
    public boolean isCancelled() {
        boolean result = false;
        if (_futureFS != null) {
            result = _futureFS.isCancelled();
        }
        if (tc.isDebugEnabled())
            Tr.debug(tc, "isCancelled for FS future " + _futureFS + ", with result " + result);
        return result;
    }

    @Override
    public boolean isDone() {
        boolean result = false;
        if (_futureFS != null) {
            result = _futureFS.isDone();
        }
        if (tc.isDebugEnabled())
            Tr.debug(tc, "isDone for FS future " + _futureFS + ", with result " + result);
        return result;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        T theReturn = null;
        if (_futureFS != null)
            theReturn = _futureFS.get();
        return theReturn;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        T theReturn = null;
        if (_futureFS != null)
            theReturn = _futureFS.get(timeout, unit);
        return theReturn;
    }

}
