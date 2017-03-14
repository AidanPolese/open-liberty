/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.core.utils.internal;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/** Test implementation of DirectBufferHelper. */
class TestBufferHelper extends DirectBufferHelperImpl {

    Map<Long, ByteBuffer> buffers = new HashMap<Long, ByteBuffer>();

    public TestBufferHelper() {
        super();
    }

    public TestBufferHelper(Map<Long, ByteBuffer> buffers) {
        super();
        this.buffers = buffers;
    }

    public void addBuffer(long address, ByteBuffer testBuffer) {
        buffers.put(address, testBuffer);
    }

    @Override
    protected ByteBuffer mapDirectByteBuffer(long address, int size) {
        return buffers.get(address).asReadOnlyBuffer();
    }

    public Map<BufferKey, ByteBuffer> getSegments() {
        return segments.get();
    }

    public BufferHolder getRecentBuffer() {
        return recentBuffer.get();
    }
}