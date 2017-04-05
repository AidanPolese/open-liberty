/* ========================================================================
 * @(#) 1.1 SERV1/ws/code/utils/src/com/ibm/jtc/adapter/IPlatformAdapter.java, WAS.runtime, WAS80.SERV1, kk1041.02 5/21/10 14:32:55 [10/22/10 00:46:49]
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * ========================================================================
 *
 * HISTORY
 * ~~~~~~~
 *
 * Change ID    Author    Abstract
 * ---------    --------  ---------------------------------------------------
 * D652960      andymc    New file
 * ======================================================================== */

package com.ibm.jtc.adapter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Interface defining methods used to access internals of the
 * Java class libraries.
 */
public interface IPlatformAdapter {
    /**
     * Get the native address for the specified DirectByteBuffer
     * 
     * @param byteBuffer
     *            Reference to a DirectByteBuffer
     * 
     * @return The native address of the specified DirectByteBuffer
     * 
     * @throws IllegalArgumentException
     *             If the specified buffer is not direct
     */
    long getByteBufferAddress(ByteBuffer byteBuffer);

    /**
     * Get the socket channel handle.
     * 
     * @param socketChannel
     *            The socket channel to get the handle from.
     * 
     * @return The handle for the specified socket channel
     */
    long getSocketChannelHandle(SocketChannel socketChannel);

    /**
     * Clean up the thread locals for the specified Thread.
     * 
     * @param thread
     *            The thread to clean up
     */
    void cleanThreadLocals(Thread thread);
}
