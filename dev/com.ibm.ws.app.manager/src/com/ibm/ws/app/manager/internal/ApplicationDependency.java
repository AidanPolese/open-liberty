/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.internal;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.threading.FutureMonitor;
import com.ibm.ws.threading.listeners.CompletionListener;

/**
 * Wrapper for a Future<Boolean> that also provided diagnostics to show
 * what that future represents.
 */
@Trivial
public final class ApplicationDependency implements CompletionListener<Boolean> {
    private final FutureMonitor futureMonitor;
    private final Future<Boolean> future;
    private final String desc;
    private final int seqNo;

    private static final AtomicInteger _appDepCounter = new AtomicInteger();

    public ApplicationDependency(FutureMonitor futureMonitor, String desc) {
        this(futureMonitor, futureMonitor.createFuture(Boolean.class), desc);
    }

    public ApplicationDependency(FutureMonitor futureMonitor, Future<Boolean> future, String desc) {
        this.futureMonitor = futureMonitor;
        this.future = future;
        this.desc = desc;
        this.seqNo = _appDepCounter.getAndIncrement();
    }

    public Future<Boolean> getFuture() {
        return future;
    }

    public void setResult(boolean result) {
        futureMonitor.setResult(future, result);
    }

    public void setResult(Throwable t) {
        futureMonitor.setResult(future, t);
    }

    public void onCompletion(CompletionListener<Boolean> completionListener) {
        futureMonitor.onCompletion(future, completionListener);
    }

    @Override
    public String toString() {
        return "AppDep[" + seqNo + "]: desc=\"" + desc + "\", future=" + future;
    }

    public boolean isDone() {
        return future.isDone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.threading.listeners.CompletionListener#successfulCompletion(java.util.concurrent.Future, java.lang.Object)
     */
    @Override
    public void successfulCompletion(Future<Boolean> future, Boolean result) {
        setResult(result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.threading.listeners.CompletionListener#failedCompletion(java.util.concurrent.Future, java.lang.Throwable)
     */
    @Override
    public void failedCompletion(Future<Boolean> future, Throwable t) {
        setResult(t);
    }
}
