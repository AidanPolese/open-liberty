/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

/**
 * The extended interface between the EJB container and the runtime environment
 * that has scheduled an asynchronous method to be invoked from a remote
 * client.
 */
public interface RemoteAsyncResultExtended
                extends RemoteAsyncResult
{
    /**
     * Attempts to wait for the computation to complete, and then retrieves its
     * result. This method is similar to Future.get, but the implementation
     * might decide to wait for less than the specified number of milliseconds.
     * If a result is returned, it will be returned in the 0th element of the
     * returned array. If the server aborted the wait before a result was
     * available and before the wait time was reached, then null is returned,
     * and the caller must retry the request after adjusting the wait time.
     * 
     * @param waitTime the number of milliseconds to wait, or less than or equal
     *            to 0 to wait indefinitely
     * @return an array with the 0th element containing the result, or null if
     *         a result is not ready and the wait time was not reached
     */
    Object[] waitForResult(long waitTime)
                    throws ExecutionException, InterruptedException, RemoteException;
}
