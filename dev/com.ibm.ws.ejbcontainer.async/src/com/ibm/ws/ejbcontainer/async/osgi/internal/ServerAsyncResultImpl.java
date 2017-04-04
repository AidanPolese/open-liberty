/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.async.osgi.internal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ibm.ejs.container.ServerAsyncResult;
import com.ibm.ws.ejbcontainer.EJBPMICollaborator;

/**
 * An implementation of {@link ServerAsyncResult) that supports asynchronous work submitted to an {@link ExecutorService}.
 */
public class ServerAsyncResultImpl extends ServerAsyncResult {
    /**
     * The submitted task future that may be used to cancel this request.
     */
    public Future<?> ivTaskFuture;

    /**
     * True if some thread has begun running or has cancelled the task.
     */
    private final AtomicBoolean runOrCancel = new AtomicBoolean();

    public ServerAsyncResultImpl(EJBPMICollaborator pmiBean) {
        super(pmiBean);
    }

    /**
     * Returns true if the caller should run or cancel the task, or false if
     * another thread has already begun running or has cancelled the task.
     */
    boolean runOrCancel() {
        return runOrCancel.compareAndSet(false, true);
    }

    @Override
    protected boolean doCancel() {
        // Future.cancel from ExecutorService returns true while the task is
        // running, but the EJB spec requires Future.cancel to return false
        // unless the task is cancelled before running.  If runOrCancel()
        // succeeds, then we have effectively cancelled the task; we still
        // attempt to remove the underlying Future from the ExecutorService's
        // queue, but it doesn't matter if that succeeds.
        if (runOrCancel()) {
            // EJB specification does not allow a running async method to be
            // interrupted, so always pass 'false' to cancel. An attempt to cancel
            // may be visible to the task via the SessionContext, but that is
            // handled by the caller.
            ivTaskFuture.cancel(false);
            return true;
        }
        return false;
    }

}
