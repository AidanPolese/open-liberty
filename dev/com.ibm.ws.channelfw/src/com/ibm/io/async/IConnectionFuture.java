// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2005, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 08/31/05 gilgen      LIDB3618-2      M2/M3 drops 
// 09/01/05 gilgen      302453          M3 code updates

package com.ibm.io.async;

import java.io.IOException;

/**
 * A future object for client socket connection operations.
 */
public interface IConnectionFuture extends IAbstractAsyncFuture {

    /**
     * Waits indefinitely for the asynchronous socket connection to complete.
     * <p>
     * This method is the equivalent of the generic {@link IAbstractAsyncFuture#waitForCompletion()}waiting method, except that this method
     * may throw an <code>IOException</code> if a problem occurs on the connection.
     * </p>
     * 
     * @throws InterruptedException
     *             the waiting thread was interrupted.
     * @throws IOException
     *             the operation completed, but failed with an IOException
     */
    public void complete() throws InterruptedException, IOException;

    /**
     * Waits for the given period of time for the asynchronous connection operation to complete.
     * <p>
     * This method is the equivalent of the generic {@link IAbstractAsyncFuture#waitForCompletion(long)}waiting method, except that this
     * method may throw an <code>IOException</code> if a problem occurs on the connection.
     * </p>
     * 
     * @param timeout
     *            the time to wait for completion, in milliseconds
     * @throws InterruptedException
     *             the thread was interrupted while waiting for the operation to complete
     * @throws AsyncTimeoutException
     *             the wait timed out before the operation completed
     * @throws IOException
     *             the operation completed, but failed with an IOException
     */
    public void complete(long timeout) throws InterruptedException, IOException;
}