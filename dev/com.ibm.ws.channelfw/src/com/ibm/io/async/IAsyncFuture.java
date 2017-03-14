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
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;

/**
 * An <code>IAsyncFuture</code> is a future which represents an asynchronous scatter or gather IO operation on an asynchronous channel.
 * <p>
 * The interface defines methods that relate to the completion of the operation represented by the future.
 * <p>
 * When an asynchronous scatter or gather operation is requested, an <code>IAsyncFuture</code> object is immediately returned which
 * represents the operation concerned. The calling application can use the <code>IAsyncFuture</code> to obtain information about the
 * completion of the operation.
 * <p>
 * There are three methods by which the calling application can get information about the completion of the operation:
 * <ul>
 * <li>polling : using the {@link #isCompleted()}method, that will return true when the operation finally completes.</li>
 * <li>blocking: using the {@link #waitForCompletion()}or {@link #waitForCompletion(long)}methods, that will block indefinitely or for a
 * limited period of time respectively. The blocking thread is woken when the future completes.</li>
 * <li>callback : by registering a listener using {@link #addCompletionListener(ICompletionListener, Object)}the caller can provider a
 * listener that will be called back when the future completes. The completed future and a user state <code>Object</code> are passed as
 * arguments to the callback method.</li>
 * </ul>
 * <p>
 * <code>IAsyncFuture</code> has methods that relate to the buffers involved in the operation, and the number of bytes written or
 * read.
 */
public interface IAsyncFuture extends IAbstractAsyncFuture {
    /**
     * Gets the byte buffer array associated with the IO operation.
     * <p>
     * None of the byte buffers in the array should be read or modified until the associated operation is complete.
     * </p>
     * 
     * @return The byte buffer array associated with the IO operation.
     */
    public ByteBuffer[] getBuffers();

    /**
     * Gets the JIT allocated byte buffer array associated with the IO operation.
     * 
     * </p>
     * 
     * @return The JIT byte buffer array associated with the IO operation.
     */
    public WsByteBuffer getJITBuffer();

    /**
     * Returns the number of bytes read/written in the operation, or zero if none. If the future is completed, the call returns immediately,
     * otherwise the call blocks until the operation completes. If the operation fails in some way an exception is thrown.
     * 
     * @return The number of bytes read/written in this operation.
     * @throws InterruptedException
     *             if the calling thread was interrupted during its wait for the operation to complete.
     * @throws IOException
     *             if the operation failed
     * @throws AsyncTimeoutException
     *             if the underlying operation was initiated with a timeout set and the timeout expired before the operation completed. The
     *             operation has been cancelled.
     * @throws ClosedChannelException
     *             if the channel is closed during the multi IO operation when the multi IO operation is not supported by the underyling OS.
     */
    public long getByteCount() throws InterruptedException, IOException;

    /**
     * Returns the number of bytes read/written in the operation, or zero if none. If the future is completed, the call returns immediately,
     * otherwise the call blocks for at least the given number of milliseconds. If the call has not completed within the timeout period an
     * <code>AsyncTimeoutException</code> is thrown. If the operation fails in some way an exception is thrown.
     * 
     * @param timeout
     *            the maximum time to wait in milliseconds. Specifying 0L means wait forever.
     * @return The number of bytes read/written in this operation.
     * @throws InterruptedException
     *             if the calling thread was interrupted during it's wait for the operation to complete.
     * @throws AsyncTimeoutException
     *             if the call did not complete within the given timeout period. The operation is still underway.
     * @throws IOException
     *             if the operation failed
     * @throws ClosedChannelException
     *             if the channel is closed during the multi IO operation when the multi IO operation is not supported by the underyling OS.
     */
    public long getByteCount(long timeout) throws InterruptedException, IOException;
}