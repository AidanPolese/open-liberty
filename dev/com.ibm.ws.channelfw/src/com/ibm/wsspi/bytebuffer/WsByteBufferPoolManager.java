//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 09/21/04 gilgen      233448          Add copyright statement and change history.
// 05/21/05 bgower      LI3187          Add ByteBuffer wrapper
// 04/30/08 wigger      515681          FileChannel buffer code
// 04/30/08 wigger      515681.1        FileChannel buffer code review changes

package com.ibm.wsspi.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This interface contains methods for obtaining WsByteBuffer objects either
 * from a pool of WsByteBuffer objects or derived from an existing WsBteBuffer
 * object
 * 
 */
public interface WsByteBufferPoolManager {

    /**
     * name for Pool Manager as bound to JNDI
     */
    String LOCAL_JNDI_SERVICE_NAME = "websphere/WsByteBufferPoolManager";

    /**
     * Name to use then looking up the Channel Framework from the global
     * "services:" JNDI namespace.
     */
    String JNDI_SERVICE_NAME = "services:" + LOCAL_JNDI_SERVICE_NAME;

    /**
     * Allocate a buffer from a buffer pool. Choose the buffer pool which is
     * closest to the desired size, but not less than the desired size. The
     * underlying ByteBuffer that is allocated will be a non-Direct ByteBuffer.
     * To release this buffer back to the pool, call the release() method on the
     * WsByteBuffer (not the WsByteBufferPoolManager). For every allocate,
     * duplicate, slice, a release needs to be called before the WsByteBuffer
     * will be released back to the pool.
     * 
     * @param entrySize
     *            the amount of memory be requested for this allocation
     * @return WsByteBuffer buffer which can now be used.
     */
    WsByteBuffer allocate(int entrySize);

    /**
     * allocate a buffer from a buffer pool. Choose the buffer pool which is
     * closest to the desired size, but not less than the desired size. The
     * underlying ByteBuffer that is allocated will be a Direct ByteBuffer. To
     * release this buffer back to the pool, call the release() method on the
     * WsByteBuffer (not the WsByteBufferPoolManager). For every allocateDirect,
     * duplicate, slice, a release needs to be called before the WsByteBuffer
     * will be released back to the pool.
     * 
     * @param entrySize
     *            the amount of memory be requested for this allocation
     * @return WsByteBuffer buffer which can now be used.
     */
    WsByteBuffer allocateDirect(int entrySize);

    /**
     * @param fc
     *            FileChannel which will be used to create a new WsByteBuffer
     * @return a new WsByteBuffer buffer which was created using the passed in
     *         FileChannel
     */
    WsByteBuffer allocateFileChannelBuffer(FileChannel fc);

    /**
     * Wraps a byte array into a WsByteBuffer.
     * 
     * The new buffer will be backed by the given byte array. The new buffer's
     * capacity and limit will be the array.length, it position will be zero,
     * and its mark will be undefined.
     * 
     * Pooling of this WsByteBuffer will not be done.
     * 
     * @param array
     *            The array that will back this buffer
     * @return WsByteBuffer the new buffer
     */
    WsByteBuffer wrap(byte[] array);

    /**
     * Wraps a byte array into a WsByteBuffer. The new buffer will be backed by
     * the given byte array. The new buffer's capacity will be the array.length,
     * its position will be offset, its limit will be offset + limit, and its
     * mark will be undefined.
     * 
     * Pooling of this WsByteBuffer will not be done.
     * 
     * @param array
     *            The array that will back this buffer
     * @param offset
     *            The offset of the subarray to be used; must be non-negative
     *            and no larger than array.length. The new buffer's position
     *            will be to this value.
     * @param length
     *            The length of the subarray to be used; must be non-negative
     *            and no larger than array.length - offset. The new buffer's
     *            limit will be set to offset + length.
     * @return WsByteBuffer the new buffer
     * @throws IndexOutOfBoundsException
     * 
     */
    WsByteBuffer wrap(byte[] array, int offset, int length) throws IndexOutOfBoundsException;

    /**
     * Wraps a java.nio.ByteBuffer into a WsByteBuffer.
     * 
     * Pooling of this WsByteBuffer will not be done.
     * 
     * @param buffer
     *            The buffer to wrap; must be non-null
     * @return WsByteBuffer the new buffer
     * 
     * 
     */
    WsByteBuffer wrap(ByteBuffer buffer); // @LI3187A

    /**
     * Duplicate a WsByteBuffer. This will mainly consist of: 1. Creating a new
     * WsByteBuffer 2. wrapping into this WsByteBuffer the ByteBuffer returned
     * on the duplicate() method of the ByteBuffer wrapped by the passed
     * WsByteBuffer 3. Updating the control variables of the new WsByteBuffer
     * (such setting the ID to be the same as that of the passed WsByteBuffer)
     * 
     * To release this buffer back to the pool, call the release() method on the
     * WsByteBuffer. For every allocate, allocateDirect, duplicate, slice, a
     * release needs to be called before the WsByteBuffer will be released back
     * to the pool.
     * 
     * @param oWsByteBuffer
     * @return WsByteBuffer
     */
    WsByteBuffer duplicate(WsByteBuffer oWsByteBuffer);

    /**
     * Slice a WsByteBuffer. This will mainly consist 1. Creating a new
     * WsByteBuffer 2. wrapping into this WsByteBuffer the ByteBuffer returned
     * on the duplicate() method of the ByteBuffer wrapped by the passed
     * WsByteBuffer 3. Updating the control variables of the new WsByteBuffer
     * (such setting the ID to be the same as that of the passed WsByteBuffer)
     * 
     * To release this buffer back to the pool, call the release() method on the
     * WsByteBuffer. For every allocate, allocateDirect, duplicate, slice, a
     * release needs to be called before the WsByteBuffer will be released back
     * to the pool.
     * 
     * @param oWsByteBuffer
     * @return WsByteBuffer
     */
    WsByteBuffer slice(WsByteBuffer oWsByteBuffer);

}
