/*
 * @start_prolog@
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 * 
 * Issue       Date        Name        Description
 * ----------- ----------- --------    ------------------------------------
 */
package com.ibm.ws.threading.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class ImmediateFutureImpl<T> implements Future<T> {
    private T _result;
    private ExecutionException _exception;

    public ImmediateFutureImpl(T result) {
        _result = result;
    }

    public ImmediateFutureImpl(Throwable t) {
        _exception = new ExecutionException(t);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public T get() throws ExecutionException {
        if (_exception != null) {
            throw _exception;
        } else {
            return _result;
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws ExecutionException {
        return get();
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }
}